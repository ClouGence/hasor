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
package org.platform.freemarker.support;
import static org.platform.PlatformConfig.FreemarkerConfig_Settings;
import static org.platform.PlatformConfig.FreemarkerConfig_TemplateLoader;
import java.util.ArrayList;
import java.util.List;
import org.more.global.assembler.xml.XmlProperty;
import org.more.webui.freemarker.loader.MultiTemplateLoader;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.freemarker.ConfigurationFactory;
import org.platform.freemarker.ITemplateLoaderCreator;
import org.platform.freemarker.loader.ITemplateLoader;
import com.google.inject.Singleton;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-5-16
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class DefaultFreemarkerFactory implements ConfigurationFactory {
    private Configuration cfg = null;
    @Override
    public synchronized Configuration configuration(AppContext appContext) {
        if (this.cfg != null)
            return this.cfg;
        //
        this.cfg = new Configuration();
        //1.应用设置
        XmlProperty settings = appContext.getSettings().getXmlProperty(FreemarkerConfig_Settings);
        if (settings != null) {
            List<XmlProperty> childrenList = settings.getChildren();
            for (XmlProperty item : childrenList) {
                String key = item.getName();
                String val = item.getText();
                val = val != null ? val.trim() : "";
                try {
                    this.cfg.setSetting(key, val);
                    Platform.info("apply setting %s = %s.", key, val);
                } catch (TemplateException e) {
                    Platform.error("apply Setting at %s an error. value is %s.%s", key, val, e);
                }
            }
        }
        //2.创建TemplateLoader
        ArrayList<ITemplateLoader> templateLoaderList = new ArrayList<ITemplateLoader>();
        XmlProperty configLoaderList = appContext.getSettings().getXmlProperty(FreemarkerConfig_TemplateLoader);
        TemplateLoaderCreatorManager templateLoaderCreatorManager = new TemplateLoaderCreatorManager(appContext);
        if (configLoaderList != null) {
            List<XmlProperty> childrenList = configLoaderList.getChildren();
            for (XmlProperty item : childrenList) {
                String key = item.getName();
                String val = item.getText();
                val = val != null ? val.trim() : "";
                //
                ITemplateLoaderCreator creator = templateLoaderCreatorManager.getCreator(key);
                if (creator == null) {
                    Platform.warning("missing %s TemplateLoaderCreator!", key);
                } else {
                    try {
                        ITemplateLoader loader = creator.newTemplateLoader(appContext, item);
                        if (loader == null)
                            Platform.error("%s newTemplateLoader call newTemplateLoader return is null. config is %s.", key, val);
                        else {
                            String logInfo = val.replace("\n", "");
                            logInfo = logInfo.length() > 30 ? logInfo.substring(0, 30) : logInfo;
                            Platform.info("[%s] TemplateLoader is added. config = %s", key, logInfo);
                            templateLoaderList.add(loader);
                        }
                    } catch (Exception e) {
                        Platform.error("%s newTemplateLoader has error.%s", e);
                    }
                }
                //
            }
        }
        //3.配置TemplateLoader
        TemplateLoader[] loaders = templateLoaderList.toArray(new TemplateLoader[templateLoaderList.size()]);
        if (loaders.length >= 0)
            this.cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        return this.cfg;
    }
}