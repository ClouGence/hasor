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
package net.hasor.plugins.templates;
import java.util.List;
import org.more.util.StringUtils;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class TemplateModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        Settings settings = apiBinder.getEnvironment().getSettings();
        String engineName = settings.getString("hasor.template.engine", "");
        if (StringUtils.isBlank(engineName)) {
            logger.info("template Module not be load. -> engineName not configured.");
            return;
        }
        List<XmlNode> engineList = settings.merageXmlNode("hasor.template.engineSet", "engine");
        XmlNode engineConfig = null;
        for (XmlNode engineType : engineList) {
            engineConfig = null;
            String etype = engineType.getAttribute("type");
            if (StringUtils.equals(engineName, etype) == true) {
                engineConfig = engineType;
                break;
            }
        }
        String engineTypeName = null;
        if (engineConfig == null) {
            engineTypeName = engineName;
        } else {
            engineTypeName = engineConfig.getText().trim();
        }
        if (StringUtils.isBlank(engineTypeName)) {
            logger.info("template Module not be load. -> engineName undefined.");
            return;
        }
        //
        try {
            Class<TemplateEngine> engineType = (Class<TemplateEngine>) Class.forName(engineTypeName);
            apiBinder.bindType(TemplateEngine.class).to(engineType);
            //
            TemplateHttpServlet servlet = new TemplateHttpServlet();
            String interceptNames = settings.getString("hasor.template.urlPatterns", "htm;html;");
            for (String name : interceptNames.split(";")) {
                if (StringUtils.isBlank(name) == false) {
                    apiBinder.serve("*." + name).with(servlet);
                }
            }
            logger.info("template Module load. -> servlet[{}], engineName={} , type={}.", interceptNames, engineName, engineType);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}