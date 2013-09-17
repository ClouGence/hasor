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
package net.hasor.mvc.resource.support;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.SettingsListener;
import net.hasor.core.context.AnnoModule;
import net.hasor.mvc.resource.ResourceLoaderCreator;
import net.hasor.mvc.resource.ResourceLoaderDefine;
import net.hasor.servlet.AbstractWebModule;
import net.hasor.servlet.WebApiBinder;
import net.hasor.servlet.anno.support.ServletAnnoSupportModule;
/**
 * 负责装载jar包中的资源。启动级别：Lv_1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule(description = "org.hasor.web.resource软件包功能支持。")
public class ServletResourceSupportModule extends AbstractWebModule {
    public void init(WebApiBinder apiBinder) {
        apiBinder.dependency().weak(ServletAnnoSupportModule.class);
        /*绑定Settings，但是不支持重载更新*/
        ResourceSettings settings = new ResourceSettings();
        settings.onLoadConfig(apiBinder.getEnvironment().getSettings());
        if (settings.isEnable() == false)
            return;
        //
        apiBinder.bindingType(SettingsListener.class, settings);
        this.loadResourceLoader(apiBinder);
        apiBinder.filter("*").through(ResourceLoaderFilter.class);
    }
    public void start(AppContext appContext) {}
    public void stop(AppContext appContext) {}
    //
    /**装载TemplateLoader*/
    protected void loadResourceLoader(WebApiBinder apiBinder) {
        //1.获取
        Set<Class<?>> resourceLoaderCreatorSet = apiBinder.getEnvironment().getClassSet(ResourceLoaderDefine.class);
        if (resourceLoaderCreatorSet == null)
            return;
        List<Class<ResourceLoaderCreator>> resourceLoaderCreatorList = new ArrayList<Class<ResourceLoaderCreator>>();
        for (Class<?> cls : resourceLoaderCreatorSet) {
            if (ResourceLoaderCreator.class.isAssignableFrom(cls) == false) {
                Hasor.warning("loadResourceLoader : not implemented ResourceLoaderCreator. class=%s", cls);
            } else {
                resourceLoaderCreatorList.add((Class<ResourceLoaderCreator>) cls);
            }
        }
        //3.注册服务
        ResourceBinderImplements loaderBinder = new ResourceBinderImplements();
        for (Class<ResourceLoaderCreator> creatorType : resourceLoaderCreatorList) {
            ResourceLoaderDefine creatorAnno = creatorType.getAnnotation(ResourceLoaderDefine.class);
            String defineName = creatorAnno.configElement();
            loaderBinder.bindLoaderCreator(defineName, creatorType);
            Hasor.info("loadResourceLoader %s at %s.", defineName, creatorType);
        }
        loaderBinder.configure(apiBinder.getGuiceBinder());
    }
}