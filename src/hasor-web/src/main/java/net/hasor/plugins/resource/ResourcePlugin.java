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
package net.hasor.plugins.resource;
import java.io.File;
import net.hasor.core.Environment;
import net.hasor.core.plugin.Plugin;
import net.hasor.web.AbstractWebHasorPlugin;
import net.hasor.web.WebApiBinder;
/**
 * 负责装载jar包中的资源。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@Plugin()
public class ResourcePlugin extends AbstractWebHasorPlugin {
    public void loadPlugin(WebApiBinder apiBinder) {
        //1.准备参数
        Environment env = apiBinder.getEnvironment();
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