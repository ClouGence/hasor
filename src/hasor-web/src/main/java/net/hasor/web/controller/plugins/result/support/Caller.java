/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.web.controller.plugins.result.support;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.web.controller.plugins.result.ControllerResultDefine;
import net.hasor.web.controller.plugins.result.ControllerResultProcess;
/**
 * 
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Singleton
class Caller implements EventListener {
    @Inject
    private AppContext                                            appContext = null;
    private Map<Class<? extends Annotation>, ResultProcessPropxy> defineMap  = null;
    //
    private void init() {
        if (this.defineMap != null)
            return;
        this.defineMap = new HashMap<Class<? extends Annotation>, ResultProcessPropxy>();
        //
        //1.ªÒ»°
        Set<Class<?>> resultDefineSet = appContext.getClassSet(ControllerResultDefine.class);
        if (resultDefineSet == null) {
            Hasor.warning("Didn't find any ResultProcess.");
            return;
        }
        //2.◊¢≤·∑˛ŒÒ
        for (Class<?> resultDefineType : resultDefineSet) {
            ControllerResultDefine resultDefineAnno = resultDefineType.getAnnotation(ControllerResultDefine.class);
            if (ControllerResultProcess.class.isAssignableFrom(resultDefineType) == false) {
                Hasor.warning("loadResultDefine : not implemented ResultProcess. class=%s", resultDefineType);
            } else {
                Class<? extends ControllerResultProcess> defineType = (Class<? extends ControllerResultProcess>) resultDefineType;
                Class<? extends Annotation> resultType = resultDefineAnno.value();
                if (resultType == null)
                    continue;
                Hasor.info("loadResultDefine annoType is %s toInstance %s", resultType, resultDefineType);
                //
                ResultProcessPropxy propxy = new ResultProcessPropxy(resultType, defineType, appContext);
                this.defineMap.put(resultType, propxy);
            }
        }
    }
    public void onEvent(String event, Object[] params) throws ServletException, IOException {
        this.init();
        //
        ActionInvoke invoke = (ActionInvoke) params[0];
        Object[] invokeParams = (Object[]) params[1];
        Object returnData = params[2];
        //
        Method httpJavaMethod = invoke.getActionDefine().getTargetMethod();
        Annotation[] annos = httpJavaMethod.getAnnotations();
        if (annos == null)
            return;
        for (Annotation anno : annos) {
            Class<? extends Annotation> resultType = anno.annotationType();
            if (resultType != null) {
                ResultProcessPropxy propxy = this.defineMap.get(resultType);
                if (propxy != null) {
                    propxy.process(invoke.getRequest(), invoke.getResponse(), anno, returnData);
                    return;
                }
            }
        }
    }
}