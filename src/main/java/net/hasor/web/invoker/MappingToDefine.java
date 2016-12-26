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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.web.DataContext;
import net.hasor.web.annotation.Async;
import net.hasor.web.annotation.HttpMethod;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.valid.ValidProcessor;
import org.more.UndefinedException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class MappingToDefine {
    private Class<?>                    targetType;
    private Provider<?>                 targetProvider;
    private String                      mappingTo;
    private String                      mappingToMatches;
    private Map<String, Method>         httpMapping;
    private Map<Method, ValidProcessor> needValid;
    private Set<Method>                 asyncMethod;
    private AsyncSupported defaultAsync = AsyncSupported.no;
    private AtomicBoolean  inited       = new AtomicBoolean(false);
    //
    protected MappingToDefine(Class<?> targetType) {
        this.targetType = targetType;
        MappingTo pathAnno = targetType.getAnnotation(MappingTo.class);
        if (pathAnno == null) {
            throw new UndefinedException("is not a valid Mapping Service.");
        }
        String servicePath = pathAnno.value();
        if (StringUtils.isBlank(servicePath)) {
            throw new NullPointerException("Service path is empty.");
        }
        if (!servicePath.matches("/.+")) {
            throw new IllegalStateException("Service path format error");
        }
        if (targetType.getAnnotation(Async.class) != null) {
            this.defaultAsync = AsyncSupported.yes;
        }
        //
        this.httpMapping = new HashMap<String, Method>();
        this.asyncMethod = new HashSet<Method>();
        List<Method> mList = BeanUtils.getMethods(targetType);
        if (mList != null && !mList.isEmpty()) {
            for (Method targetMethod : mList) {
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
            }
        }
        //
        // .执行调用，每个方法的参数都进行判断，一旦查到参数上具有Valid 标签那么就调用doValid进行参数验证。
        this.needValid = new HashMap<Method, ValidProcessor>();
        for (String key : this.httpMapping.keySet()) {
            Method targetMethod = this.httpMapping.get(key);
            //
            this.needValid.put(targetMethod, new ValidProcessor(targetMethod));
            //
            // @Async
            if (targetMethod.getAnnotation(Async.class) != null) {
                asyncMethod.add(targetMethod);
            }
        }
        //
        this.mappingTo = servicePath;
        this.mappingToMatches = servicePath.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
    }
    //
    //
    /**@return 获取映射的地址*/
    public String getMappingTo() {
        return this.mappingTo;
    }
    public String getMappingToMatches() {
        return this.mappingToMatches;
    }
    /**
     * 判断是否要求异步处理请求。
     */
    public AsyncSupported isAsync(String httpMethod, String requestPath) {
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        if (!requestPath.matches(this.mappingToMatches)) {
            return this.defaultAsync;
        }
        Method targetMethod = null;
        for (String m : this.httpMapping.keySet()) {
            if (StringUtils.equals(httpMethod, m)) {
                targetMethod = this.httpMapping.get(m);
                break;
            } else if (StringUtils.equals(m, HttpMethod.ANY)) {
                targetMethod = this.httpMapping.get(HttpMethod.ANY);
                break;
            }
        }
        //
        if (targetMethod == null) {
            return this.defaultAsync;
        }
        if (this.asyncMethod.contains(targetMethod)) {
            return AsyncSupported.yes;
        } else {
            return this.defaultAsync;
        }
    }
    /**
     * 首先测试路径是否匹配，然后判断Restful实例是否支持这个 请求方法。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(String httpMethod, String requestPath) {
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        if (!requestPath.matches(this.mappingToMatches)) {
            return false;
        }
        for (String m : this.httpMapping.keySet()) {
            if (StringUtils.equals(httpMethod, m)) {
                return true;
            } else if (StringUtils.equals(m, HttpMethod.ANY)) {
                return true;
            }
        }
        return false;
    }
    //
    /** 执行初始化 */
    protected void init(final AppContext appContext) {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        Hasor.assertIsNotNull(appContext, "appContext is null.");
        this.targetProvider = new Provider<Object>() {
            public Object get() {
                return appContext.getInstance(targetType);
            }
        };
    }
    /**
     * 调用目标
     * @throws Throwable 异常抛出
     */
    public final void invoke(DataContext dataContext) throws ServletException, IOException {
        String httpMethod = dataContext.getHttpRequest().getMethod();
        Method targetMethod = this.httpMapping.get(httpMethod.trim().toUpperCase());
        if (targetMethod == null) {
            targetMethod = this.httpMapping.get(HttpMethod.ANY);
        }
        //
        try {
            Hasor.assertIsNotNull(targetMethod, "not font mapping Method.");
            ValidProcessor needValid = this.needValid.get(targetMethod);
            new InvokerSSS(this, dataContext).exeCall(this.targetProvider, targetMethod, needValid);
        } catch (Throwable target) {
            if (target instanceof ServletException)
                throw (ServletException) target;
            if (target instanceof IOException)
                throw (IOException) target;
            if (target instanceof RuntimeException)
                throw (RuntimeException) target;
            throw new ServletException(target);
        }
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}