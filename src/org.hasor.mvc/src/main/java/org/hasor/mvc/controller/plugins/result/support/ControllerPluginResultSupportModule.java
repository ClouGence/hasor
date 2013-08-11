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
package org.hasor.mvc.controller.plugins.result.support;
import java.util.Set;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.Module;
import org.hasor.mvc.controller.plugins.result.ResultDefine;
import org.hasor.mvc.controller.plugins.result.ResultProcess;
import org.hasor.mvc.controller.support.ActionInvoke;
import org.hasor.mvc.controller.support.ServletControllerSupportModule;
import org.hasor.servlet.AbstractWebHasorModule;
import org.hasor.servlet.WebApiBinder;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 负责处理Action调用之后返回值的处理。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(description = "org.hasor.mvc.controller.plugins.result软件包功能支持。")
public class ControllerPluginResultSupportModule extends AbstractWebHasorModule {
    @Override
    public void configuration(ModuleSettings info) {
        info.followTarget(ServletControllerSupportModule.class);
    }
    @Override
    public void init(WebApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> resultDefineSet = apiBinder.getClassSet(ResultDefine.class);
        if (resultDefineSet == null) {
            Hasor.warning("Didn't find any ResultProcess.");
            return;
        }
        //2.注册服务
        for (Class<?> resultDefineType : resultDefineSet) {
            ResultDefine resultDefineAnno = resultDefineType.getAnnotation(ResultDefine.class);
            if (ResultProcess.class.isAssignableFrom(resultDefineType) == false) {
                Hasor.warning("loadResultDefine : not implemented ResultProcess. class=%s", resultDefineType);
            } else {
                Hasor.info("loadResultDefine annoType is %s toInstance %s", resultDefineAnno.value(), resultDefineType);
                //
                Class<? extends ResultProcess> defineType = (Class<? extends ResultProcess>) resultDefineType;
                ResultProcessPropxy propxy = new ResultProcessPropxy(resultDefineAnno.value(), defineType);
                apiBinder.getGuiceBinder().bind(ResultProcessPropxy.class).annotatedWith(UniqueAnnotations.create()).toInstance(propxy);
            }
        }
        //3.声明Caller、ResultProcessManager
        apiBinder.getGuiceBinder().bind(ResultProcessManager.class);
        apiBinder.getGuiceBinder().bind(Caller.class);
    }
    @Override
    public void start(AppContext appContext) {
        Caller caller = appContext.getInstance(Caller.class);
        appContext.getEventManager().addEventListener(ActionInvoke.Event_AfterInvoke, caller);
    }
    @Override
    public void stop(AppContext appContext) {
        Caller caller = appContext.getInstance(Caller.class);
        appContext.getEventManager().removeEventListener(ActionInvoke.Event_AfterInvoke, caller);;
    }
}