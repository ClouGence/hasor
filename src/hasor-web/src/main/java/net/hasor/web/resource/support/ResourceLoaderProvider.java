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
package net.hasor.web.resource.support;
import java.io.IOException;
import net.hasor.core.AppContext;
import net.hasor.core.XmlNode;
import net.hasor.web.resource.ResourceLoader;
import net.hasor.web.resource.ResourceLoaderCreator;
import org.more.UnhandledException;
import com.google.inject.Provider;
/**
 *  
 * @version : 2013-3-12
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class ResourceLoaderProvider implements Provider<ResourceLoader> {
    private String                       loaderType;
    private XmlNode                      xmlSettings;
    private Class<ResourceLoaderCreator> loaderCreator;
    //
    private AppContext                   appContext   = null;
    private ResourceLoader               loaderObject = null;
    //
    public ResourceLoaderProvider(String loaderType, XmlNode xmlSettings, Class<ResourceLoaderCreator> loaderCreator) {
        this.loaderType = loaderType;
        this.xmlSettings = xmlSettings;
        this.loaderCreator = loaderCreator;
    }
    public String getLoaderType() {
        return loaderType;
    }
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public ResourceLoader get() {
        if (this.loaderObject != null)
            return this.loaderObject;
        //
        try {
            ResourceLoaderCreator creator = this.appContext.getInstance(this.loaderCreator);
            this.loaderObject = creator.newInstance(this.appContext, this.xmlSettings);
            return this.loaderObject;
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }
}