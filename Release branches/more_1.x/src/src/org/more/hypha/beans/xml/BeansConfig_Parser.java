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
package org.more.hypha.beans.xml;
import java.util.List;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.assembler.B_MDParser;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析c:parser标签
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class BeansConfig_Parser extends BeansConfig_NS implements XmlElementHook {
    public BeansConfig_Parser(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        List<B_MDParser> btList = (List<B_MDParser>) context.getAttribute(BeansConfig_MDParserConfig.MDParserConfigList);
        B_MDParser bt = new B_MDParser();
        bt.setMdType(event.getAttributeValue("mdType"));
        bt.setClassName(event.getAttributeValue("class"));
        btList.add(bt);
    }
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {}
}