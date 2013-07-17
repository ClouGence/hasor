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
import java.util.HashSet;
import org.hasor.annotation.SettingsListener;
import org.hasor.context.HasorSettingListener;
import org.hasor.context.Settings;
import org.hasor.context.XmlProperty;
import org.more.util.StringUtils;
/**
 * ≈‰÷√–≈œ¢
 * @version : 2013-4-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SettingsListener
public class ResourceSettings implements HasorSettingListener {
    public static class LoaderConfig {
        public String      loaderType = null;
        public XmlProperty config     = null;
    }
    private boolean        enable       = false;
    private String[]       contentTypes = null;
    private LoaderConfig[] loaders      = null;
    private String         cacheDir     = null;
    //
    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public String[] getContentTypes() {
        return contentTypes.clone();
    }
    public void setContentTypes(String[] contentTypes) {
        this.contentTypes = contentTypes;
    }
    public LoaderConfig[] getLoaders() {
        return loaders.clone();
    }
    public void setLoaders(LoaderConfig[] loaders) {
        this.loaders = loaders;
    }
    public String getCacheDir() {
        return cacheDir;
    }
    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }
    @Override
    public void onLoadConfig(Settings newConfig) {
        this.enable = newConfig.getBoolean("httpServlet.resourceLoader.enable");
        String typesRoot = newConfig.getString("httpServlet.resourceLoader.contentTypes");
        typesRoot = typesRoot == null ? "" : typesRoot;
        //1.∂¡»°types≈‰÷√
        HashSet<String> typesArray = new HashSet<String>();
        for (String type : typesRoot.split(",")) {
            if (StringUtils.isBlank(type) == true)
                continue;
            typesArray.add(type.trim());
        }
        this.contentTypes = typesArray.toArray(new String[typesArray.size()]);
        //2.∂¡»°loader≈‰÷√
        XmlProperty loaderRoot = newConfig.getXmlProperty("httpServlet.resourceLoader");
        ArrayList<LoaderConfig> loaderArray = new ArrayList<LoaderConfig>();
        for (XmlProperty c : loaderRoot.getChildren()) {
            LoaderConfig lc = new LoaderConfig();
            lc.loaderType = c.getName();
            lc.config = c;
            loaderArray.add(lc);
        }
        this.loaders = loaderArray.toArray(new LoaderConfig[loaderArray.size()]);
        //3.
        this.cacheDir = newConfig.getString("httpServlet.resourceLoader.cacheDir");
    }
}