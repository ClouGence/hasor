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
package org.hasor.test.web.resource;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.context.AnnoModule;
import net.hasor.web.resource.ResourceLoader;
import net.hasor.web.resource.ResourceLoaderFactory;
import net.hasor.web.resource.loader.ClassPathResourceLoader;
import net.hasor.web.resource.loader.ZipResourceLoader;
import net.hasor.web.resource.support.ResourceHttpServlet;
import net.hasor.web.servlet.WebApiBinder;
import net.hasor.web.servlet.WebModule;
/**
 * 
 * @version : 2013-10-29
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@AnnoModule
public class CustomResources extends WebModule {
    public void init(WebApiBinder apiBinder) {
        //
        //js,css,gif,ico,jpg,jpeg,png,swf,swc,flv,mp3,wav,avi
        apiBinder.serve("*.js").with(ResourceHttpServlet.class);
        apiBinder.serve("*.css").with(ResourceHttpServlet.class);
        apiBinder.serve("*.gif").with(ResourceHttpServlet.class);
        apiBinder.serve("*.ico").with(ResourceHttpServlet.class);
        apiBinder.serve("*.jpg").with(ResourceHttpServlet.class);
        apiBinder.serve("*.jpeg").with(ResourceHttpServlet.class);
        apiBinder.serve("*.png").with(ResourceHttpServlet.class);
        apiBinder.serve("*.swf").with(ResourceHttpServlet.class);
        apiBinder.serve("*.swc").with(ResourceHttpServlet.class);
        apiBinder.serve("*.flv").with(ResourceHttpServlet.class);
        apiBinder.serve("*.mp3").with(ResourceHttpServlet.class);
        apiBinder.serve("*.wav").with(ResourceHttpServlet.class);
        apiBinder.serve("*.avi").with(ResourceHttpServlet.class);
        //
        apiBinder.getGuiceBinder().bind(ResourceLoaderFactory.class).toInstance(new CustomResourceLoaderFactory());
    }
    public void start(AppContext appContext) {
        // TODO Auto-generated method stub
    }
    public void stop(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}
/**/
class CustomResourceLoaderFactory implements ResourceLoaderFactory {
    public ResourceLoader[] loaderArray(AppContext appContext) {
        Environment env = appContext.getEnvironment();
        ResourceLoader[] loader = new ResourceLoader[2];
        //
        try {
            loader[0] = new ZipResourceLoader(env.evalString("%HASOR_WEBROOT%/pic.zip"));
            loader[1] = new ClassPathResourceLoader("/META-INF/webapp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        return loader;
    }
}