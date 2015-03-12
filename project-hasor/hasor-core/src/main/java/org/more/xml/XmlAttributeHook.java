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
import org.more.xml.stream.AttributeEvent;
/**
 * 当遇到一个属性需要解析时使用该接口，使用该接口可以用于解析特定的属性。
 * @version 2010-9-13
 * @author 赵永春 (zyc@hasor.net)
 */
public interface XmlAttributeHook extends XmlParserHook {
    /**
     * 当遇到一个属性时。
     * @param context 环境上下文。
     * @param xpath 当前标签在所定义的命名空间中的xpath。
     * @param event 事件。
     */
    public void attribute(XmlStackDecorator<Object> context, String xpath, AttributeEvent event) throws XMLStreamException, IOException;
}