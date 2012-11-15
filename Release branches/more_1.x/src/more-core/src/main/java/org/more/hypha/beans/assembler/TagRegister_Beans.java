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
package org.more.hypha.beans.assembler;
import java.io.IOException;
import org.more.core.error.LoadException;
import org.more.core.xml.XmlParserKit;
import org.more.hypha.commons.xml.AbstractXmlRegister;
import org.more.hypha.xml.XmlDefineResource;
import org.more.hypha.xml.XmlNameSpaceRegister;
import org.more.hypha.xml.tags.beans.TagBeans_Array;
import org.more.hypha.xml.tags.beans.TagBeans_Beans;
import org.more.hypha.xml.tags.beans.TagBeans_BigText;
import org.more.hypha.xml.tags.beans.TagBeans_ClassBean;
import org.more.hypha.xml.tags.beans.TagBeans_Constructor;
import org.more.hypha.xml.tags.beans.TagBeans_Date;
import org.more.hypha.xml.tags.beans.TagBeans_DefaultPackage;
import org.more.hypha.xml.tags.beans.TagBeans_Directory;
import org.more.hypha.xml.tags.beans.TagBeans_EL;
import org.more.hypha.xml.tags.beans.TagBeans_Entity;
import org.more.hypha.xml.tags.beans.TagBeans_Enum;
import org.more.hypha.xml.tags.beans.TagBeans_File;
import org.more.hypha.xml.tags.beans.TagBeans_List;
import org.more.hypha.xml.tags.beans.TagBeans_Map;
import org.more.hypha.xml.tags.beans.TagBeans_MetaData;
import org.more.hypha.xml.tags.beans.TagBeans_Method;
import org.more.hypha.xml.tags.beans.TagBeans_Package;
import org.more.hypha.xml.tags.beans.TagBeans_Param;
import org.more.hypha.xml.tags.beans.TagBeans_Property;
import org.more.hypha.xml.tags.beans.TagBeans_Ref;
import org.more.hypha.xml.tags.beans.TagBeans_RefBean;
import org.more.hypha.xml.tags.beans.TagBeans_Set;
import org.more.hypha.xml.tags.beans.TagBeans_TemplateBean;
import org.more.hypha.xml.tags.beans.TagBeans_URI;
import org.more.hypha.xml.tags.beans.TagBeans_Value;
import org.more.hypha.xml.tags.beans.TagBeans_VarBean;
import org.more.hypha.xml.tags.beans.qpp.QPP_Date;
import org.more.hypha.xml.tags.beans.qpp.QPP_Directory;
import org.more.hypha.xml.tags.beans.qpp.QPP_EL;
import org.more.hypha.xml.tags.beans.qpp.QPP_Enum;
import org.more.hypha.xml.tags.beans.qpp.QPP_File;
import org.more.hypha.xml.tags.beans.qpp.QPP_ROOT;
import org.more.hypha.xml.tags.beans.qpp.QPP_Ref;
import org.more.hypha.xml.tags.beans.qpp.QPP_URILocation;
import org.more.hypha.xml.tags.beans.qpp.QPP_Value;
/**
 * 该类实现了{@link XmlNameSpaceRegister}接口并且提供了对命名空间“http://project.byshell.org/more/schema/beans”的解析支持。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagRegister_Beans extends AbstractXmlRegister {
    /**执行初始化注册。*/
    public void initRegister(XmlParserKit parserKit, XmlDefineResource resource) throws LoadException, IOException {
        //1.注册标签解析器
        parserKit.regeditHook("/beans", new TagBeans_Beans(resource));
        parserKit.regeditHook("/beans/meta", new TagBeans_MetaData(resource));
        parserKit.regeditHook("/beans/defaultPackage", new TagBeans_DefaultPackage(resource));
        parserKit.regeditHook(new String[] { "/beans/package", "/beans/*/package" }, new TagBeans_Package(resource));
        //
        parserKit.regeditHook(new String[] { "/beans/classBean", "/beans/*/classBean" }, new TagBeans_ClassBean(resource));
        parserKit.regeditHook(new String[] { "/beans/refBean", "/beans/*/refBean" }, new TagBeans_RefBean(resource));
        parserKit.regeditHook(new String[] { "/beans/templateBean", "/beans/*/templateBean" }, new TagBeans_TemplateBean(resource));
        parserKit.regeditHook(new String[] { "/beans/varBean", "/beans/*/varBean" }, new TagBeans_VarBean(resource));
        //
        parserKit.regeditHook("/beans/*Bean/constructor-arg", new TagBeans_Constructor(resource));
        parserKit.regeditHook("/beans/*Bean/property", new TagBeans_Property(resource));
        parserKit.regeditHook("/beans/*Bean/meta", new TagBeans_MetaData(resource));
        parserKit.regeditHook("/beans/*Bean/*/meta", new TagBeans_MetaData(resource));
        parserKit.regeditHook("/beans/*Bean/method", new TagBeans_Method(resource));
        parserKit.regeditHook("/beans/*Bean/method/param", new TagBeans_Param(resource));
        //
        parserKit.regeditHook("/beans/*Bean/*/value", new TagBeans_Value(resource));
        parserKit.regeditHook("/beans/*Bean/*/date", new TagBeans_Date(resource));
        parserKit.regeditHook("/beans/*Bean/*/enum", new TagBeans_Enum(resource));
        parserKit.regeditHook("/beans/*Bean/*/bigText", new TagBeans_BigText(resource));
        parserKit.regeditHook("/beans/*Bean/*/ref", new TagBeans_Ref(resource));
        parserKit.regeditHook("/beans/*Bean/*/file", new TagBeans_File(resource));
        parserKit.regeditHook("/beans/*Bean/*/directory", new TagBeans_Directory(resource));
        parserKit.regeditHook("/beans/*Bean/*/uri", new TagBeans_URI(resource));
        parserKit.regeditHook("/beans/*Bean/*/el", new TagBeans_EL(resource));
        parserKit.regeditHook("/beans/*Bean/*/array", new TagBeans_Array(resource));
        parserKit.regeditHook("/beans/*Bean/*/set", new TagBeans_Set(resource));
        parserKit.regeditHook("/beans/*Bean/*/list", new TagBeans_List(resource));
        parserKit.regeditHook("/beans/*Bean/*/map", new TagBeans_Map(resource));
        parserKit.regeditHook("/beans/*Bean/*/map/entity", new TagBeans_Entity(resource));
        //2.注册快速属性值解析器，顺序就是优先级。
        /*当xml中试图配置了多种属性类别值时候优先级将会起到作用，列如同时配置了value 和 refBean属性。那么value的优先级比refBean高。*/
        QPP_ROOT typeManager = new QPP_ROOT();
        typeManager.regeditTypeParser(new QPP_Value());
        typeManager.regeditTypeParser(new QPP_EL());
        typeManager.regeditTypeParser(new QPP_Date());
        typeManager.regeditTypeParser(new QPP_Enum());
        typeManager.regeditTypeParser(new QPP_Ref());
        typeManager.regeditTypeParser(new QPP_File());
        typeManager.regeditTypeParser(new QPP_Directory());
        typeManager.regeditTypeParser(new QPP_URILocation());
        resource.getFlash().setAttribute("org.more.hypha.beans.xml.QPP_ROOT", typeManager);
    }
}