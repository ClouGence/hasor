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
package org.platform.servlet.resource.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import org.more.util.StringUtils;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.TextEvent;
import org.more.xml.stream.XmlAccept;
import org.more.xml.stream.XmlReader;
import org.more.xml.stream.XmlStreamEvent;
/**
 * 
 * @version : 2013-6-7
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class MimeType extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;
    public void loadStream(InputStream inStream, String encoding) throws XMLStreamException, IOException {
        new XmlReader(inStream).reader(new XmlAccept() {
            private StringBuffer stringBuffer = new StringBuffer();
            private String       extension    = null;
            private String       mimeType     = null;
            @Override
            public void beginAccept() throws XMLStreamException {}
            @Override
            public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
                if (e instanceof TextEvent) {
                    TextEvent event = (TextEvent) e;
                    this.stringBuffer.append(event.getText());
                } else if (e instanceof EndElementEvent) {
                    EndElementEvent ee = (EndElementEvent) e;
                    if (StringUtils.eqUnCaseSensitive(ee.getElementName(), "extension"))
                        this.extension = this.stringBuffer.toString();
                    else if (StringUtils.eqUnCaseSensitive(ee.getElementName(), "mime-type"))
                        this.mimeType = this.stringBuffer.toString();
                    else if (StringUtils.eqUnCaseSensitive(ee.getElementName(), "mime-mapping")) {
                        if (!StringUtils.isBlank(this.extension) && !StringUtils.isBlank(this.mimeType))
                            MimeType.this.put(this.extension.trim().toLowerCase(), this.mimeType.trim().toLowerCase());
                        this.extension = null;
                        this.mimeType = null;
                    }
                    //
                    this.stringBuffer = new StringBuffer();
                }
            }
            @Override
            public void endAccept() throws XMLStreamException {}
        }, encoding, null);
    }
}