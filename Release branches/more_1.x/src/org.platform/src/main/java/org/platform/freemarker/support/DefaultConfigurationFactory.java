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
import java.util.List;
import org.more.global.assembler.xml.XmlProperty;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.freemarker.ConfigurationFactory;
import org.platform.freemarker.loader.ClassPathTemplateLoader;
import com.google.inject.Singleton;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-5-16
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class DefaultConfigurationFactory implements ConfigurationFactory {
    private Configuration cfg = null;
    @Override
    public Configuration configuration(AppContext appContext) {
        if (this.cfg == null) {
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
                        Platform.info("apply freemarker setting %s = %s.", key, val);
                    } catch (TemplateException e) {
                        Platform.error("apply Configuration Setting at %s an error. value is %s.%s", key, val, e);
                    }
                }
            }
            //2.应用TemplateLoader
            cfg.setTemplateLoader(new ClassPathTemplateLoader("/webapps"));
            XmlProperty loaderList = appContext.getSettings().getXmlProperty(FreemarkerConfig_TemplateLoader);
            if (loaderList != null) {
                List<XmlProperty> childrenList = loaderList.getChildren();
                for (XmlProperty item : childrenList) {
                    String key = item.getName();
                    String val = item.getText();
                    val = val != null ? val.trim() : "";
                    System.out.println(key + "=" + val);
                }
            }
            //
            //cfg.setAllSharedVariables(hash)
            //TemplateLoader[] loaders = null;
            //if (cfg.getTemplateLoader() != null) {
            //    loaders = new TemplateLoader[2];
            //    loaders[1] = cfg.getTemplateLoader();
            //} else
            //    loaders = new TemplateLoader[1];
            //loaders[0] = this.configTemplateLoader;
            //cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        }
        return cfg;
    }
}