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
package net.hasor.web.resource.support;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.context.AnnoModule;
import net.hasor.core.gift.GiftSupportModule;
import net.hasor.web.resource.ResourceLoaderCreator;
import net.hasor.web.resource.ResourceLoaderDefine;
import net.hasor.web.resource.support.ResourceSettings.LoaderConfig;
import net.hasor.web.servlet.WebApiBinder;
import net.hasor.web.servlet.WebModule;
import org.more.util.StringUtils;
/**
 * 负责装载jar包中的资源。启动级别：Lv_1
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule()
public class WebResourceModule extends WebModule {
    public void init(WebApiBinder apiBinder) {
        //1.依赖关系
        apiBinder.dependency().weak(GiftSupportModule.class);
        //2.装载配置
        Settings settings = apiBinder.getEnvironment().getSettings();
        ResourceSettings resSettings = new ResourceSettings(settings);
        apiBinder.bindingType(ResourceSettings.class).toInstance(resSettings);
        if (resSettings.isEnable() == false)
            return;
        //3.loadLoader
        initServlet(apiBinder);
        //4.绑定 Servlet
        String[] types = resSettings.getContentTypes();
        for (String t : types)
            apiBinder.serve(t).with(ResourceHttpServlet.class);
    }
    //
    public void start(AppContext appContext) {
        //
    }
    public void stop(AppContext appContext) {
        //
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    /**装载TemplateLoader*/
    private static Map<String, Class<ResourceLoaderCreator>> loadResourceLoader(WebApiBinder apiBinder) {
        Map<String, Class<ResourceLoaderCreator>> creatorMap = new HashMap<String, Class<ResourceLoaderCreator>>();
        //1.获取
        Set<Class<?>> resourceLoaderCreatorSet = apiBinder.getEnvironment().getClassSet(ResourceLoaderDefine.class);
        if (resourceLoaderCreatorSet == null)
            return creatorMap;
        List<Class<ResourceLoaderCreator>> resourceLoaderCreatorList = new ArrayList<Class<ResourceLoaderCreator>>();
        for (Class<?> cls : resourceLoaderCreatorSet) {
            if (ResourceLoaderCreator.class.isAssignableFrom(cls) == false)
                Hasor.warning("loadResourceLoader : not implemented ResourceLoaderCreator. class=%s", cls);
            else
                resourceLoaderCreatorList.add((Class<ResourceLoaderCreator>) cls);
        }
        //3.注册服务
        for (Class<ResourceLoaderCreator> creatorType : resourceLoaderCreatorList) {
            ResourceLoaderDefine creatorAnno = creatorType.getAnnotation(ResourceLoaderDefine.class);
            String defineName = creatorAnno.configElement();
            if (StringUtils.isBlank(defineName) == true) {
                Hasor.warning("loadResourceLoader %s miss name.", creatorType);
                continue;
            }
            //
            creatorMap.put(defineName.toUpperCase(), creatorType);
            Hasor.info("loadResourceLoader %s at %s.", defineName, creatorType);
        }
        return creatorMap;
    }
    /***/
    private static void initServlet(WebApiBinder apiBinder) {
        //1.准备参数
        Environment env = apiBinder.getEnvironment();
        Map<String, Class<ResourceLoaderCreator>> creatorMap = loadResourceLoader(apiBinder);
        //2.配置Loader
        Settings settings = env.getSettings();
        ResourceSettings resSettings = new ResourceSettings(settings);
        for (LoaderConfig item : resSettings.getLoaders()) {
            String loaderType = item.loaderType;
            String configVal = item.config.getText();
            Class<ResourceLoaderCreator> creatorType = creatorMap.get(loaderType.toUpperCase());
            if (creatorType == null) {
                Hasor.warning("missing %s ResourceLoaderCreator config is %s.", loaderType, configVal);
                continue;
            }
            //
            ResourceLoaderProvider rlcd = new ResourceLoaderProvider(loaderType, item.config, creatorType);
            apiBinder.bindingType(ResourceLoaderProvider.class).toInstance(rlcd);
            Hasor.info("add %s ResourceLoaderCreator config is %s.", loaderType, configVal);
        }
        //3.缓存路径
        String cacheSubPath = "%HASOR_PLUGIN_PATH%/net.hasor.web.resource/";
        File cacheDir = new File(env.evalString(cacheSubPath));
        if (!chekcCacheDir(cacheDir)) {
            int i = 0;
            while (true) {
                cacheDir = new File(env.evalString(cacheSubPath + "_" + String.valueOf(i)));;
                if (chekcCacheDir(cacheDir))
                    break;
            }
        }
        ResourceHttpServlet.initCacheDir(cacheDir);
    }
    private static boolean chekcCacheDir(File cacheDir) {
        cacheDir.mkdirs();
        if (cacheDir.isDirectory() == false && cacheDir.exists() == true)
            return false;
        else
            return true;
    }
}