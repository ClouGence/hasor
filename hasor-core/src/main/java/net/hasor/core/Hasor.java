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
import net.hasor.core.setting.BasicSettings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.core.setting.provider.StreamType;
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
public final class Hasor {
    protected final static Logger                           logger                 = LoggerFactory.getLogger(Hasor.class);
    private final          Object                           context;
    private                Object                           mainSettings           = TemplateAppContext.DefaultSettings;
    private                StreamType                       mainSettingsStreamType = null;
    private final          List<Module>                     moduleList             = new ArrayList<>();
    private                ClassLoader                      loader;
    private final          Map<String, Map<String, Object>> initSettingMap         = new HashMap<>();
    private final          Map<String, String>              variableMap            = new HashMap<>();
    private                Level                            asLevel                = Level.Full;

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

    public Hasor addSettings(String namespace, String key, Object value) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("namespace or key is null.");
        }
        Map<String, Object> stringMap = this.initSettingMap.computeIfAbsent(namespace, k -> new HashMap<>());
        stringMap.put(key, value);
        return this;
    }

    /** 加载配置文件到默认配置空间。 */
    public Hasor loadSettings(Properties properties) {
        return loadSettings(Settings.DefaultNameSpace, properties);
    }

    /** 加载配置文件到指定配置空间。 */
    public Hasor loadSettings(String namespace, Properties properties) {
        if (properties != null) {
            for (Object key : properties.keySet()) {
                this.addSettings(namespace, key.toString(), properties.getProperty(key.toString()));
            }
        }
        return this;
    }

    /** 添加 Hasor 环境变量 */
    public Hasor addVariable(String key, String value) {
        this.variableMap.put(key, value);
        return this;
    }

    /** 添加 Hasor 环境变量 */
    public Hasor addVariableMap(Map<String, String> mapData) {
        this.variableMap.putAll(mapData);
        return this;
    }

    /** 从文件中加载环境变量到 Hasor 框架中 */
    public Hasor loadVariables(File resourceName) throws IOException {
        return loadVariables(new FileReader(resourceName));
    }

    /** 从资源文件中加载环境变量到 Hasor 框架中 */
    public Hasor loadVariables(String resourceName) throws IOException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(resourceName);
        return loadVariables(new InputStreamReader(inStream, Settings.DefaultCharset));
    }

    /** 从资源文件中加载环境变量到 Hasor 框架中 */
    public Hasor loadVariables(String encodeing, InputStream inStream) throws IOException {
        return loadVariables(new InputStreamReader(inStream, encodeing));
    }

    /** 从属性对象中加载环境变量到 Hasor 框架中 */
    public Hasor loadVariables(Properties properties) {
        if (properties != null) {
            for (Object key : properties.keySet()) {
                this.variableMap.put(key.toString(), properties.getProperty(key.toString()));
            }
        }
        return this;
    }

    /** 从资源文件中加载环境变量到 Hasor 框架中 */
    public Hasor loadVariables(Reader propertiesReader) throws IOException {
        Properties properties = new Properties();
        properties.load(propertiesReader);
        return loadVariables(properties);
    }

    /** 导入环境变量到配置（导入目标是：Settings.DefaultNameSpace） */
    public Hasor importVariablesToSettings() {
        return importVariablesToSettings(Settings.DefaultNameSpace);
    }

    /** 导入环境变量到配置（导入目标自定义） */
    public Hasor importVariablesToSettings(String namespace) {
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is not null.");
        }
        for (String key : this.variableMap.keySet()) {
            addSettings(namespace, key, this.variableMap.get(key));
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

    /**用简易的方式创建{@link Settings}容器。*/
    public Settings buildSettings() {
        // .单独处理RUN_PATH
        String runPath = new File("").getAbsolutePath();
        this.addVariable("RUN_PATH", runPath);
        this.addVariable("RUN_MODE", this.asLevel.name());
        if (logger.isInfoEnabled()) {
            logger.info("runMode at {} ,runPath at {}", this.variableMap.get("RUN_MODE"), runPath);
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
            StandardContextSettings mainSettings = null;
            if (this.mainSettings == null) {
                mainSettings = new StandardContextSettings(TemplateAppContext.DefaultSettings);
            } else if (this.mainSettings instanceof String) {
                if (StringUtils.isBlank(this.mainSettings.toString())) {
                    this.mainSettings = TemplateAppContext.DefaultSettings;
                }
                mainSettings = new StandardContextSettings((String) this.mainSettings);
            } else if (this.mainSettings instanceof File) {
                mainSettings = new StandardContextSettings((File) this.mainSettings);
            } else if (this.mainSettings instanceof URI) {
                mainSettings = new StandardContextSettings((URI) this.mainSettings);
            } else if (this.mainSettings instanceof URL) {
                mainSettings = new StandardContextSettings(((URL) this.mainSettings).toURI());
            } else if (this.mainSettings instanceof Reader && this.mainSettingsStreamType != null) {
                mainSettings = new StandardContextSettings((Reader) this.mainSettings, this.mainSettingsStreamType);
            } else {
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
            return mainSettings;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    /**用简易的方式创建{@link Environment}容器。*/
    public Environment buildEnvironment() {
        BasicSettings buildSettings = (BasicSettings) buildSettings();
        return new StandardEnvironment(this.context, buildSettings, this.variableMap, this.loader);
    }

    /**用简易的方式创建{@link AppContext}容器。*/
    public AppContext build(Module... modules) {
        if (modules != null) {
            this.addModules(modules);
        }
        //
        try {
            Environment env = this.buildEnvironment();
            AppContext appContext = new StatusAppContext(env);
            appContext.start(this.moduleList.toArray(new Module[0]));
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntime(e);
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
