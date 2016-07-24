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
package net.hasor.restful.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.restful.RestfulContext;
import net.hasor.restful.api.HttpMethod;
import net.hasor.restful.api.MappingTo;
import org.more.UndefinedException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class MappingToDefine {
    private Class<?>            targetType;
    private Provider<?>         targetProvider;
    private String              mappingTo;
    private String              mappingToMatches;
    private Map<String, Method> httpMapping;
    private AtomicBoolean inited = new AtomicBoolean(false);
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
        //
        this.httpMapping = new HashMap<String, Method>();
        List<Method> mList = BeanUtils.getMethods(targetType);
        if (mList != null && !mList.isEmpty()) {
            for (Method targetMethod : mList) {
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
                /*default*/
                if (targetMethod.getName().equals("execute") && !this.httpMapping.containsKey("execute")) {
                    this.httpMapping.put(HttpMethod.ANY, targetMethod);
                }
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
     * @param httpReq
     * @param httpResp
     * @throws Throwable 异常抛出
     */
    public final void invoke(HttpServletRequest httpReq, HttpServletResponse httpResp, RestfulContext context) throws Throwable {
        String httpMethod = httpReq.getMethod();
        Method targetMethod = this.httpMapping.get(httpMethod.trim().toUpperCase());
        if (targetMethod == null) {
            targetMethod = this.httpMapping.get(HttpMethod.ANY);
        }
        //
        Hasor.assertIsNotNull(targetMethod, "not font mapping Method.");
        InvContext invokerContext = new InvContext(this, targetMethod, context);
        invokerContext.initParams(httpReq, httpResp);
        new Invoker().exeCall(this.targetProvider, invokerContext);
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}