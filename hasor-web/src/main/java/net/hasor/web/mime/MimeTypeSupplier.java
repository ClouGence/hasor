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
package net.hasor.web.mime;
import net.hasor.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 * {@link MimeType} 接口实现。
 * @version : 2015年2月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class MimeTypeSupplier extends ConcurrentHashMap<String, String> implements MimeType {
    private static final long   serialVersionUID = -8955832291109288048L;
    protected            Logger logger           = LoggerFactory.getLogger(getClass());
    private ServletContext content;
    public MimeTypeSupplier(ServletContext content) {
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
    public void loadStream(String resourceName) throws IOException {
        List<InputStream> inStreamList = ResourcesUtils.getResourcesAsStream(resourceName);
        for (InputStream inStream : inStreamList) {
            this.loadStream(inStream);
        }
    }
    public void loadStream(InputStream inStream) throws IOException {
        try {
            this.logger.debug("parsing...");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            factory.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = factory.newSAXParser();
            SaxXmlParser handler = new SaxXmlParser(this);
            parser.parse(inStream, handler);
            IOUtils.closeQuietly(inStream);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}