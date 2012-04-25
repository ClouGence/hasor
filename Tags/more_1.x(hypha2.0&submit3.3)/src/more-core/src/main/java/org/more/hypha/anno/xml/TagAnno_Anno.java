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
package org.more.hypha.anno.xml;
import org.more.core.event.Event;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.anno.AnnoService;
import org.more.hypha.commons.xml.Tag_Abstract;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.context.xml.XmlLoadedEvent;
import org.more.util.StringConvertUtil;
/**
 * 用于解析anno标签，负责注册{@link TagListener}类型对象。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAnno_Anno extends Tag_Abstract implements XmlElementHook {
    private AnnoService annoService = null;
    /**创建{@link TagAnno_Anno}对象*/
    public TagAnno_Anno(XmlDefineResource configuration, AnnoService annoService) {
        super(configuration);
        this.annoService = annoService;
    };
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        String packageText = event.getAttributeValue("package");
        String enable = event.getAttributeValue("enable");
        if (StringConvertUtil.parseBoolean(enable, true) == true) {
            TagListener annoListener = new TagListener(packageText, this.annoService);
            Event e = Event.getEvent(XmlLoadedEvent.class);
            this.getDefineResource().getEventManager().addEventListener(e, annoListener);
        }
    };
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {};
};