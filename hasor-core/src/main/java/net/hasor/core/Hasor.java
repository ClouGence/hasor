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
    private          Map<String, Map<String, Object>> initSettingMap         = new HashMap<>();
    private          Level                            asLevel                = Level.Full;

    protected Hasor(Object context) {
        this.context = context;
    }

    /** 加载框架的规模 */
    public static enum Level {
        /**
         * 微小的，放弃一切插件加载，并且只处理 hasor-core 的加载
         * 下面环境变量会被设置
         *  - HASOR_LOAD_MODULE 为 false
         *  - HASOR_LOAD_EXTERNALBINDER 为 false
         */
        Tiny(),
        /** 核心部分，只完整的加载 hasor-core。 */
        Core(),
        /** 完整加载框架和可以发现的所有插件模块。 */
        Full()
    }

    public Hasor mainSettingWith(File mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }

    public Hasor mainSettingWith(URI mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }

    public Hasor mainSettingWith(URL mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }

    public Hasor mainSettingWith(String mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }

    public Hasor mainSettingWith(Reader mainSettings, StreamType streamType) {
        this.mainSettings = mainSettings;
        this.mainSettingsStreamType = streamType;
        return this;
    }

    public Hasor mainSettingWith(String encoding, InputStream mainSettings, StreamType streamType) throws UnsupportedEncodingException {
        this.mainSettings = new InputStreamReader(mainSettings, encoding);
        this.mainSettingsStreamType = streamType;
        return this;
    }

    public Hasor addSettings(String namespace, String key, Object value) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(key)) {
            return this;
        }
        Map<String, Object> stringMap = this.initSettingMap.computeIfAbsent(namespace, k -> new HashMap<>());
        stringMap.put(key, value);
        return this;
    }

    public Hasor addVariable(String key, String value) {
        this.put(key, value);
        return this;
    }

    public Hasor addVariableMap(Map<String, String> mapData) {
        this.putAll(mapData);
        return this;
    }

    public Hasor loadVariables(File resourceName) throws IOException {
        return loadVariables(new FileReader(resourceName));
    }

    public Hasor loadVariables(String resourceName) throws IOException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(resourceName);
        return loadVariables(new InputStreamReader(inStream, Settings.DefaultCharset));
    }

    public Hasor loadVariables(String encodeing, InputStream inStream) throws IOException {
        return loadVariables(new InputStreamReader(inStream, encodeing));
    }

    public Hasor loadVariables(Reader propertiesReader) throws IOException {
        Properties properties = new Properties();
        properties.load(propertiesReader);
        for (Object key : properties.keySet()) {
            this.put(key.toString(), properties.getProperty(key.toString()));
        }
        return this;
    }

    public Hasor importVariablesToSettings() {
        return importVariablesToSettings(Settings.DefaultNameSpace);
    }

    public Hasor importVariablesToSettings(String namespace) {
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is not null.");
        }
        for (String key : this.keySet()) {
            addSettings(namespace, key, get(key));
        }
        return this;
    }

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

    public Hasor parentClassLoaderWith(ClassLoader loader) {
        this.loader = loader;
        return this;
    }

    /**
     * 微小的，放弃一切插件加载，并且只处理 hasor-core 的加载
     * 下面环境变量会被设置
     *  - HASOR_LOAD_MODULE 为 false
     *  - HASOR_LOAD_EXTERNALBINDER 为 false
     */
    public Hasor asTiny() {
        this.asLevel = Level.Tiny;
        return this;
    }

    /** 核心部分，只完整的加载 hasor-core。 */
    public Hasor asCore() {
        this.asLevel = Level.Core;
        return this;
    }

    /** 完整加载框架和可以发现的所有插件模块。 */
    public Hasor asFull() {
        this.asLevel = Level.Full;
        return this;
    }

    /**用简易的方式创建{@link AppContext}容器。*/
    public AppContext build(Module... modules) {
        if (modules != null) {
            this.addModules(modules);
        }
        //
        // .单独处理RUN_PATH
        String runPath = new File("").getAbsolutePath();
        this.addVariable("RUN_PATH", runPath);
        this.addVariable("RUN_MODE", this.asLevel.name());
        if (logger.isInfoEnabled()) {
            logger.info("runMode at {}", this.get("RUN_MODE"));
            logger.info("runPath at {}", runPath);
        }
        //
        if (this.asLevel == Level.Tiny) {
            this.addVariable("HASOR_LOAD_MODULE", "false");
            this.addVariable("HASOR_LOAD_EXTERNALBINDER", "false");
        }
        if (this.asLevel == Level.Tiny || this.asLevel == Level.Core) {
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
            AppContext appContext = new StatusAppContext(env);
            appContext.start(this.moduleList.toArray(new Module[0]));
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /**用Builder的方式创建{@link AppContext}容器。*/
    public static Hasor create() {
        return new Hasor(null);
    }

    /**用Builder的方式创建{@link AppContext}容器。*/
    public static Hasor create(Object context) {
        return new Hasor(context);
    }
}