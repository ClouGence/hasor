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
package net.hasor.core.environment;
import net.hasor.core.Environment;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.core.setting.StreamType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
/**
 * {@link Environment}接口实现类，继承自{@link AbstractEnvironment}。
 * @version : 2013-9-11
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardEnvironment extends AbstractEnvironment {
    public StandardEnvironment(Object context, InputStream mainSettings, StreamType type) throws IOException, URISyntaxException {
        this(context, mainSettings, type, true);
    }
    public StandardEnvironment(Object context, String mainSettings) throws IOException, URISyntaxException {
        this(context, mainSettings, true);
    }
    public StandardEnvironment(Object context, File mainSettings) throws IOException {
        this(context, mainSettings, true);
    }
    public StandardEnvironment(Object context, URL mainSettings) throws URISyntaxException, IOException {
        this(context, mainSettings, true);
    }
    public StandardEnvironment(Object context, URI mainSettings) throws IOException {
        this(context, mainSettings, true);
    }
    //
    public StandardEnvironment(Object context, InputStream mainSettings, StreamType type, boolean loadEnvConfig) throws IOException, URISyntaxException {
        super(context, new StandardContextSettings(mainSettings, type));
        logger.info("create Environment, type = StandardEnvironment, settingsType is [string] mode, mainSettings = {}", mainSettings);
        this.getSettings().refresh();
        this.initEnvironment(loadEnvConfig);
    }
    public StandardEnvironment(Object context, String mainSettings, boolean loadEnvConfig) throws IOException, URISyntaxException {
        super(context, new StandardContextSettings(mainSettings));
        logger.info("create Environment, type = StandardEnvironment, settingsType is [string] mode, mainSettings = {}", mainSettings);
        this.getSettings().refresh();
        this.initEnvironment(loadEnvConfig);
    }
    public StandardEnvironment(Object context, File mainSettings, boolean loadEnvConfig) throws IOException {
        super(context, new StandardContextSettings(mainSettings));
        logger.info("create Environment, type = StandardEnvironment, settingsType is [file] mode, mainSettings = {}", mainSettings);
        this.getSettings().refresh();
        this.initEnvironment(loadEnvConfig);
    }
    public StandardEnvironment(Object context, URL mainSettings, boolean loadEnvConfig) throws URISyntaxException, IOException {
        super(context, new StandardContextSettings(mainSettings.toURI()));
        logger.info("create Environment, type = StandardEnvironment, settingsType is [url] mode, mainSettings = {}", mainSettings);
        this.getSettings().refresh();
        this.initEnvironment(loadEnvConfig);
    }
    public StandardEnvironment(Object context, URI mainSettings, boolean loadEnvConfig) throws IOException {
        super(context, new StandardContextSettings(mainSettings));
        logger.info("create Environment, type = StandardEnvironment, settingsType is [uri] mode, mainSettings = {}", mainSettings);
        this.getSettings().refresh();
        this.initEnvironment(loadEnvConfig);
    }
}