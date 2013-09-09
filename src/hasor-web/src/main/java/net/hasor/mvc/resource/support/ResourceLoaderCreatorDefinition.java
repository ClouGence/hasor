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
package net.hasor.mvc.resource.support;
import net.hasor.core.AppContext;
import net.hasor.mvc.resource.ResourceLoaderCreator;
import com.google.inject.Provider;
/**
 *  
 * @version : 2013-3-12
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class ResourceLoaderCreatorDefinition implements Provider<ResourceLoaderCreator> {
    private String                       name                = null;
    private AppContext                   appContext          = null;
    private Class<ResourceLoaderCreator> loaderCreatorType   = null;
    private ResourceLoaderCreator        loaderCreatorObject = null;
    //
    public ResourceLoaderCreatorDefinition(String name, Class<ResourceLoaderCreator> loaderCreatorType) {
        this.name = name;
        this.loaderCreatorType = loaderCreatorType;
    }
    public String getName() {
        return name;
    }
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public ResourceLoaderCreator get() {
        if (this.loaderCreatorObject == null)
            this.loaderCreatorObject = this.appContext.getInstance(this.loaderCreatorType);
        return this.loaderCreatorObject;
    }
}