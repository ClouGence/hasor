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
package net.hasor.restful.invoker;
import net.hasor.restful.MimeType;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.xml.stream.*;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @version : 2015年2月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class MimeTypeContext extends ConcurrentHashMap<String, String> implements MimeType {
    private static final long serialVersionUID = -8955832291109288048L;
    private ServletContext content;
    public MimeTypeContext(ServletContext content) {
        this.content = content;
    }
    //
    public ServletContext getContent() {
        return this.content;
    }
    //
    /**根据扩展名获取meta类型。*/
    public String getMimeType(String suffix) {
        String mimeType = this.getContent().getMimeType(suffix);
        if (StringUtils.isNotBlank(mimeType)) {
            return mimeType;
        }
        return this.get(suffix);
    }
    //
    /**装载数据。*/
    public void loadStream(String resourceName) throws XMLStreamException, IOException {
        List<InputStream> inStreamList = ResourcesUtils.getResourcesAsStream(resourceName);
        for (InputStream inStream : inStreamList) {
            this.loadStream(inStream);
        }
    }
    public void loadStream(InputStream inStream) throws XMLStreamException, IOException {
        new XmlReader(inStream).reader(new XmlAccept() {
            private StringBuffer stringBuffer = new StringBuffer();
            private String extension = null;
            private String mimeType = null;
            public void beginAccept() throws XMLStreamException {
            }
            public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
                if (e instanceof TextEvent) {
                    TextEvent event = (TextEvent) e;
                    this.stringBuffer.append(event.getText());
                } else if (e instanceof EndElementEvent) {
                    EndElementEvent ee = (EndElementEvent) e;
                    if (StringUtils.equalsIgnoreCase(ee.getElementName(), "extension")) {
                        this.extension = this.stringBuffer.toString();
                    } else if (StringUtils.equalsIgnoreCase(ee.getElementName(), "mime-type")) {
                        this.mimeType = this.stringBuffer.toString();
                    } else if (StringUtils.equalsIgnoreCase(ee.getElementName(), "mime-mapping")) {
                        if (!StringUtils.isBlank(this.extension) && !StringUtils.isBlank(this.mimeType)) {
                            String key = this.extension.trim().toLowerCase();
                            String var = this.mimeType.trim();
                            if (!MimeTypeContext.this.containsKey(key)) {
                                MimeTypeContext.this.put(key, var);
                            }
                        }
                        this.extension = null;
                        this.mimeType = null;
                    }
                    //
                    this.stringBuffer = new StringBuffer();
                }
            }
            public void endAccept() throws XMLStreamException {
            }
        }, null);//最后一个参数为空,表示不忽略任何xml节点。
    }
}