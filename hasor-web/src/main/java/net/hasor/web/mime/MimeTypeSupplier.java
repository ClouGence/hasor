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
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.web.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.servlet.ServletContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link MimeType} 接口实现。
 * @version : 2015年2月11日
 * @author 赵永春 (zyc@hasor.net)
 */
public class MimeTypeSupplier extends ConcurrentHashMap<String, String> implements MimeType {
    private static final long           serialVersionUID = -8955832291109288048L;
    private static final Logger         logger           = LoggerFactory.getLogger(MimeTypeSupplier.class);
    private final        ServletContext content;

    public MimeTypeSupplier(ServletContext content) {
        this.content = content;
    }

    public ServletContext getContent() {
        return this.content;
    }

    /**根据扩展名获取meta类型。*/
    public String getMimeType(String suffix) {
        String mimeType = this.get(suffix.toUpperCase());
        if (StringUtils.isBlank(mimeType)) {
            return this.getContent().getMimeType(suffix);
        }
        return mimeType;
    }

    public void addMimeType(String type, String mimeType) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(mimeType)) {
            put(type.toUpperCase(), mimeType);
        }
    }

    /**装载数据。*/
    public void loadResource(String resourceName) throws IOException {
        ClassLoader classLoader = this.content.getClassLoader();
        List<InputStream> inStreamList = null;
        if (classLoader == null) {
            inStreamList = ResourcesUtils.getResourceAsStreamList(resourceName);
        } else {
            inStreamList = ResourcesUtils.getResourceAsStreamList(this.content.getClassLoader(), resourceName);
        }
        for (InputStream inStream : inStreamList) {
            this.loadStream(inStream);
        }
    }

    public void loadReader(Reader reader) throws IOException {
        logger.debug("parsingReader...");
        prossParser(reader, saxParser -> {
            saxParser.parse(new InputSource(reader), new SaxXmlParser(MimeTypeSupplier.this));
        });
    }

    public void loadStream(InputStream inStream) throws IOException {
        logger.debug("parsingStream...");
        prossParser(inStream, saxParser -> {
            saxParser.parse(inStream, new SaxXmlParser(MimeTypeSupplier.this));
        });
    }

    private void prossParser(Closeable closeable, Call call) throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            factory.setFeature("http://xml.org/sax/features/namespaces", true);
            call.parser(factory.newSAXParser());
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(closeable);
        }
    }

    private static interface Call {
        public void parser(SAXParser saxParser) throws Exception;
    }
}
