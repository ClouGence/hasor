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
package net.hasor.core;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.ContainerCreater;
import net.hasor.core.context.StatusAppContext;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.setting.AbstractSettings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.core.setting.StreamType;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
/**
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public final class Hasor extends HashMap<String, String> {
    protected static Logger                           logger                 = LoggerFactory.getLogger(Hasor.class);
    private final    Object                           context;
    private          Object                           mainSettings           = TemplateAppContext.DefaultSettings;
    private          StreamType                       mainSettingsStreamType = null;
    private final    List<Module>                     moduleList             = new ArrayList<>();
    private          ClassLoader                      loader;
    private          ContainerCreater                 creater;
    private          Map<String, Map<String, Object>> initSettingMap         = new HashMap<>();
    private          boolean                          asSmaller              = false;
    //
    protected Hasor(Object context) {
        this.context = context;
    }
    //
    //
    public Hasor setMainSettings(File mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(URI mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(URL mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(String mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(Reader mainSettings, StreamType streamType) {
        this.mainSettings = mainSettings;
        this.mainSettingsStreamType = streamType;
        return this;
    }
    public Hasor setMainSettings(String encoding, InputStream mainSettings, StreamType streamType) throws UnsupportedEncodingException {
        this.mainSettings = new InputStreamReader(mainSettings, encoding);
        this.mainSettingsStreamType = streamType;
        return this;
    }
    //
    //
    public Hasor addSettings(String namespace, String key, Object value) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(key)) {
            return this;
        }
        Map<String, Object> stringMap = this.initSettingMap.computeIfAbsent(namespace, k -> new HashMap<>());
        stringMap.put(key, value);
        return this;
    }
    //
    //
    public Hasor putData(String key, String value) {
        this.put(key, value);
        return this;
    }
    public Hasor putAllData(Map<String, String> mapData) {
        this.putAll(mapData);
        return this;
    }
    public Hasor loadProperties(File resourceName) throws IOException {
        return loadProperties(new FileReader(resourceName));
    }
    public Hasor loadProperties(String resourceName) throws IOException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(resourceName);
        return loadProperties(new InputStreamReader(inStream, Settings.DefaultCharset));
    }
    public Hasor loadProperties(String encodeing, InputStream inStream) throws IOException {
        return loadProperties(new InputStreamReader(inStream, encodeing));
    }
    public Hasor loadProperties(Reader propertiesReader) throws IOException {
        Properties properties = new Properties();
        properties.load(propertiesReader);
        for (Object key : properties.keySet()) {
            this.put(key.toString(), properties.getProperty(key.toString()));
        }
        return this;
    }
    //
    //
    public Hasor addModules(List<Module> moduleList) {
        if (moduleList != null) {
            this.moduleList.addAll(moduleList);
        }
        return this;
    }
    public Hasor addModules(Module... modules) {
        if (modules != null) {
            this.moduleList.addAll(Arrays.asList(modules));
        }
        return this;
    }
    //
    //
    public Hasor setLoader(ClassLoader loader) {
        this.loader = loader;
        return this;
    }
    //
    //
    public Hasor asSmaller() {
        this.asSmaller = true;
        return this;
    }
    public Hasor asFull() {
        this.asSmaller = false;
        return this;
    }
    //
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public AppContext build(Module... modules) {
        return this.addModules(modules).build();
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public AppContext build() {
        //
        // .单独处理RUN_PATH
        String runPath = new File("").getAbsolutePath();
        this.putData("RUN_PATH", runPath);
        this.putData("RUN_MODE", this.asSmaller ? "smaller" : "none");
        if (logger.isInfoEnabled()) {
            logger.info("runMode at {}", this.get("RUN_MODE"));
            logger.info("runPath at {}", runPath);
        }
        //
        if (this.asSmaller) {
            this.putData("HASOR_LOAD_MODULE", "false");
            this.putData("HASOR_LOAD_EXTERNALBINDER", "false");
            StandardContextSettings.setLoadMatcher("/META-INF/hasor-framework/core-hconfig.xml"::equals);
        } else {
            StandardContextSettings.setLoadMatcher(null);
        }
        //
        try {
            AbstractSettings mainSettings = null;
            if (this.mainSettings == null) {
                logger.info("create AppContext ,mainSettings = {}", TemplateAppContext.DefaultSettings);
                mainSettings = new StandardContextSettings(TemplateAppContext.DefaultSettings);
            } else if (this.mainSettings instanceof String) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                mainSettings = new StandardContextSettings((String) this.mainSettings);
            } else if (this.mainSettings instanceof File) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                mainSettings = new StandardContextSettings((File) this.mainSettings);
            } else if (this.mainSettings instanceof URI) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                mainSettings = new StandardContextSettings((URI) this.mainSettings);
            } else if (this.mainSettings instanceof URL) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                mainSettings = new StandardContextSettings(((URL) this.mainSettings).toURI());
            } else if (this.mainSettings instanceof Reader && this.mainSettingsStreamType != null) {
                logger.info("create AppContext ,mainSettingsStreamType = {} , ", this.mainSettingsStreamType);
                mainSettings = new StandardContextSettings((Reader) this.mainSettings, this.mainSettingsStreamType);
            } else {
                logger.error("create AppContext ,mainSettings Unsupported.");
                throw new UnsupportedOperationException();
            }
            //
            for (Map.Entry<String, Map<String, Object>> namespaceData : this.initSettingMap.entrySet()) {
                String namespaceKey = namespaceData.getKey();
                Map<String, Object> value = namespaceData.getValue();
                if (StringUtils.isBlank(namespaceKey) || value.isEmpty()) {
                    continue;
                }
                for (Map.Entry<String, Object> settingKV : value.entrySet()) {
                    mainSettings.setSetting(settingKV.getKey(), settingKV.getValue(), namespaceKey);
                }
            }
            //
            Environment env = new StandardEnvironment(this.context, mainSettings, this, this.loader);
            BeanContainer container = null;
            if (this.creater != null) {
                container = this.creater.create(env);
            } else {
                container = new BeanContainer();
            }
            //
            AppContext appContext = new StatusAppContext(env, Objects.requireNonNull(container));
            appContext.start(this.moduleList.toArray(new Module[0]));
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    //
    /**用Builder的方式创建{@link AppContext}容器。*/
    public static Hasor create() {
        return new Hasor(null);
    }
    /**用Builder的方式创建{@link AppContext}容器。*/
    public static Hasor create(Object context) {
        return new Hasor(context);
    }
}