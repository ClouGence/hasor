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
package org.more.xml;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.StartElementEvent;
/**
 * 该钩子用于处理元素开始和元素结束事件。
 * @version 2010-9-13
 * @author 赵永春 (zyc@hasor.net)
 */
public interface XmlElementHook extends XmlParserHook {
    /**
     * 当遇到一个开始标签时。在该方法中可以通过event对象获取到这个标签的属性。
     * @param context 环境上下文。
     * @param xpath 当前标签在所定义的命名空间中的xpath。
     * @param event 事件。
     */
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException;
    /**
     * 当遇到一个结束标签时。
     * @param context 环境上下文。
     * @param xpath 当前标签在所定义的命名空间中的xpath。
     * @param event 事件。
     */
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException;
}