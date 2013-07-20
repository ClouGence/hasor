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
package org.hasor.web.resource.support;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.servlet.WebApiBinder;
import org.hasor.servlet.WebHasorModule;
import org.hasor.web.resource.ResourceLoaderCreator;
import org.hasor.web.resource.ResourceLoaderDefine;
/**
 * 负责装载jar包中的资源。启动级别：Lv_1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "ResourceLoaderPlatformListener", description = "org.hasor.web.resource软件包功能支持。", startIndex = Module.Lv_1)
public class ResourceLoaderPlatformListener extends WebHasorModule {
    @Override
    public void init(WebApiBinder apiBinder) {
        this.loadResourceLoader(apiBinder);
        apiBinder.filter("*").through(ResourceLoaderFilter.class);
    }
    //
    /**装载TemplateLoader*/
    protected void loadResourceLoader(WebApiBinder event) {
        //1.获取
        Set<Class<?>> resourceLoaderCreatorSet = event.getClassSet(ResourceLoaderDefine.class);
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
        loaderBinder.configure(event.getGuiceBinder());
    }
}