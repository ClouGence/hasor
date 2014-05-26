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
import javax.xml.stream.XMLStreamReader;
/**
 * 当阅读xml结束时。
 * @version 2010-9-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class EndDocumentEvent extends XmlStreamEvent {
    public EndDocumentEvent(String xpath, XMLStreamReader reader) {
        super(xpath, reader);
    }
    /**该事件的拍档是{@link StartDocumentEvent}类型对象。*/
    public boolean isPartner(XmlStreamEvent e) {
        return e instanceof StartDocumentEvent;
    };
    /**文档结束事件，是共有事件。*/
    public boolean isPublicEvent() {
        return true;
    }
}