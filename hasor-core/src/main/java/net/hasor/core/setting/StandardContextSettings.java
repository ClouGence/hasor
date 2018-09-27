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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
/**
 * 继承自{@link InputStreamSettings}父类，该类自动装载 classpath 中所有静态配置文件。
 * 并且自动装载主配置文件（该配置文件应当只有一个）。
 * @version : 2013-9-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardContextSettings extends InputStreamSettings {
    /**主配置文件名称*/
    public static final  String MainSettingName = "hasor-config.xml";
    /**默认静态配置文件名称*/
    private static final String SechmaName      = "/META-INF/hasor.schemas";
    private              URI    settingURI;
    //
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
    public StandardContextSettings(InputStream mainSettings, StreamType type) throws IOException, URISyntaxException {
        if (mainSettings != null) {
            outInitLog("stream", mainSettings);
        }
        this.addStream(mainSettings, type);
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final String mainSettings) throws IOException, URISyntaxException {
        URL url = ResourcesUtils.getResource(mainSettings);
        if (url != null) {
            this.settingURI = url.toURI();
            outInitLog("string", mainSettings);
        }
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final File mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings.toURI();
            outInitLog("file", mainSettings);
        }
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(final URI mainSettings) throws IOException {
        if (mainSettings != null) {
            this.settingURI = mainSettings;
            outInitLog("uri", mainSettings);
        }
    }
    /**获取配置文件{@link URI}。*/
    public URI getSettingURI() {
        return this.settingURI;
    }
    //
    @Override
    protected void readyLoad() throws IOException {
        super.readyLoad();
        //1.装载所有 xxx-hconfig.xml
        List<URL> schemaUrlList = ResourcesUtils.getResources(SechmaName);
        for (URL schemaUrl : schemaUrlList) {
            InputStream sechmaStream = ResourcesUtils.getResourceAsStream(schemaUrl);
            List<String> readLines = IOUtils.readLines(sechmaStream, Settings.DefaultCharset);
            if (readLines == null || readLines.isEmpty()) {
                logger.warn("found nothing , {}", schemaUrl.toString());
                continue;
            }
            for (String sechma : readLines) {
                InputStream stream = ResourcesUtils.getResourceAsStream(sechma);
                if (stream != null) {
                    logger.info("addSechma '{}' in '{}'", sechma, schemaUrl.toString());
                    _addStream(stream, sechma);
                } else {
                    logger.error("cannot be read '{}' in '{}'", sechma);
                }
            }
        }
        //2.装载hasor-config.xml
        URI settingConfig = getSettingURI();
        if (settingConfig != null) {
            InputStream stream = ResourcesUtils.getResourceAsStream(settingConfig);
            if (stream != null) {
                logger.info("found = {}", settingConfig);
                _addStream(stream, settingConfig.toString());
            } else {
                logger.error("cannot be read {}", settingConfig);
            }
        }
    }
    private void _addStream(InputStream stream, String suffix) {
        if (suffix != null && suffix.toLowerCase().endsWith(".xml")) {
            this.addStream(stream, StreamType.Xml);
        } else {
            this.addStream(stream, StreamType.Properties);
        }
    }
    @Override
    public void refresh() throws IOException {
        logger.info("refresh -> cleanData and loadSettings...");
        this.cleanData();
        this.loadSettings();
    }
}