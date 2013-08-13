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
package org.hasor.mvc.controller.support;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import org.hasor.context.AppContext;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.Module;
import org.hasor.mvc.controller.ActionBinder.ActionBindingBuilder;
import org.hasor.mvc.controller.ActionBinder.NameSpaceBindingBuilder;
import org.hasor.mvc.controller.Controller;
import org.hasor.mvc.controller.HttpMethod;
import org.hasor.mvc.controller.Produces;
import org.hasor.mvc.controller.RestfulMapping;
import org.hasor.servlet.AbstractWebHasorModule;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.anno.support.ServletAnnoSupportModule;
import org.more.util.ArrayUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import com.google.inject.Binder;
/**
 * Action服务启动类，用于装载action。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(description = "org.hasor.web.controller软件包功能支持。")
public class ServletControllerSupportModule extends AbstractWebHasorModule {
    private ActionSettings settings      = null;
    private ActionManager  actionManager = null;
    @Override
    public void configuration(ModuleSettings info) {
        info.beforeMe(ServletAnnoSupportModule.class);
    }
    @Override
    public void init(WebApiBinder apiBinder) {
        Binder binder = apiBinder.getGuiceBinder();
        apiBinder.filter("*").through(MergedController.class);
        this.settings = new ActionSettings();
        this.settings.onLoadConfig(apiBinder.getInitContext().getSettings());
        apiBinder.getGuiceBinder().bind(ActionSettings.class).toInstance(this.settings);
        /*配置*/
        binder.bind(ActionManager.class).asEagerSingleton();
        ActionManagerBuilder actionBinder = new ActionManagerBuilder();
        /*初始化*/
        this.loadController(apiBinder, actionBinder);
        /*构造*/
        actionBinder.buildManager(binder);
    }
    //
    /**装载Controller*/
    protected void loadController(WebApiBinder event, ActionManagerBuilder actionBinder) {
        //1.获取
        Set<Class<?>> controllerSet = event.getClassSet(Controller.class);
        if (controllerSet == null)
            return;
        //3.注册服务
        for (Class<?> controllerType : controllerSet) {
            Controller controllerAnno = controllerType.getAnnotation(Controller.class);
            for (String namespace : controllerAnno.value()) {
                NameSpaceBindingBuilder nsBinding = actionBinder.bindNameSpace(namespace);
                this.loadController(nsBinding, controllerType);
            }
        }
    }
    private void loadController(NameSpaceBindingBuilder nsBinding, Class<?> controllerType) {
        List<Method> actionMethods = BeanUtils.getMethods(controllerType);
        Object[] ignoreMethods = settings.getIgnoreMethod().toArray();//忽略
        for (Method method : actionMethods) {
            //1.执行忽略
            if (ArrayUtils.contains(ignoreMethods, method.getName()) == true)
                continue;
            //2.注册Action
            ActionBindingBuilder actionBinding = nsBinding.bindActionMethod(method);
            actionBinding = actionBinding.onHttpMethod(HttpMethod.Any);
            //3. 
            Produces mt = method.getAnnotation(Produces.class);
            mt = (mt == null) ? controllerType.getAnnotation(Produces.class) : mt;
            String minmeType = (mt != null) ? mt.value() : this.settings.getDefaultProduces();
            if (!StringUtils.isBlank(minmeType))
                actionBinding = actionBinding.returnMimeType(minmeType);
            //4.restful
            RestfulMapping mappingRestful = method.getAnnotation(RestfulMapping.class);
            if (mappingRestful != null) {
                for (HttpMethod httpMethod : mappingRestful.httpMethod())
                    actionBinding = actionBinding.onHttpMethod(httpMethod);
                actionBinding.mappingRestful(mappingRestful.value());
            }
        }
    }
    //
    /***/
    @Override
    public void start(AppContext appContext) {
        this.actionManager = appContext.getInstance(ActionManager.class);
        this.actionManager.initManager(appContext);
    }
    @Override
    public void destroy(AppContext appContext) {
        this.actionManager.destroyManager(appContext);
    }
}