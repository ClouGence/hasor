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
import net.hasor.utils.ResourcesUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * 继承自{@link InputStreamSettings}父类，该类自动装载 classpath 中所有静态配置文件。
 * 并且自动装载主配置文件（该配置文件应当只有一个）。
 * @version : 2013-9-9
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardContextSettings extends InputStreamSettings {
    /**主配置文件名称*/
    public static final String MainSettingName   = "hasor-config.xml";
    /**默认静态配置文件名称*/
    public static final String StaticSettingName = "static-config.xml";
    private URI settingURI;
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
        //1.装载所有static-config.xml
        List<URL> streamList = ResourcesUtils.getResources(StandardContextSettings.StaticSettingName);
        //2.排序，确保位于jar包中的资源排序优先级靠后。
        //因为覆盖加载是顺序的，因此先加载位于jar中的资源。然后覆盖它们。
        Collections.sort(streamList, new Comparator<URL>() {
            @Override
            public int compare(final URL o1, final URL o2) {
                String o1p = o1.getProtocol();
                String o2p = o2.getProtocol();
                if (o1p.equals(o2p)) {
                    return 0;
                }
                if (o1p.equals("jar")) {
                    return -1;
                }
                if (o2p.equals("jar")) {
                    return 1;
                }
                return 0;
            }
        });
        if (streamList == null || streamList.isEmpty()) {
            logger.warn("found nothing , use match {}", StandardContextSettings.StaticSettingName);
        } else {
            for (URL resURL : streamList) {
                InputStream stream = ResourcesUtils.getResourceAsStream(resURL);
                if (stream != null) {
                    logger.info("found = {}", resURL);
                    _addStream(stream, resURL.toString());
                } else {
                    logger.error("cannot be read {}", resURL);
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