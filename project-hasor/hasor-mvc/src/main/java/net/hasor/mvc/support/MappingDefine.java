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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.mvc.MappingTo;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.strategy.CallStrategy;
import net.hasor.mvc.strategy.CallStrategyFactory;
import org.more.UndefinedException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingDefine {
    private String                    bindID           = null;
    private Provider<ModelController> targetProvider   = null;
    private Method                    targetMethod     = null;
    private Class<?>[]                targetParamTypes = null;
    private Annotation[][]            targetParamAnno  = null;
    private Annotation[]              targetMethodAnno = null;
    private MappingInfo               mappingInfo      = null;
    private CallStrategyFactory       strategyFactory  = null;
    private AtomicBoolean             inited           = new AtomicBoolean(false);
    //
    protected MappingDefine(String bindID, Method targetMethod, CallStrategyFactory strategyFactory) {
        MappingTo pathAnno = targetMethod.getAnnotation(MappingTo.class);
        if (pathAnno == null)
            throw new UndefinedException("is not a valid Mapping Service.");
        String servicePath = pathAnno.value();
        if (StringUtils.isBlank(servicePath))
            throw new NullPointerException("Service path is empty.");
        if (!servicePath.matches("/.+"))
            throw new IllegalStateException("Service path format error");
        //
        this.bindID = bindID;
        this.targetMethod = targetMethod;
        this.targetParamTypes = targetMethod.getParameterTypes();
        this.targetParamAnno = targetMethod.getParameterAnnotations();
        this.targetMethodAnno = targetMethod.getAnnotations();
        this.mappingInfo = new MappingInfo();
        this.mappingInfo.setMappingTo(servicePath);
        this.mappingInfo.setMappingToMatches(servicePath.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})"));
        this.strategyFactory = strategyFactory;
    }
    /**@return 获取映射的地址*/
    public String getMappingTo() {
        return this.mappingInfo.getMappingTo();
    }
    /**
     * 测试路径是否匹配
     * @param requestPath 要测试的路径。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(String requestPath) {
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        return requestPath.matches(this.mappingInfo.getMappingToMatches());
    }
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
     * 创建 CallStrategy 对象。
     * @param parentCall 父parentCall
     * @return 返回CallStrategy
     */
    protected CallStrategy createCallStrategy(CallStrategy parentCall) {
        return this.strategyFactory.createStrategy(parentCall);
    }
    //
    /**
     * 调用目标并返回结果
     * @return 返回调用结果
     * @throws Throwable 异常抛出
     */
    public Object invoke() throws Throwable {
        return this.invoke(null, null);
    }
    /**
     * 调用目标并返回结果
     * @param params 执行控制器时用到的参数。
     * @return 返回调用结果
     * @throws Throwable 异常抛出
     */
    public Object invoke(Map<String, ?> params) throws Throwable {
        return this.invoke(null, params);
    }
    /**
     * 调用目标
     * @param call 执行策略
     * @param params 执行控制器时用到的参数。
     * @return 返回调用结果
     * @throws Throwable 异常抛出
     */
    public Object invoke(CallStrategy call, Map<String, ?> params) throws Throwable {
        final CallStrategy alCall = this.createCallStrategy(call);
        final Map<String, ?> atParams = (params == null) ? new HashMap<String, Object>() : params;
        //
        final ModelController mc = this.targetProvider.get();
        return alCall.exeCall(new Call() {
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
                return mappingInfo;
            }
            public Object call(Object... objects) throws Throwable {
                return targetMethod.invoke(mc, objects);
            }
        });
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}