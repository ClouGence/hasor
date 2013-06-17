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
package org.platform.servlet.action.support;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import org.more.util.ArrayUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AppContext;
import org.platform.context.PlatformListener;
import org.platform.context.startup.PlatformExt;
import org.platform.servlet.action.ActionBinder.ActionBindingBuilder;
import org.platform.servlet.action.ActionBinder.NameSpaceBindingBuilder;
import org.platform.servlet.action.Controller;
import org.platform.servlet.action.HttpMethod;
import org.platform.servlet.action.MimeType;
import org.platform.servlet.action.RestfulMapping;
import org.platform.servlet.action.ResultDefine;
import org.platform.servlet.action.ResultProcess;
import com.google.inject.Binder;
/**
 * Action服务启动类，用于装载action。启动级别Lv1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@PlatformExt(displayName = "ActionModuleListener", description = "org.platform.action软件包功能支持。", startIndex = PlatformExt.Lv_1)
public class ActionPlatformListener implements PlatformListener {
    private ActionSettings settings      = null;
    private ActionManager  actionManager = null;
    @Override
    public void initialize(ApiBinder event) {
        Binder binder = event.getGuiceBinder();
        event.filter("*").through(MergedController.class);
        /*配置*/
        this.settings = new ActionSettings();
        this.settings.loadConfig(event.getSettings());
        binder.bind(ActionSettings.class).toInstance(this.settings);//通过Guice
        binder.bind(ActionManager.class).to(InternalActionManager.class).asEagerSingleton();
        binder.bind(ActionManager.class).to(InternalActionManager.class).asEagerSingleton();
        /*初始化*/
        this.loadController(event);
        /*注册结果处理器*/
        this.loadResultDefine(event);
    }
    //
    /*装载Controller*/
    protected void loadController(ApiBinder event) {
        //1.获取
        Set<Class<?>> controllerSet = event.getClassSet(Controller.class);
        if (controllerSet == null)
            return;
        //3.注册服务
        ActionBinderImplements actionBinder = new ActionBinderImplements();
        for (Class<?> controllerType : controllerSet) {
            Controller controllerAnno = controllerType.getAnnotation(Controller.class);
            for (String namespace : controllerAnno.value()) {
                NameSpaceBindingBuilder nsBinding = actionBinder.bindNameSpace(namespace);
                this.loadController(nsBinding, controllerType);
            }
        }
        event.getGuiceBinder().install(actionBinder);
    }
    /*装载Controller*/
    private void loadController(NameSpaceBindingBuilder nsBinding, Class<?> controllerType) {
        List<Method> actionMethods = BeanUtils.getMethods(controllerType);
        Object[] ignoreMethods = this.settings.getIgnoreMethod().toArray();//忽略
        Controller controllerAnno = controllerType.getAnnotation(Controller.class);
        for (Method method : actionMethods) {
            //1.执行忽略
            if (ArrayUtils.isInclude(ignoreMethods, method.getName()) == true)
                continue;
            //2.注册Action
            ActionBindingBuilder actionBinding = nsBinding.bindActionMethod(method);
            for (String httpMethod : controllerAnno.httpMethod())
                actionBinding = actionBinding.onHttpMethod(httpMethod);
            //3.
            MimeType mt = method.getAnnotation(MimeType.class);
            mt = (mt == null) ? controllerType.getAnnotation(MimeType.class) : mt;
            String minmeType = (mt != null) ? mt.value() : this.settings.getDefaultMimeType();
            if (!StringUtils.isBlank(minmeType))
                actionBinding = actionBinding.returnMimeType(minmeType);
            //4.restful
            RestfulMapping mappingRestful = method.getAnnotation(RestfulMapping.class);
            if (mappingRestful != null) {
                for (HttpMethod httpMethod : mappingRestful.httpMethod())
                    actionBinding = actionBinding.onHttpMethod(httpMethod.name().toUpperCase());
                actionBinding.mappingRestful(mappingRestful.value());
            }
            //
        }
    }
    //
    /*装载ResultDefine*/
    protected void loadResultDefine(ApiBinder event) {
        //1.获取
        Set<Class<?>> resultDefineSet = event.getClassSet(ResultDefine.class);
        if (resultDefineSet == null)
            return;
        //3.注册服务
        ActionBinderImplements actionBinder = new ActionBinderImplements();
        for (Class<?> resultDefineType : resultDefineSet) {
            ResultDefine resultDefineAnno = resultDefineType.getAnnotation(ResultDefine.class);
            if (ResultProcess.class.isAssignableFrom(resultDefineType) == false) {
                Platform.warning("loadResultDefine : not implemented ResultProcess. class=%s", resultDefineType);
            } else {
                actionBinder.bindResultProcess(resultDefineAnno.value()).toType((Class<? extends ResultProcess>) resultDefineType);
            }
        }
        event.getGuiceBinder().install(actionBinder);
    }
    //
    @Override
    public void initialized(AppContext appContext) {
        appContext.getSettings().addSettingsListener(this.settings);
        this.actionManager = appContext.getInstance(ActionManager.class);
        this.actionManager.initManager(appContext);
        //
        Platform.info("online ->> action is %s for style %s", (this.settings.isEnable() ? "enable." : "disable."), this.settings.getMode());
    }
    @Override
    public void destroy(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this.settings);
        this.actionManager.destroyManager(appContext);
    }
}