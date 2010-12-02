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
package org.more.hypha.beans.support;
import org.more.core.xml.XmlParserKit;
import org.more.hypha.beans.TypeManager;
import org.more.hypha.context.XmlDefineResource;
import org.more.hypha.context.XmlNameSpaceRegister;
/**
 * 该类实现了{@link XmlNameSpaceRegister}接口并且提供了对命名空间“http://project.byshell.org/more/schema/beans”的解析支持。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class Register_Beans implements XmlNameSpaceRegister {
    /**如果没有指定namespaceURL参数则该常量将会指定默认的命名空间。*/
    public static final String DefaultNameSpaceURL = "http://project.byshell.org/more/schema/beans";
    /**执行初始化注册。*/
    public void initRegister(String namespaceURL, XmlDefineResource resource) {
        //1.注册标签解析器
        XmlParserKit kit = new XmlParserKit();
        kit.regeditHook("/beans", new TagBeans_Beans(resource));
        kit.regeditHook("/beans/meta", new TagBeans_MetaData(resource));
        kit.regeditHook("/beans/defaultPackage", new TagBeans_DefaultPackage(resource));
        kit.regeditHook(new String[] { "/beans/package", "/beans/*/package" }, new TagBeans_Package(resource));
        //
        kit.regeditHook(new String[] { "/beans/classBean", "/beans/*/classBean" }, new TagBeans_ClassBean(resource));
        kit.regeditHook(new String[] { "/beans/generateBean", "/beans/*/generateBean" }, new TagBeans_GenerateBean(resource));
        kit.regeditHook(new String[] { "/beans/refBean", "/beans/*/refBean" }, new TagBeans_RefBean(resource));
        kit.regeditHook(new String[] { "/beans/scriptBean", "/beans/*/scriptBean" }, new TagBeans_ScriptBean(resource));//同时具备text和element
        kit.regeditHook(new String[] { "/beans/templateBean", "/beans/*/templateBean" }, new TagBeans_TemplateBean(resource));
        kit.regeditHook(new String[] { "/beans/varBean", "/beans/*/varBean" }, new TagBeans_VarBean(resource));
        //
        kit.regeditHook("/beans/*Bean/constructor-arg", new TagBeans_Constructor(resource));
        kit.regeditHook("/beans/*Bean/property", new TagBeans_Property(resource));
        kit.regeditHook("/beans/*Bean/meta", new TagBeans_MetaData(resource));
        kit.regeditHook("/beans/*Bean/*/meta", new TagBeans_MetaData(resource));
        kit.regeditHook("/beans/*Bean/method", new TagBeans_Method(resource));
        kit.regeditHook("/beans/*Bean/method/param", new TagBeans_Param(resource));
        //
        kit.regeditHook("/beans/*Bean/*/value", new TagBeans_Value(resource));
        kit.regeditHook("/beans/*Bean/*/date", new TagBeans_Date(resource));
        kit.regeditHook("/beans/*Bean/*/enum", new TagBeans_Enum(resource));
        kit.regeditHook("/beans/*Bean/*/bigText", new TagBeans_BigText(resource));
        kit.regeditHook("/beans/*Bean/*/ref", new TagBeans_Ref(resource));
        kit.regeditHook("/beans/*Bean/*/file", new TagBeans_File(resource));
        kit.regeditHook("/beans/*Bean/*/directory", new TagBeans_Directory(resource));
        kit.regeditHook("/beans/*Bean/*/uri", new TagBeans_URI(resource));
        kit.regeditHook("/beans/*Bean/*/el", new TagBeans_EL(resource));
        kit.regeditHook("/beans/*Bean/*/array", new TagBeans_Array(resource));
        kit.regeditHook("/beans/*Bean/*/set", new TagBeans_Set(resource));
        kit.regeditHook("/beans/*Bean/*/list", new TagBeans_List(resource));
        kit.regeditHook("/beans/*Bean/*/map", new TagBeans_Map(resource));
        kit.regeditHook("/beans/*Bean/*/map/entity", new TagBeans_Entity(resource));
        //
        //2.注册命名空间
        if (namespaceURL == null)
            namespaceURL = DefaultNameSpaceURL;
        resource.regeditXmlParserKit(namespaceURL, kit);
        //3.注册快速属性值解析器，顺序就是优先级。
        /*当xml中试图配置了多种属性类别值时候优先级将会起到作用，列如同时配置了value 和 refBean属性。那么value的优先级比refBean高。*/
        TypeManager typeManager = resource.getTypeManager();
        typeManager.regeditTypeParser(new QPP_Value());
        typeManager.regeditTypeParser(new QPP_EL());
        typeManager.regeditTypeParser(new QPP_Date());
        typeManager.regeditTypeParser(new QPP_Enum());
        typeManager.regeditTypeParser(new QPP_Ref());
        typeManager.regeditTypeParser(new QPP_File());
        typeManager.regeditTypeParser(new QPP_Directory());
        typeManager.regeditTypeParser(new QPP_URILocation());
    }
}