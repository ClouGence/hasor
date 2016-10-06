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

import org.more.xml.stream.XmlStreamEvent;
/**
 * Level 2：级别的事件接收者。经过{@link XmlParserKitManager}工具进行细分的xml事件分流之后都发送到了该接口中。
 * @version 2010-9-13
 * @author 赵永春 (zyc@hasor.net)
 */
public interface XmlNamespaceParser {
    /**当收到开始解析的信号时，该方法主要用于初始化解析器。*/
    public void beginAccept();

    /**当收到停止解析的信号时，该方法主要用于做解析器的后续处理工作。*/
    public void endAccept();

    /**该方法在beginAccept和endAccept方法调用期间反复调用，每当Level 1发现一个事件都会通知给Level 2，然后由Level 2进行分发。*/
    public void sendEvent(XmlStackDecorator<Object> context, String xpath, XmlStreamEvent event) throws IOException, XMLStreamException;
}