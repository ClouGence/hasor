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
package net.hasor.core.setting;
import net.hasor.core.Settings;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;

/**
 * 继承自{@link InputStreamSettings}父类，该类自动装载 classpath 中所有静态配置文件。
 * 并且自动装载主配置文件（该配置文件应当只有一个）。
 * @version : 2013-9-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardContextSettings extends InputStreamSettings {
    /**主配置文件名称*/
    public static final  String            MainSettingName = "hconfig.xml";
    /**默认静态配置文件名称*/
    private static final String            SchemaName      = "/META-INF/hasor.schemas";
    private              URI               settingURI;
    private static       Predicate<String> loadMatcher     = null;

    public static void setLoadMatcher(Predicate<String> loadMatcher) {
        StandardContextSettings.loadMatcher = loadMatcher;
    }

    private void outInitLog(String mode, Object oriResource) {
        if (logger.isInfoEnabled()) {
            if (this.settingURI != null) {
                logger.info("create Settings, type = StandardContextSettings, settingsType is [{}] mode, mainSettings = {}", mode, this.settingURI);
            } else {
                if (oriResource == null) {
                    logger.info("create Settings, type = StandardContextSettings, settingsType is [{}] mode, mainSettings is not specified.", mode);
                } else {
                    logger.error("create Settings, type = StandardContextSettings, settingsType is [{}] mode, mainSettings = {}, not found.", mode, oriResource);
                }
            }
        }
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings() throws IOException, URISyntaxException {
        this(StandardContextSettings.MainSettingName);
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(Reader mainSettings, StreamType type) throws IOException {
        if (mainSettings != null) {
            outInitLog("stream", mainSettings);
        }
        this.addReader(new ConfigSource(type, mainSettings));
        refresh();
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final String mainSettings) throws IOException, URISyntaxException {
        URL url = ResourcesUtils.getResource(mainSettings);
        if (url != null) {
            this.settingURI = url.toURI();
            outInitLog("string", mainSettings);
        }
        refresh();
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final File mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings.toURI();
            outInitLog("file", mainSettings);
        }
        refresh();
    }

    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final URI mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings;
            outInitLog("uri", mainSettings);
        }
        refresh();
    }

    /**获取配置文件{@link URI}。*/
    public URI getSettingURI() {
        return this.settingURI;
    }

    @Override
    protected void readyLoad() throws IOException {
        super.readyLoad();
        //1.装载所有 xxx-hconfig.xml
        List<URL> schemaUrlList = ResourcesUtils.getResources(SchemaName);
        for (URL schemaUrl : schemaUrlList) {
            InputStream schemaStream = ResourcesUtils.getResourceAsStream(schemaUrl);
            List<String> readLines = IOUtils.readLines(schemaStream, Settings.DefaultCharset);
            if (readLines.isEmpty()) {
                logger.warn("found nothing , {}", schemaUrl.toString());
                continue;
            }
            for (String schema : readLines) {
                if (loadMatcher != null && !loadMatcher.test(schema)) {
                    logger.info("addConfig '{}' ignore.", schema);
                    continue;
                }
                //
                try (InputStream stream = ResourcesUtils.getResourceAsStream(schema)) {
                    if (stream != null) {
                        logger.info("addConfig '{}' in '{}'", schema, schemaUrl.toString());
                        _addStream(ResourcesUtils.getResource(schema));
                    } else {
                        logger.error("cannot be read '{}' in '{}'", schema, schemaUrl.toString());
                    }
                }
            }
        }
        //2.装载 hconfig.xml
        URI settingConfig = getSettingURI();
        if (settingConfig != null) {
            try (InputStream stream = ResourcesUtils.getResourceAsStream(settingConfig)) {
                if (stream != null) {
                    logger.info("addConfig '{}'", settingConfig);
                    _addStream(settingConfig);
                } else {
                    logger.error("not found {}", settingConfig);
                }
            }
        }
    }

    private void _addStream(URL resourceUrl) {
        if (resourceUrl.toString().toLowerCase().endsWith(".xml")) {
            this.addReader(new ConfigSource(StreamType.Xml, resourceUrl));
        } else {
            this.addReader(new ConfigSource(StreamType.Properties, resourceUrl));
        }
    }

    private void _addStream(URI resourceUrl) {
        if (resourceUrl.toString().toLowerCase().endsWith(".xml")) {
            this.addReader(new ConfigSource(StreamType.Xml, resourceUrl));
        } else {
            this.addReader(new ConfigSource(StreamType.Properties, resourceUrl));
        }
    }

    @Override
    public void refresh() throws IOException {
        logger.debug("refresh -> cleanData and loadSettings...");
        this.cleanData();
        this.loadSettings();
    }
}
