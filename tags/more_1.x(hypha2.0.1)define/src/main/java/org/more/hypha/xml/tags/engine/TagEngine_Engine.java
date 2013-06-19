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
package org.more.hypha.xml.tags.engine;
import java.util.Map;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.xml.XmlDefineResource;
/**
 * 解析e:engine
 * @version : 2011-6-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagEngine_Engine extends TagRegister_NS implements XmlElementHook {
    private Map<String, String> xmlConfig = null;
    public TagEngine_Engine(XmlDefineResource configuration, Map<String, String> xmlConfig) {
        super(configuration);
        this.xmlConfig = xmlConfig;
    }
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        String name = event.getAttributeValue("name");
        String classname = event.getAttributeValue("class");
        this.xmlConfig.put(name, classname);//重复注册会导致替换的作用。
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
}