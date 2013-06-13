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
package org.platform.servlet.resource.support;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AppContext;
import org.platform.context.PlatformListener;
import org.platform.context.startup.PlatformExt;
import org.platform.servlet.resource.IResourceLoaderCreator;
import org.platform.servlet.resource.ResourceLoaderCreator;
/**
 * 负责装载jar包中的资源。启动级别：Lv1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@PlatformExt(displayName = "ResourceLoaderPlatformListener", description = "org.platform.servlet.resource软件包功能支持。", startIndex = PlatformExt.Lv_1)
public class ResourceLoaderPlatformListener implements PlatformListener {
    private ResourceSettings resourceSettings = null;
    @Override
    public void initialize(ApiBinder binder) {
        this.resourceSettings = new ResourceSettings();
        this.resourceSettings.loadConfig(binder.getSettings());
        binder.getGuiceBinder().bind(ResourceSettings.class).toInstance(this.resourceSettings);
        //
        this.loadResourceLoader(binder);
        binder.filter("*").through(ResourceLoaderFilter.class);
    }
    //
    /**装载TemplateLoader*/
    protected void loadResourceLoader(ApiBinder event) {
        //1.获取
        Set<Class<?>> resourceLoaderCreatorSet = event.getClassSet(ResourceLoaderCreator.class);
        if (resourceLoaderCreatorSet == null)
            return;
        List<Class<IResourceLoaderCreator>> resourceLoaderCreatorList = new ArrayList<Class<IResourceLoaderCreator>>();
        for (Class<?> cls : resourceLoaderCreatorSet) {
            if (IResourceLoaderCreator.class.isAssignableFrom(cls) == false) {
                Platform.warning("loadResourceLoader : not implemented ResourceLoaderCreator. class=%s", cls);
            } else {
                resourceLoaderCreatorList.add((Class<IResourceLoaderCreator>) cls);
            }
        }
        //3.注册服务
        ResourceBinderImplements loaderBinder = new ResourceBinderImplements();
        for (Class<IResourceLoaderCreator> creatorType : resourceLoaderCreatorList) {
            ResourceLoaderCreator creatorAnno = creatorType.getAnnotation(ResourceLoaderCreator.class);
            String defineName = creatorAnno.value();
            loaderBinder.bindLoaderCreator(defineName, creatorType);
            Platform.info("loadResourceLoader %s at %s.", defineName, creatorType);
        }
        loaderBinder.configure(event.getGuiceBinder());
    }
    @Override
    public void initialized(AppContext appContext) {
        appContext.getSettings().addSettingsListener(this.resourceSettings);
    }
    @Override
    public void destroy(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this.resourceSettings);
    }
}