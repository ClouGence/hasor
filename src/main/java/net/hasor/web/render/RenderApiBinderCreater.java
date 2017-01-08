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
package net.hasor.web.render;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.binder.ApiBinderCreater;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        RenderApiBinder binder = new RenderApiBinderImpl(apiBinder);
        //
        Environment environment = apiBinder.getEnvironment();
        Settings settings = environment.getSettings();
        XmlNode[] xmlPropArray = settings.getXmlNodeArray("hasor.renderSet");
        Map<String, String> renderMap = new HashMap<String, String>();
        for (XmlNode xmlProp : xmlPropArray) {
            for (XmlNode envItem : xmlProp.getChildren()) {
                if (StringUtils.equalsIgnoreCase("render", envItem.getName())) {
                    String renderTypeStr = envItem.getAttribute("renderType");
                    String renderClass = envItem.getText();
                    if (StringUtils.isNotBlank(renderTypeStr)) {
                        String[] renderTypeArray = renderTypeStr.split(";");
                        for (String renderType : renderTypeArray) {
                            if (StringUtils.isNotBlank(renderType)) {
                                logger.info("web -> renderType {} mappingTo {}.", renderType, renderClass);
                                renderMap.put(renderType.toUpperCase(), renderClass);
                            }
                        }
                    }
                }
            }
        }
        //
        for (String key : renderMap.keySet()) {
            String type = renderMap.get(key);
            try {
                Class<?> renderType = environment.getClassLoader().loadClass(type);
                binder.suffix(key).bind((Class<? extends RenderEngine>) renderType);
            } catch (Exception e) {
                logger.error("web -> renderType {} load failed {}.", type, e.getMessage(), e);
            }
        }
        //
        return binder;
    }
}