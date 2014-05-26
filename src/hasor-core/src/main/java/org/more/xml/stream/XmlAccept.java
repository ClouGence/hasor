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
package org.more.xml.stream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
/**
 * 该接口的功能是用于接收{@link XmlReader}类扫描的xml事件流。如果在解析期间由sendEvent方法抛出异常那么endAccept方法很可能不会被调用。
 * @version 2010-9-11
 * @author 赵永春 (zyc@hasor.net)
 */
public interface XmlAccept {
    /**开始{@link XmlAccept}接口的调用，该方法主要用于重置状态。
     * @throws XMLStreamException */
    public void beginAccept() throws XMLStreamException;
    /**结束{@link XmlAccept}接口的调用。*/
    public void endAccept() throws XMLStreamException;
    /**该方法是用于接受{@link XmlReader}类扫描的事件结果。如果在解析期间由sendEvent方法抛出异常那么endAccept方法很可能不会被调用。 */
    public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException;
}