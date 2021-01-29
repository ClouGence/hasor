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
import net.hasor.core.setting.provider.ConfigSource;
import net.hasor.core.setting.provider.SettingsReader;
import net.hasor.core.setting.provider.StreamType;
import net.hasor.core.setting.provider.properties.PropertiesSettingsReader;
import net.hasor.core.setting.provider.xml.XmlSettingsReader;
import net.hasor.core.setting.provider.yaml.YamlSettingsReader;
import net.hasor.utils.ResourcesUtils;

import java.io.*;
import java.util.LinkedList;

/***
 * 传入{@link InputStream}的方式获取{@link Settings}接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class InputStreamSettings extends BasicSettings implements IOSettings {
    private final LinkedList<ConfigSource> pendingConfigSource = new LinkedList<>();

    /**子类决定如何添加资源*/
    public InputStreamSettings() {
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public synchronized boolean addReader(String mainSettings, StreamType type) {
        return this.addReader(new StringReader(mainSettings), type);
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    public synchronized boolean addReader(Reader mainSettings, StreamType type) {
        return this.addReader(new ConfigSource(DefaultNameSpace, type, mainSettings));
    }

    /** 将一个配置源添加到列表，后面会通过 load 方法加载这些数据。
     * 注意：待处理列表中的数据一旦装载完毕将会从待处理列表中清除出去。*/
    protected synchronized boolean addReader(ConfigSource configSource) {
        if (configSource == null || configSource.getStreamType() == null) {
            return false;
        }
        if (configSource.getResourceUrl() == null && configSource.getResourceReader() == null) {
            return false;
        }
        for (ConfigSource cs : this.pendingConfigSource) {
            if (cs.equals(configSource)) {
                return false;
            }
        }
        return this.pendingConfigSource.add(configSource);
    }

    /**load装载所有待处理的流，如果没有待处理流则直接return。*/
    @Override
    public synchronized int loadSettings() throws IOException {
        this.readyLoad();//准备装载
        int loadCount = 0;
        if (this.pendingConfigSource.isEmpty()) {
            logger.info("loadSettings finish -> there is no need to be load.");
            return 0;
        }
        //构建装载环境
        ConfigSource entity = null;
        try {
            logger.debug("parsing...");
            while ((entity = this.pendingConfigSource.removeFirst()) != null) {
                loadCount++;
                Reader dataReader = null;
                try {
                    // 拿到Reader
                    dataReader = entity.getResourceReader();
                    if (dataReader == null && entity.getResourceUrl() != null) {
                        InputStream asStream = ResourcesUtils.getResourceAsStream(entity.getResourceUrl());
                        if (asStream != null) {
                            dataReader = new InputStreamReader(asStream, Settings.DefaultCharset);
                        }
                    }
                    //
                    if (dataReader == null) {
                        continue;
                    }
                    //
                    SettingsReader settingsReader;
                    switch (entity.getStreamType()) {
                        case Xml:
                            settingsReader = new XmlSettingsReader();
                            break;
                        case Yaml:
                            settingsReader = new YamlSettingsReader();
                            break;
                        case Properties:
                            settingsReader = new PropertiesSettingsReader();
                            break;
                        default:
                            throw new UnsupportedOperationException(entity.getStreamType() + " Unsupported.");
                    }
                    settingsReader.readSetting(null, entity, this);
                } catch (Exception e) {
                    logger.error("load Config " + entity.toString() + " failed -> " + e.getMessage());
                    throw e;
                } finally {
                    if (dataReader != null) {
                        dataReader.close();
                    }
                }
                if (this.pendingConfigSource.isEmpty()) {
                    break;
                }
            }
        } catch (Throwable e) {
            String errorMessage = "parsing failed -> ";
            logger.error("parsing failed -> " + e.getMessage(), e);
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(errorMessage + e.getMessage(), e);
            }
        }
        logger.debug("parsing finish.");
        this.loadFinish();//完成装载
        logger.debug("loadSettings finish.");
        return loadCount;
    }

    /**准备装载*/
    protected void readyLoad() throws IOException {
        //
    }

    /**完成装载*/
    protected void loadFinish() throws IOException {
        //
    }
}
