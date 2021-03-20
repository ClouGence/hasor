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
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.aop.AopClassLoader;
import net.hasor.core.event.StandardEventManager;
import net.hasor.core.setting.BasicSettings;
import net.hasor.core.setting.SettingNode;
import net.hasor.utils.ScanClassPath;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link Environment}接口实现类，集成该类的子类需要调用{@link #initEnvironment(Map)}方法以初始化。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractEnvironment implements Environment {
    protected static Logger              logger       = LoggerFactory.getLogger(AbstractEnvironment.class);
    private          String[]            spanPackage  = null;
    private          ScanClassPath       scanUtils    = null;
    private          BasicSettings       settings     = null;
    private          Object              context      = null;
    private          ClassLoader         rootLoader   = null;
    private          EventContext        eventManager = null;
    private          Map<String, String> envMap       = null;

    /* --------------------------------------------------------------------------------- get/set */
    public AbstractEnvironment(Object context, BasicSettings settings) {
        this.settings = settings;
        this.context = context;
        this.rootLoader = new AopClassLoader();
        this.envMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getContext() {
        return this.context;
    }

    /**设置或更新 context */
    public void setContext(final Object context) {
        this.context = context;
    }

    /**获取当创建Bean时使用的{@link ClassLoader}*/
    public ClassLoader getClassLoader() {
        return this.rootLoader;
    }

    /**设置类加载器*/
    public void setRootLoader(ClassLoader classLoader) {
        if (classLoader != null) {
            this.rootLoader = classLoader;
        }
    }

    /**设置扫描路径*/
    public void setSpanPackage(String[] spanPackage) {
        this.spanPackage = spanPackage;
    }

    @Override
    public String[] getSpanPackage() {
        return this.spanPackage;
    }

    @Override
    public final EventContext getEventContext() {
        return this.eventManager;
    }

    @Override
    public Hasor.Level runMode() {
        String runMode = this.getVariable("RUN_MODE");
        for (Hasor.Level level : Hasor.Level.values()) {
            if (level.name().equalsIgnoreCase(runMode)) {
                return level;
            }
        }
        return null;
    }
    // ------------------------------------------------------------------------------- findClass */

    @Override
    public Set<Class<?>> findClass(final Class<?> featureType) {
        return this.findClass(featureType, this.spanPackage);
    }

    /** 在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类） */
    public Set<Class<?>> findClass(final Class<?> featureType, String[] loadPackages) {
        if (featureType == null) {
            return null;
        }
        if (loadPackages == null || loadPackages.length == 0) {
            return null;
        }
        if (this.scanUtils == null) {
            this.scanUtils = ScanClassPath.newInstance(loadPackages);
        }
        return this.scanUtils.getClassSet(featureType);
    }

    /** 在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类） */
    public Set<Class<?>> findClass(final Class<?> featureType, String loadPackages) {
        if (featureType == null) {
            return null;
        }
        loadPackages = loadPackages == null ? "" : loadPackages;
        String[] spanPackage = loadPackages.split(",");
        return this.findClass(featureType, spanPackage);
    }

    @Override
    public BasicSettings getSettings() {
        return this.settings;
    }

    /**创建事件管理器*/
    protected EventContext createEventManager(int eventThreadPoolSize) {
        return new StandardEventManager(eventThreadPoolSize, "Hasor", this.getClassLoader());
    }

    /* ------------------------------------------------------------------------------------- Env */

    public String[] getVariableNames() {
        return envMap.keySet().toArray(new String[0]);
    }

    public String getVariable(String varName) {
        return evalString("%" + varName + "%");
    }

    @Override
    public void addVariable(final String varName, final String value) {
        if (StringUtils.isBlank(value)) {
            removeVariable(varName);
            return;
        }
        if (StringUtils.isBlank(varName)) {
            logger.warn(varName + "{} env, name is empty.");
            return;
        }
        logger.info("var -> {} = {}.", varName, value);
        this.envMap.put(varName.toUpperCase(), value);
    }

    @Override
    public void removeVariable(final String varName) {
        if (StringUtils.isBlank(varName)) {
            return;
        }
        this.envMap.remove(varName.toUpperCase());
        logger.info(varName + " env removed.");
    }

    @Override
    public String evalString(String evalString) {
        if (StringUtils.isBlank(evalString)) {
            return "";
        }
        Pattern keyPattern = Pattern.compile("(?:%([\\w\\._-]+)%){1,1}");//  (?:%([\w\._-]+)%)
        Matcher keyM = keyPattern.matcher(evalString);
        Map<String, String> data = new HashMap<>();
        while (keyM.find()) {
            String varKeyOri = keyM.group(1);
            String keyName = "%" + varKeyOri + "%";
            String var = this.envMap.get(varKeyOri.toUpperCase());
            if (var == null) {
                data.put(keyName, "");
            } else {
                data.put(keyName, evalString(var));
            }
        }
        String newEvalString = evalString;
        for (String key : data.keySet()) {
            newEvalString = newEvalString.replace(key, data.get(key));
        }
        logger.debug("evalString '{}' eval to '{}'.", evalString, newEvalString);
        return newEvalString;
    }

    /* ------------------------------------------------------------------------------------ init */

    /**初始化方法*/
    protected final void initEnvironment(Map<String, String> frameworkEnvConfig) {
        // .load & init
        this.envMap = new ConcurrentHashMap<>();
        logger.debug("load envVars...");
        // .vars
        this.initEnvConfig(frameworkEnvConfig);
        this.refreshVariables();
        //
        // .Packages
        String[] spanPackages = this.getSettings().getStringArray("hasor.loadPackages", "net.hasor.core.*,net.hasor.plugins.*");
        Set<String> allPack = new HashSet<>();
        for (String packs : spanPackages) {
            if (StringUtils.isBlank(packs)) {
                continue;
            }
            String[] packArray = packs.split(",");
            for (String pack : packArray) {
                if (StringUtils.isBlank(packs)) {
                    continue;
                }
                allPack.add(pack.trim());
            }
        }
        ArrayList<String> spanPackagesArrays = new ArrayList<>(allPack);
        Collections.sort(spanPackagesArrays);
        this.spanPackage = spanPackagesArrays.toArray(new String[0]);
        if (logger.isInfoEnabled()) {
            StringBuilder packages = new StringBuilder("");
            for (int i = 0; i < this.spanPackage.length; i++) {
                if (i > 0) {
                    packages.append(", ");
                }
                packages.append(this.spanPackage[i]);
            }
            logger.info("loadPackages = " + packages);
        }
        //
        int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
        this.eventManager = createEventManager(eventThreadPoolSize);
    }

    /**
     * 1st，System.getProperties()
     * 2st，System.getenv()
     * 3st，配置文件"hasor.environmentVar"
     * 4st，传入的配置
     */
    private void initEnvConfig(Map<String, String> frameworkEnvConfig) {
        //
        // .1st，System.getProperties()
        Properties prop = System.getProperties();
        for (Object propKey : prop.keySet()) {
            String k = propKey.toString();
            Object v = prop.get(propKey);
            if (v != null) {
                this.envMap.put(k.toUpperCase(), v.toString());
            }
        }
        // .2st，System.getenv()
        Map<String, String> envMap = System.getenv();
        for (String key : envMap.keySet()) {
            this.envMap.put(key.toUpperCase(), envMap.get(key));
        }
        // .3st，配置文件"hasor.environmentVar"
        Settings settings = getSettings();
        SettingNode[] environmentVar = settings.getNodeArray("hasor.environmentVar");
        List<String> envNames = new ArrayList<>();//用于收集环境变量名称
        for (SettingNode varGroups : environmentVar) {
            for (SettingNode varItem : varGroups.getSubNodes()) {
                envNames.add(varItem.getName().toUpperCase());
            }
        }
        for (String envItem : envNames) {
            if (this.envMap.containsKey(envItem)) {
                String val = this.envMap.get(envItem);
                if (StringUtils.isNotBlank(val)) {
                    logger.warn("environmentVar {} is define, ignored. value is {}", envItem, val);
                    continue;
                }
            }
            this.envMap.put(envItem.toUpperCase(), settings.getString("hasor.environmentVar." + envItem, ""));
        }
        // .4st，传入的配置
        if (frameworkEnvConfig != null && !frameworkEnvConfig.isEmpty()) {
            logger.debug("use framework map, size = " + frameworkEnvConfig.size());
            for (String name : frameworkEnvConfig.keySet()) {
                String envStr = frameworkEnvConfig.get(name);
                if (envStr == null) {
                    envStr = "";
                    logger.debug("framework key {} is empty. ", name);
                }
                this.envMap.put(name.toUpperCase(), envStr);
            }
        }
    }

    /* ------------------------------------------------------------------------------------ init */
    @Override
    public void refreshVariables() {
        this.getSettings().resetValues((dataNode, context) -> {
            String[] values = dataNode.getValues();
            //
            for (int index = 0; index < values.length; index++) {
                String oldVar = values[index];
                String newVal = evalSettingString(oldVar);
                if (!StringUtils.equals(oldVar, newVal)) {
                    dataNode.replace(index, newVal);
                }
            }
        });
    }

    private String evalSettingString(String evalString) {
        if (StringUtils.isBlank(evalString)) {
            return "";
        }
        //
        String newEvalString = new GenericTokenParser(new String[] { "${" }, "}", (builder, token, content) -> {
            String varKey = content;
            String varDefault = "";
            int defaultIndexOf = content.indexOf(":");
            if (defaultIndexOf != -1) {
                varDefault = content.substring(defaultIndexOf + 1);
                varKey = content.substring(0, defaultIndexOf);
            }
            //
            String envKey = "%" + varKey.toUpperCase() + "%";
            String dataKey = "${" + varKey + "}";
            //
            String var = this.evalString(envKey);
            if (StringUtils.isBlank(var) && StringUtils.isNotBlank(varDefault)) {
                var = varDefault;
            }
            //
            if (envKey.equalsIgnoreCase(var)) {
                return envKey;
            } else {
                return var;
            }
        }).parse(evalString);
        //
        if (!evalString.equalsIgnoreCase(newEvalString)) {
            logger.debug("replace settingValue '{}' to '{}'.", evalString, newEvalString);
        }
        return newEvalString;
    }

    /* ----------------------------------------------------------------------------------- toos */
    @Override
    public String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            logger.error("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }
}
