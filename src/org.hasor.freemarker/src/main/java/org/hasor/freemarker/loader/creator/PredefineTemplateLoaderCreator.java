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
package org.hasor.freemarker.loader.creator;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.XmlProperty;
import org.hasor.freemarker.FmTemplateLoader;
import org.hasor.freemarker.FmTemplateLoaderCreator;
import org.hasor.freemarker.FmTemplateLoaderDefine;
import org.hasor.freemarker.loader.ConfigTemplateLoader;
import org.more.convert.ConverterUtils;
import org.more.util.StringUtils;
/**
 * 处理配置文件中添加的模板。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org) 
 */
@FmTemplateLoaderDefine(configElement = "PredefineLoader")
public class PredefineTemplateLoaderCreator implements FmTemplateLoaderCreator {
    public static enum PredefineType {
        Resource, File, String, URL
    }
    public FmTemplateLoader newTemplateLoader(AppContext appContext, XmlProperty xmlConfig) throws MalformedURLException {
        List<XmlProperty> childrenList = xmlConfig.getChildren();
        if (childrenList == null)
            return null;
        //
        ConfigTemplateLoader configTemplateLoader = new ConfigTemplateLoader();
        for (XmlProperty xmlItem : childrenList) {
            if (xmlItem.getName().toLowerCase().equals("templatebody") == false)
                continue;
            //
            String keyVal = xmlItem.getAttributeMap().get("key");
            String bodyVal = xmlItem.getText();
            keyVal = StringUtils.isBlank(keyVal) ? null : keyVal;
            bodyVal = StringUtils.isBlank(bodyVal) ? "" : bodyVal;
            //
            if (StringUtils.isBlank(keyVal) == true)
                continue;
            //
            String predefineTypeStr = xmlConfig.getAttributeMap().get("type");
            PredefineType predefineType = (PredefineType) ConverterUtils.convert(PredefineType.class, predefineTypeStr);
            predefineType = (predefineType == null) ? PredefineType.String : predefineType;
            switch (predefineType) {
            case File:
                configTemplateLoader.addTemplate(keyVal, new File(bodyVal));
                break;
            case Resource:
                configTemplateLoader.addTemplate(keyVal, bodyVal);
                break;
            case String:
                configTemplateLoader.addTemplateAsString(keyVal, bodyVal);
                break;
            case URL:
                configTemplateLoader.addTemplate(keyVal, new URL(bodyVal));
                break;
            }
        }
        Hasor.info("loadConfig keys %s", configTemplateLoader.getKeys());
        return configTemplateLoader;
    }
}