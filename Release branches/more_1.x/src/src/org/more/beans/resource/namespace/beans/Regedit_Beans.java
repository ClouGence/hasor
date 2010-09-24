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
package org.more.beans.resource.namespace.beans;
import org.more.beans.resource.XmlConfiguration;
import org.more.beans.resource.namespace.NameSpaceRegedit;
import org.more.core.xml.XmlParserKit;
/**
 * 该类实现了{@link NameSpaceRegedit}接口并且提供了对命名空间“http://project.byshell.org/more/schema/beans”的解析支持。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class Regedit_Beans implements NameSpaceRegedit {
    public static final String DefaultNameSpaceURL = "http://project.byshell.org/more/schema/beans";
    /**执行初始化注册。*/
    public void initRegedit(String namespaceURL, XmlConfiguration config) {
        //1.注册标签解析器
        XmlParserKit kit = new XmlParserKit();
        kit.regeditHook("/beans", new TagBeans_Beans(config));
        kit.regeditHook("/beans/classBean", new TagBeans_ClassBean(config));
        kit.regeditHook("/beans/generateBean", new TagBeans_GenerateBean(config));
        kit.regeditHook("/beans/refBean", new TagBeans_RefBean(config));
        kit.regeditHook("/beans/scriptBean", new TagBeans_ScriptBean(config));//同时具备text和element
        kit.regeditHook("/beans/templateBean", new TagBeans_TemplateBean(config));
        kit.regeditHook("/beans/varBean", new TagBeans_VarBean(config));
        kit.regeditHook("/beans/*Bean/constructor-arg", new TagBeans_Constructor(config));
        kit.regeditHook("/beans/*Bean/property", new TagBeans_Property(config));
        kit.regeditHook("/beans/*Bean/meta", new TagBeans_MetaData(config));
        kit.regeditHook("/beans/*Bean/*/meta", new TagBeans_MetaData(config));
        //
        kit.regeditHook("/beans/*Bean/*/value", new TagBeans_Value(config));
        kit.regeditHook("/beans/*Bean/*/date", new TagBeans_Date(config));
        kit.regeditHook("/beans/*Bean/*/enum", new TagBeans_Enum(config));
        kit.regeditHook("/beans/*Bean/*/bigText", new TagBeans_BigText(config));
        kit.regeditHook("/beans/*Bean/*/ref", new TagBeans_Ref(config));
        kit.regeditHook("/beans/*Bean/*/file", new TagBeans_File(config));
        kit.regeditHook("/beans/*Bean/*/directory", new TagBeans_Directory(config));
        kit.regeditHook("/beans/*Bean/*/uri", new TagBeans_URI(config));
        kit.regeditHook("/beans/*Bean/*/array", new TagBeans_Array(config));
        kit.regeditHook("/beans/*Bean/*/set", new TagBeans_Set(config));
        kit.regeditHook("/beans/*Bean/*/list", new TagBeans_List(config));
        kit.regeditHook("/beans/*Bean/*/map", new TagBeans_Map(config));
        kit.regeditHook("/beans/*Bean/*/map/entity", new TagBeans_Entity(config));
        //
        //2.注册命名空间
        if (namespaceURL == null)
            namespaceURL = DefaultNameSpaceURL;
        config.regeditXmlParserKit(namespaceURL, kit);
        //3.注册快速属性值解析器，顺序就是优先级。
        /*当xml中试图配置了多种属性类别值时候优先级将会起到作用，列如同时配置了value 和 refBean属性。那么value的优先级比refBean高。*/
        config.regeditQuickParser(new QPP_Value());
        config.regeditQuickParser(new QPP_Date());
        config.regeditQuickParser(new QPP_Enum());
        config.regeditQuickParser(new QPP_Ref());
        config.regeditQuickParser(new QPP_File());
        config.regeditQuickParser(new QPP_Directory());
        config.regeditQuickParser(new QPP_URILocation());
    }
}