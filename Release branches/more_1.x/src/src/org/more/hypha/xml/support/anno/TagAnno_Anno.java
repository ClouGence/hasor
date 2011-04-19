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
package org.more.hypha.xml.support.anno;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.Event;
import org.more.hypha.xml.context.XmlDefineResource;
import org.more.hypha.xml.event.XmlLoadedEvent;
import org.more.hypha.xml.support.Tag_Abstract;
import org.more.util.StringConvert;
/**
 * 用于解析anno标签，负责注册{@link TagListener}类型对象。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAnno_Anno extends Tag_Abstract implements XmlElementHook {
    /**创建{@link TagAnno_Anno}对象*/
    public TagAnno_Anno(XmlDefineResource configuration) {
        super(configuration);
    };
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        String packageText = event.getAttributeValue("package");
        String enable = event.getAttributeValue("enable");
        if (StringConvert.parseBoolean(enable, true) == true) {
            TagListener annoListener = new TagListener(packageText);
            Event e = Event.getEvent(XmlLoadedEvent.class);
            this.getDefineResource().getEventManager().addEventListener(e, annoListener);
        }
    };
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {};
};