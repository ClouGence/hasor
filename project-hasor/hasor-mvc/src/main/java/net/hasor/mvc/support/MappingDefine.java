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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.mvc.Call;
import net.hasor.mvc.CallStrategy;
import net.hasor.mvc.MappingInfo;
import net.hasor.mvc.api.HttpMethod;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.api.ModelController;
import org.more.UndefinedException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class MappingDefine implements MappingInfo {
    private String                    bindID;
    private Provider<ModelController> targetProvider;
    private Method                    targetMethod;
    private Class<?>[]                targetParamTypes;
    private Annotation[][]            targetParamAnno;
    private Annotation[]              targetMethodAnno;
    private String                    mappingTo;
    private String                    mappingToMatches;
    private String[]                  httpMethod;
    private CallStrategy              callStrategy;
    private AtomicBoolean             inited = new AtomicBoolean(false);
    //
    protected MappingDefine(String bindID, Method targetMethod) {
        MappingTo pathAnno = targetMethod.getAnnotation(MappingTo.class);
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
        this.bindID = bindID;
        this.targetMethod = targetMethod;
        this.targetParamTypes = targetMethod.getParameterTypes();
        this.targetParamAnno = targetMethod.getParameterAnnotations();
        this.targetMethodAnno = targetMethod.getAnnotations();
        this.mappingTo = servicePath;
        this.mappingToMatches = servicePath.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
        //
        /*HttpMethod*/
        Annotation[] annos = targetMethod.getAnnotations();
        ArrayList<String> allHttpMethod = new ArrayList<String>();
        if (annos != null) {
            for (Annotation anno : annos) {
                HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                if (httpMethodAnno != null) {
                    String bindMethod = httpMethodAnno.value();
                    if (StringUtils.isBlank(bindMethod) == false)
                        allHttpMethod.add(bindMethod);
                }
            }
        }
        if (allHttpMethod.isEmpty())
            allHttpMethod.add("ANY");
        this.httpMethod = allHttpMethod.toArray(new String[allHttpMethod.size()]);
    }
    /**@return 获取映射的地址*/
    public String getMappingTo() {
        return this.mappingTo;
    }
    public String getMappingToMatches() {
        return this.mappingToMatches;
    }
    /**
     * 测试路径是否匹配
     * @param requestPath 要测试的路径。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(String requestPath) {
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        return requestPath.matches(this.mappingToMatches);
    }
    public String[] getHttpMethod() {
        return this.httpMethod;
    }
    /**判断Restful实例是否支持这个 请求方法。*/
    public boolean matchingMethod(String httpMethod) {
        for (String m : this.httpMethod) {
            if (StringUtils.equalsIgnoreCase(httpMethod, m)) {
                return true;
            } else if (StringUtils.equalsIgnoreCase(m, "ANY")) {
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
    protected void init(AppContext appContext) {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        Hasor.assertIsNotNull(appContext, "appContext is null.");
        BindInfo<ModelController> controllerInfo = appContext.getBindInfo(this.bindID);
        this.targetProvider = appContext.getProvider(controllerInfo);
    }
    /**
     * 调用目标
     * @param call 执行策略
     * @param params 执行控制器时用到的参数。
     * @return 返回调用结果
     * @throws Throwable 异常抛出
     */
    public final Object invoke(final HttpInfo httpInfo, CallStrategy callStrategy, Map<String, ?> params) throws Throwable {
        Hasor.assertIsNotNull(callStrategy);
        final ModelController mc = this.targetProvider.get();
        final Map<String, ?> atParams = (params == null) ? new HashMap<String, Object>() : params;
        final Call call = new Call() {
            public Set<String> getParamKeys() {
                return atParams.keySet();
            }
            public Object getParam(String key) {
                return atParams.get(key);
            }
            public Method getMethod() {
                return targetMethod;
            }
            public Class<?>[] getParameterTypes() {
                return targetParamTypes;
            }
            public Annotation[][] getMethodParamAnnos() {
                return targetParamAnno;
            }
            public Annotation[] getAnnotations() {
                return targetMethodAnno;
            }
            public ModelController getTarget() {
                return mc;
            }
            public MappingInfo getMappingInfo() {
                return MappingDefine.this;
            }
            public Object call(Object... objects) throws Throwable {
                return targetMethod.invoke(mc, objects);
            }
            public HttpServletRequest getHttpRequest() {
                return httpInfo.getHttpRequest();
            }
            public HttpServletResponse getHttpResponse() {
                return httpInfo.getHttpResponse();
            }
        };
        //
        return callStrategy.exeCall(call);
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}