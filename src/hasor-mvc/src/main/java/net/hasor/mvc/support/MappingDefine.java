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
import java.lang.reflect.Method;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.mvc.MappingTo;
import net.hasor.mvc.ModelController;
import org.more.UndefinedException;
import org.more.classcode.FormatException;
import org.more.util.BeanUtils;
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
    private String                    mappingTo        = null;
    private String                    mappingToMatches = null;
    //
    protected MappingDefine(String bindID, Method targetMethod) {
        MappingTo pathAnno = targetMethod.getAnnotation(MappingTo.class);
        if (pathAnno == null)
            throw new UndefinedException("is not a valid Mapping Service.");
        String servicePath = pathAnno.value();
        if (StringUtils.isBlank(servicePath))
            throw new NullPointerException("Service path is empty.");
        if (!servicePath.matches("/.+"))
            throw new FormatException("Service path format error");
        //
        this.bindID = bindID;
        this.targetMethod = targetMethod;
        this.mappingTo = servicePath;
        this.mappingToMatches = servicePath.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
    }
    /**获取映射的地址*/
    public String getMappingTo() {
        return mappingTo;
    }
    /**测试路径是否匹配*/
    public boolean matchingMapping(String requestPath) {
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        return requestPath.matches(this.mappingToMatches);
    }
    /**执行初始化*/
    public void init(AppContext appContext) {
        Hasor.assertIsNotNull(appContext, "appContext is null.");
        BindInfo<ModelController> controllerInfo = appContext.getBindInfo(this.bindID);
        this.targetProvider = appContext.getProvider(controllerInfo);
    }
    /**调用目标*/
    public Object invoke(CallStrategy call) throws Throwable {
        if (call == null)
            call = new InnerCallStrategy();
        //
        final ModelController mc = this.targetProvider.get();
        return call.exeCall(new Call() {
            public Method getMethod() {
                return targetMethod;
            }
            public ModelController getTarget() {
                return mc;
            }
            public Object call(Object... objects) throws Throwable {
                return targetMethod.invoke(mc, objects);
            }
        });
    }
}
class InnerCallStrategy implements CallStrategy {
    public Object exeCall(Call call) throws Throwable {
        Class<?>[] params = call.getMethod().getParameterTypes();
        Object[] paramsValues = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            paramsValues[i] = BeanUtils.getDefaultValue(params[i]);
        }
        return call.call(paramsValues);
    }
}