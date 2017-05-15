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
import net.hasor.core.classcode.MoreClassLoader;
import net.hasor.core.setting.StandardContextSettings;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
/**
 * {@link Environment}接口实现类，继承自{@link AbstractEnvironment}。
 * @version : 2013-9-11
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardEnvironment extends AbstractEnvironment {
    public StandardEnvironment(Object context, File mainSettings) throws IOException {
        this(context, mainSettings, null, null, null);
    }
    //
    public StandardEnvironment(Object context, File mainSettings, Map<String, String> frameworkEnvConfig, Map<String, String> customEnvConfig, ClassLoader loader) throws IOException {
        this(context, (mainSettings != null ? mainSettings.toURI() : null), frameworkEnvConfig, customEnvConfig, loader);
    }
    public StandardEnvironment(Object context, URL mainSettings, Map<String, String> frameworkEnvConfig, Map<String, String> customEnvConfig, ClassLoader loader) throws URISyntaxException, IOException {
        this(context, (mainSettings != null ? mainSettings.toURI() : null), frameworkEnvConfig, customEnvConfig, loader);
    }
    public StandardEnvironment(Object context, String mainSettings, Map<String, String> frameworkEnvConfig, Map<String, String> customEnvConfig, ClassLoader loader) throws IOException, URISyntaxException {
        super(context, new StandardContextSettings(mainSettings));
        logger.info("create Environment, type = StandardEnvironment, mainSettings = {}", mainSettings);
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        this.getSettings().refresh();
        this.setRootLosder(new MoreClassLoader(loader));
        this.initEnvironment(frameworkEnvConfig, customEnvConfig);
    }
    public StandardEnvironment(Object context, URI mainSettings, Map<String, String> frameworkEnvConfig, Map<String, String> customEnvConfig, ClassLoader loader) throws IOException {
        super(context, new StandardContextSettings(mainSettings));
        logger.info("create Environment, type = StandardEnvironment, mainSettings = {}", mainSettings);
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        this.getSettings().refresh();
        this.setRootLosder(new MoreClassLoader(loader));
        this.initEnvironment(frameworkEnvConfig, customEnvConfig);
    }
}