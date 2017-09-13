/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web.invoker;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Async;
import net.hasor.web.annotation.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class InMappingDef implements InMapping {
    private final long                index;
    private       BindInfo<?>         targetType;
    private       String              mappingTo;
    private       String              mappingToMatches;
    private       Map<String, Method> httpMapping;
    private       Set<Method>         asyncMethod;
    private AsyncSupported defaultAsync = AsyncSupported.no;
    //
    public InMappingDef(long index, BindInfo<?> targetType, String mappingTo, List<Method> methodList, boolean force) {
        this.index = index;
        this.targetType = Hasor.assertIsNotNull(targetType);
        String servicePath = Hasor.assertIsNotNull(mappingTo);
        if (StringUtils.isBlank(servicePath)) {
            throw new NullPointerException("'" + targetType.getBindType() + "' Service path is empty.");
        }
        if (!servicePath.matches("/.+")) {
            throw new IllegalStateException("'" + targetType.getBindType() + "' Service path format error, must be a '/' at the start.");
        }
        if (targetType.getBindType().getAnnotation(Async.class) != null) {
            this.defaultAsync = AsyncSupported.yes;
        }
        //
        this.mappingTo = servicePath;
        this.mappingToMatches = wildToRegex(servicePath).replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
        this.httpMapping = new HashMap<String, Method>();
        this.asyncMethod = new HashSet<Method>();
        //
        if (methodList == null || methodList.isEmpty()) {
            return;
        }
        //
        for (Method targetMethod : methodList) {
            // .HttpMethod
            Annotation[] annos = targetMethod.getAnnotations();
            if (annos != null) {
                for (Annotation anno : annos) {
                    HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                    if (httpMethodAnno != null) {
                        String bindMethod = httpMethodAnno.value();
                        if (!StringUtils.isBlank(bindMethod)) {
                            this.httpMapping.put(bindMethod.toUpperCase(), targetMethod);
                        }
                    }
                }
            }
            if (targetMethod.getName().equals("execute") && !this.httpMapping.containsKey("execute")) {
                this.httpMapping.put(HttpMethod.ANY, targetMethod);
            }
            // .Async
            if (targetMethod.getAnnotation(Async.class) != null) {
                this.asyncMethod.add(targetMethod);
            }
            //
            if (!this.httpMapping.containsValue(targetMethod) && force) {
                this.httpMapping.put(HttpMethod.ANY, targetMethod);
            }
        }
    }
    private static String wildToRegex(String wild) {
        //'\\', '$', '^', '[', ']', '(', ')', '{', '|', '+', '.'
        wild = wild.replace("\\", "\\\\"); // <-- 必须放在前面
        wild = wild.replace("$", "\\$");
        wild = wild.replace("^", "\\^");
        wild = wild.replace("[", "\\[");
        wild = wild.replace("]", "\\]");
        wild = wild.replace("(", "\\(");
        wild = wild.replace(")", "\\)");
        wild = wild.replace("|", "\\|");
        wild = wild.replace("+", "\\+");
        wild = wild.replace(".", "\\.");
        //
        wild = wild.replace("*", ".*");
        wild = wild.replace("?", ".");
        return wild;
    }
    //
    //
    @Override
    public BindInfo<?> getTargetType() {
        return this.targetType;
    }
    /** 获取映射的地址 */
    public String getMappingTo() {
        return this.mappingTo;
    }
    public String getMappingToMatches() {
        return this.mappingToMatches;
    }
    public long getIndex() {
        return index;
    }
    public Method[] getMethods() {
        return this.httpMapping.values().toArray(new Method[httpMapping.size()]);
    }
    //
    //
    /**
     * 首先测试路径是否匹配，然后判断Restful实例是否支持这个 请求方法。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(Invoker invoker) {
        String httpMethod = invoker.getHttpRequest().getMethod();
        String requestPath = invoker.getRequestPath();
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        if (!requestPath.matches(this.mappingToMatches)) {
            return false;
        }
        for (String m : this.httpMapping.keySet()) {
            if (httpMethod.equals(m)) {
                return true;
            } else if (HttpMethod.ANY.equals(m)) {
                return true;
            }
        }
        return false;
    }
    //
    /**
     * 调用目标
     * @throws Throwable 异常抛出
     */
    public final Method findMethod(final Invoker invoker) {
        String requestPath = invoker.getRequestPath();
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        if (!requestPath.matches(this.mappingToMatches)) {
            return null;
        }
        //
        String httpMethod = invoker.getHttpRequest().getMethod();
        Method targetMethod = this.httpMapping.get(httpMethod.trim().toUpperCase());
        if (targetMethod == null) {
            targetMethod = this.httpMapping.get(HttpMethod.ANY);
        }
        return targetMethod;
    }
    public boolean isAsync(Invoker invoker) {
        Method targetMethod = this.findMethod(invoker);
        if (targetMethod == null) {
            return false;
        }
        AsyncSupported async = this.asyncMethod.contains(targetMethod) ? AsyncSupported.yes : this.defaultAsync;
        return async == AsyncSupported.yes;
    }
    //
    @Override
    public Object newInstance(Invoker invoker) throws Throwable {
        return invoker.getAppContext().getInstance(getTargetType());
    }
    //
    @Override
    public String toString() {
        return String.format("pattern=%s ,methodSet=%s ,type %s", //
                this.mappingTo, StringUtils.join(this.httpMapping.keySet().toArray(), ","), this.getTargetType());
    }
}