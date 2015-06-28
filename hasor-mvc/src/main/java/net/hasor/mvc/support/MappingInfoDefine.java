/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.mvc.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.mvc.MappingInfo;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.WebCall;
import net.hasor.mvc.WebCallInterceptor;
import net.hasor.mvc.api.HttpMethod;
import net.hasor.mvc.api.MappingTo;
import org.more.UndefinedException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class MappingInfoDefine implements MappingInfo {
    private Class<? extends ModelController> targetType;
    private Provider<ModelController>        targetProvider;
    private String                           mappingTo;
    private String                           mappingToMatches;
    private Map<String, MethodInfo>          httpMapping;
    private AtomicBoolean                    inited = new AtomicBoolean(false);
    //
    protected MappingInfoDefine(Class<? extends ModelController> targetType) {
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
        this.httpMapping = new HashMap<String, MethodInfo>();
        List<Method> mList = BeanUtils.getMethods(targetType);
        if (mList != null && !mList.isEmpty()) {
            for (Method targetMethod : mList) {
                /*HttpMethod*/
                Annotation[] annos = targetMethod.getAnnotations();
                if (annos != null) {
                    for (Annotation anno : annos) {
                        HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                        if (httpMethodAnno != null) {
                            String bindMethod = httpMethodAnno.value();
                            if (StringUtils.isBlank(bindMethod) == false) {
                                this.httpMapping.put(bindMethod.toUpperCase(), new MethodInfo(targetMethod));
                            }
                        }
                    }
                }
                /*default*/
                if (targetMethod.getName().equals("execute") && !this.httpMapping.containsKey("execute")) {
                    this.httpMapping.put(HttpMethod.ANY, new MethodInfo(targetMethod));
                }
            }
        }
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
        if (requestPath.matches(this.mappingToMatches) == false) {
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
    //
    //
    /**
     * 执行初始化
     * @param appContext appContext
     */
    protected void init(final AppContext appContext) {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        Hasor.assertIsNotNull(appContext, "appContext is null.");
        this.targetProvider = new Provider<ModelController>() {
            public ModelController get() {
                return appContext.getInstance(targetType);
            }
        };;
    }
    /**
     * 调用目标
     * @param call 执行策略
     * @param params 执行控制器时用到的参数。
     * @return 返回调用结果
     * @throws Throwable 异常抛出
     */
    public final Object invoke(final HttpInfo httpInfo, WebCallInterceptor[] callInterceptor) throws Throwable {
        String httpMethod = httpInfo.getHttpRequest().getMethod();
        MethodInfo methodInfo = this.httpMapping.get(httpMethod);
        if (methodInfo == null) {
            methodInfo = this.httpMapping.get(HttpMethod.ANY);
        }
        //
        Hasor.assertIsNotNull(methodInfo, "not font mapping Method.");
        final MethodInfo method = methodInfo;
        //
        final ModelController mc = this.targetProvider.get();
        final WebCall call = new WebCall() {
            public Method getMethod() {
                return method.targetMethod;
            }
            public Class<?>[] getParameterTypes() {
                return method.targetParamTypes;
            }
            public Annotation[][] getMethodParamAnnos() {
                return method.targetParamAnno;
            }
            public Annotation[] getAnnotations() {
                return method.targetMethodAnno;
            }
            public ModelController getTarget() {
                return mc;
            }
            public MappingInfo getMappingInfo() {
                return MappingInfoDefine.this;
            }
            public Object call(Object[] args) throws Throwable {
                try {
                    return method.targetMethod.invoke(mc, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
            public HttpServletRequest getHttpRequest() {
                return httpInfo.getHttpRequest();
            }
            public HttpServletResponse getHttpResponse() {
                return httpInfo.getHttpResponse();
            }
        };
        return new WebCallInvocation(call, callInterceptor).call(method.targetDefaultValues);
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //
    //
    //
    private static class MethodInfo {
        public Class<?>       targetType;
        public Method         targetMethod;
        public Class<?>[]     targetParamTypes;
        public Object[]       targetDefaultValues;
        public Annotation[][] targetParamAnno;
        public Annotation[]   targetMethodAnno;
        //
        public MethodInfo(Method targetMethod) {
            this.targetType = targetMethod.getDeclaringClass();
            this.targetMethod = targetMethod;
            this.targetParamTypes = targetMethod.getParameterTypes();
            this.targetParamAnno = targetMethod.getParameterAnnotations();
            this.targetMethodAnno = targetMethod.getAnnotations();
            //
            this.targetDefaultValues = new Object[this.targetParamTypes.length];
            for (int i = 0; i < this.targetParamTypes.length; i++) {
                Class<?> type = this.targetParamTypes[i];
                this.targetDefaultValues[i] = BeanUtils.getDefaultValue(type);
            }
        }
    }
}
