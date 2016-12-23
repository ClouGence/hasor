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
package net.hasor.restful.invoker;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.web.RenderEngine;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.more.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/***
 * restful插件
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class RestfulModule extends WebModule {
    public final void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        // .Render
        Settings settings = apiBinder.getEnvironment().getSettings();
        XmlNode[] xmlPropArray = settings.getXmlNodeArray("hasor.restful.renderSet");
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
                                logger.info("restful -> renderType {} mappingTo {}.", renderType, renderClass);
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
                Class<?> renderType = apiBinder.getEnvironment().getClassLoader().loadClass(type);
                apiBinder.bindType(RenderEngine.class)//
                        .nameWith(key)//
                        .to((Class<? extends RenderEngine>) renderType)//
                        .metaData("FORM-XML", true);
            } catch (Exception e) {
                logger.error("restful -> renderType {} load failed {}.", type, e.getMessage(), e);
            }
        }
        //
        // .MappingTo
        Set<Class<?>> serviceSet = apiBinder.findClass(MappingTo.class);
        serviceSet = (serviceSet == null) ? new HashSet<Class<?>>() : new HashSet<Class<?>>(serviceSet);
        serviceSet.remove(MappingTo.class);
        if (serviceSet.isEmpty()) {
            logger.warn("restful -> exit , not found any @MappingTo.");
            return;
        }
        int count = 0;
        for (Class<?> type : serviceSet) {
            if (loadType(apiBinder, type)) {
                count++;
            }
        }
        if (count > 0) {
            logger.info("restful -> init restful root filter , found {} MappingTo.", count);
            apiBinder.filter("/*").through(new RestfulFilter());
        } else {
            logger.warn("restful -> exit , not add any @MappingTo.");
        }
        //
    }
    public boolean loadType(WebApiBinder apiBinder, Class<?> clazz) {
        int modifier = clazz.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT)) {
            return false;
        }
        if (!clazz.isAnnotationPresent(MappingTo.class)) {
            return false;
        }
        //
        MappingTo mto = clazz.getAnnotation(MappingTo.class);
        logger.info("restful -> type ‘{}’ mappingTo: ‘{}’.", clazz.getName(), mto.value());
        MappingToDefine define = new MappingToDefine(clazz);
        apiBinder.bindType(MappingToDefine.class).uniqueName().toInstance(define);
        return true;
    }
    //
    /** 通过位运算决定check是否在data里。 */
    private boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}