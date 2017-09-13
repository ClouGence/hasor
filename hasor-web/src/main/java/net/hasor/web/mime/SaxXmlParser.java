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
import net.hasor.utils.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;
/**
 * @version : 2013-7-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class SaxXmlParser extends DefaultHandler {
    private Map<String, String> dataMap;
    public SaxXmlParser(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }
    //
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (!"mime-mapping".equalsIgnoreCase(localName))
            return;
        String extension = attributes.getValue("extension");
        String mimeType = attributes.getValue("mimeType");
        if (StringUtils.isBlank(extension) || StringUtils.isBlank(mimeType))
            return;
        this.dataMap.put(extension, mimeType);
    }
}