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
package net.hasor.web.binder;
import net.hasor.core.BindInfo;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.Mapping;
import net.hasor.web.annotation.Async;
import net.hasor.web.annotation.HttpMethod;
import net.hasor.web.annotation.Produces;
import net.hasor.web.invoker.AsyncSupported;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * 一个请求地址只能是一个Action类进行处理，Action中的不同方法可以通过 @HttpMethod 等注解映射到 HTTP 协议中 GET、PUT 等行为上。
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingDef implements Mapping {
    private final int                 index;
    private final BindInfo<?>         targetType;
    private final String              mappingTo;
    private final String              mappingToMatches;
    private final Map<String, Method> httpMapping;
    private final Map<String, String> contentTypeMapping;
    private final Set<Method>         asyncMethod;
    private       AsyncSupported      defaultAsync = AsyncSupported.no;

    public MappingDef(int index, BindInfo<?> targetType, String mappingTo, Predicate<Method> methodMatcher) {
        this(index, targetType, mappingTo, methodMatcher, true);
    }

    public MappingDef(int index, BindInfo<?> targetType, String mappingTo, Predicate<Method> methodMatcher, boolean needAnno) {
        this.targetType = Objects.requireNonNull(targetType, "targetType is null.");
        if (StringUtils.isBlank(mappingTo)) {
            throw new NullPointerException("'" + targetType.getBindType() + "' Service path is empty.");
        }
        if (!mappingTo.matches("/.+")) {
            throw new IllegalStateException("'" + targetType.getBindType() + "' Service path format error, must be a '/' at the start.");
        }
        if (targetType.getBindType().getAnnotation(Async.class) != null) {
            this.defaultAsync = AsyncSupported.yes;
        }
        //
        this.index = index;
        this.mappingTo = mappingTo;
        this.mappingToMatches = wildToRegex(mappingTo).replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
        this.httpMapping = new HashMap<>();
        this.contentTypeMapping = new HashMap<>();
        this.asyncMethod = new HashSet<>();
        //
        List<Method> methodList = BeanUtils.getMethods(targetType.getBindType());
        for (Method targetMethod : methodList) {
            boolean matches = methodMatcher.test(targetMethod);
            if (!matches) {
                continue;
            }
            // .MetaType
            String metaType = null;
            Produces pro = targetMethod.getAnnotation(Produces.class);
            if (pro == null) {
                pro = targetMethod.getDeclaringClass().getAnnotation(Produces.class);
            }
            if (pro != null) {
                if (StringUtils.isBlank(pro.value())) {
                    throw new IllegalStateException(" @Produces value is empty. of " + targetMethod.toString());
                } else {
                    metaType = pro.value();
                }
            }
            // .HttpMethod
            Annotation[] annos = targetMethod.getAnnotations();
            if (annos != null) {
                for (Annotation anno : annos) {
                    if (anno instanceof HttpMethod) {
                        String[] methodSet = ((HttpMethod) anno).value();
                        for (String http : methodSet) {
                            this.httpMapping.put(http.toUpperCase(), targetMethod);
                            if (StringUtils.isNotBlank(metaType)) {
                                this.contentTypeMapping.put(http.toUpperCase(), metaType);
                            }
                        }
                    }
                    HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                    if (httpMethodAnno != null) {
                        String[] methodSet = httpMethodAnno.value();
                        for (String http : methodSet) {
                            this.httpMapping.put(http.toUpperCase(), targetMethod);
                            if (StringUtils.isNotBlank(metaType)) {
                                this.contentTypeMapping.put(http.toUpperCase(), metaType);
                            }
                        }
                    }
                }
            }
            // .Default (needAnno 为 true 表示，必须注释了 HttpMethod 注解的方法才可以被列为 Action)
            if (this.httpMapping.isEmpty() && !needAnno) {
                this.httpMapping.put(HttpMethod.ANY, targetMethod);
                if (StringUtils.isNotBlank(metaType)) {
                    this.contentTypeMapping.put(HttpMethod.ANY, metaType);
                }
            }
            // .Async
            if (targetMethod.getAnnotation(Async.class) != null) {
                this.asyncMethod.add(targetMethod);
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

    public int getIndex() {
        return index;
    }

    public String[] getHttpMethodSet() {
        return this.httpMapping.keySet().toArray(new String[httpMapping.size()]);
    }

    /**
     * 首先测试路径是否匹配，然后判断Restful实例是否支持这个 请求方法。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(HttpServletRequest request) {
        Objects.requireNonNull(request, "request is null.");
        //
        String httpMethod = request.getMethod();
        String requestPath = evalRequestPath(request);
        //
        if (!requestPath.matches(this.mappingToMatches)) {
            return false;
        }
        for (String m : this.httpMapping.keySet()) {
            if (httpMethod.equalsIgnoreCase(m)) {
                return true;
            } else if (HttpMethod.ANY.equalsIgnoreCase(m)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Method findMethod(String requestMethod) {
        Method targetMethod = this.httpMapping.get(requestMethod);
        if (targetMethod == null) {
            targetMethod = this.httpMapping.get(HttpMethod.ANY);
        }
        return targetMethod;
    }

    @Override
    public String getSpecialContentType(String requestMethod) {
        String specialMetaType = this.contentTypeMapping.get(requestMethod.toUpperCase());
        if (specialMetaType == null) {
            specialMetaType = this.contentTypeMapping.get(HttpMethod.ANY);
        }
        return specialMetaType;
    }

    public boolean isAsync(HttpServletRequest request) {
        Method targetMethod = this.findMethod(request);
        if (targetMethod == null) {
            return false;
        }
        AsyncSupported async = this.asyncMethod.contains(targetMethod) ? AsyncSupported.yes : this.defaultAsync;
        return AsyncSupported.yes == async;
    }

    public Map<String, Method> getHttpMapping() {
        return this.httpMapping;
    }

    @Override
    public String toString() {
        return String.format("pattern=%s ,methodSet=%s ,type %s", //
                this.mappingTo, StringUtils.join(this.httpMapping.keySet().toArray(), ","), this.getTargetType());
    }

    private String evalRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        return requestPath;
    }
}
