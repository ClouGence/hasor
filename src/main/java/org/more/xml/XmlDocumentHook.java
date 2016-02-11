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
package org.more.xml;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.more.xml.stream.EndDocumentEvent;
import org.more.xml.stream.StartDocumentEvent;
/**
 * 该钩子用于处理文档开始和文档结束事件。
 * @version 2010-9-13
 * @author 赵永春 (zyc@hasor.net)
 */
public interface XmlDocumentHook extends XmlParserHook {
    /**当遇到文档开始时。context参数是共用的环境对象。*/
    public void beginDocument(XmlStackDecorator<Object> context, StartDocumentEvent event) throws XMLStreamException, IOException;
    /**当遇到文档结束时。context参数是共用的环境对象。*/
    public void endDocument(XmlStackDecorator<Object> context, EndDocumentEvent event) throws XMLStreamException, IOException;
}