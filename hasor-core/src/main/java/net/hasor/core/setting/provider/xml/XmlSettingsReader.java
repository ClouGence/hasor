/*
 *
 *  * Copyright 2008-2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package net.hasor.core.setting.provider.xml;
import net.hasor.core.Settings;
import net.hasor.core.setting.provider.ConfigSource;
import net.hasor.core.setting.provider.SettingsReader;
import net.hasor.core.setting.provider.StreamType;
import net.hasor.utils.ResourcesUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 *
 * @version : 2021-02-01
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlSettingsReader implements SettingsReader {
    @Override
    public void readSetting(ClassLoader classLoader, ConfigSource configSource, Settings readTo) throws IOException {
        if (configSource == null || configSource.getStreamType() != StreamType.Xml) {
            return;
        }
        //
        Reader resourceReader = configSource.getResourceReader();
        if (resourceReader != null) {
            readXml(resourceReader, readTo);
            return;
        }
        //
        URL resourceUrl = configSource.getResourceUrl();
        if (resourceUrl != null) {
            InputStream asStream = ResourcesUtils.getResourceAsStream(classLoader, resourceUrl);
            if (asStream != null) {
                readXml(new InputStreamReader(asStream, Settings.DefaultCharset), readTo);
                return;
            }
            return;
        }
    }

    protected void readXml(Reader dataReader, Settings readTo) throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            factory.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = factory.newSAXParser();
            SaxXmlParser handler = new SaxXmlParser(readTo);
            InputSource inputSource = new InputSource(dataReader);
            parser.parse(inputSource, handler);
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException("parsing failed -> " + e.getMessage(), e);
        }
    }
}
