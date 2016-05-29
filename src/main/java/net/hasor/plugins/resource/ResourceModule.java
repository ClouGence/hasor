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
package net.hasor.plugins.resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.more.json.JSON;
import org.more.util.StringUtils;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * 负责装载jar包中的资源。
 * 
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResourceModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        // 确定要加载的资源类型。
        Settings settings = apiBinder.getEnvironment().getSettings();
        String interceptNames = settings.getString("hasor.resourceLoader.urlPatterns", "");
        List<String> filterList = new ArrayList<String>();
        for (String name : interceptNames.split(";")) {
            if (StringUtils.isBlank(name) == false) {
                filterList.add("*." + name);
            }
        }
        if (filterList.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("resource -> urlPatterns is empty , exit.");
            }
            return;
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("resource -> urlPatterns is {}", JSON.toString(filterList));
            }
        }
        //
        // 缓存路径
        Environment env = apiBinder.getEnvironment();
        String cacheSubPath = env.getPluginDir(ResourceModule.class);
        File cacheDir = new File(env.evalString(cacheSubPath));
        if (!chekcCacheDir(cacheDir)) {
            int i = 0;
            while (true) {
                if (i > 99999) {
                    logger.error("resource -> loade failed cacheDir no permission. at {}.", cacheDir);
                    return;
                }
                cacheDir = new File(env.evalString(cacheSubPath + "_" + String.valueOf(i)));;
                if (chekcCacheDir(cacheDir)) {
                    break;
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("resource -> use cacheDir is {}.", cacheDir);
        }
        //
        // 资源过滤器
        ResourceFilter resourceFilter = new ResourceFilter(cacheDir);
        for (String item : filterList) {
            apiBinder.filter(item).through(Integer.MAX_VALUE, resourceFilter);
        }
        if (logger.isInfoEnabled()) {
            logger.info("resource -> finish.");
        }
    }
    private static boolean chekcCacheDir(File cacheDir) {
        cacheDir.mkdirs();
        if (cacheDir.isDirectory() == false && cacheDir.exists() == true) {
            return false;
        } else {
            return true;
        }
    }
}