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
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.classcode.MoreClassLoader;
import net.hasor.core.event.StandardEventManager;
import net.hasor.core.setting.AbstractSettings;
import net.hasor.core.setting.SettingValue;
import net.hasor.core.setting.UpdateValue;
import net.hasor.core.setting.xml.DefaultXmlNode;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.ScanClassPath;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * {@link Environment}接口实现类，集成该类的子类需要调用{@link #initEnvironment(Map, Map)}方法以初始化。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractEnvironment implements Environment {
    protected Logger              logger       = LoggerFactory.getLogger(getClass());
    private   String              workMode     = null;
    private   String[]            spanPackage  = null;
    private   ScanClassPath       scanUtils    = null;
    private   AbstractSettings    settings     = null;
    private   Object              context      = null;
    private   ClassLoader         rootLosder   = null;
    private   EventContext        eventManager = null;
    private   Map<String, String> envMap       = null;
    //
    /* --------------------------------------------------------------------------------- get/set */
    public AbstractEnvironment(Object context, AbstractSettings settings) {
        this.settings = settings;
        this.context = context;
        this.rootLosder = new MoreClassLoader();
        this.envMap = new ConcurrentHashMap<String, String>();
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
        return this.rootLosder;
    }
    /**设置类加载器*/
    public void setRootLosder(ClassLoader classLoader) {
        if (classLoader != null) {
            this.rootLosder = classLoader;
        }
    }
    /**设置扫描路径*/
    public void setSpanPackage(final String[] spanPackage) {
        this.spanPackage = spanPackage;
    }
    @Override
    public String[] getSpanPackage() {
        return this.spanPackage;
    }
    @Override
    public String getWorkMode() {
        return this.workMode;
    }
    @Override
    public final EventContext getEventContext() {
        if (this.eventManager == null) {
            int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
            this.eventManager = createEventManager(eventThreadPoolSize);
        }
        return this.eventManager;
    }
    //
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
        if (loadPackages == null) {
            loadPackages = new String[] { "" };
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
    public AbstractSettings getSettings() {
        return this.settings;
    }
    /**创建事件管理器*/
    protected EventContext createEventManager(int eventThreadPoolSize) {
        return new StandardEventManager(eventThreadPoolSize, "Hasor", this.getClassLoader());
    }
    //
    /* ------------------------------------------------------------------------------------ Toos */
    public String getPluginDir(Class<?> pluginType) {
        String subName = "_";
        if (pluginType != null) {
            subName = pluginType.getPackage().getName();
        }
        return evalString("%" + HASOR_PLUGIN_PATH + "%/" + subName + "/");
    }
    public String getWorkSpaceDir() {
        return evalString("%" + WORK_HOME + "%/");
    }
    //
    private String formatMap4log(final int colWidth, final Map<String, String> mapData) {
        /*输出系统环境变量日志*/
        StringBuilder outLog = new StringBuilder("");
        for (String key : mapData.keySet()) {
            String var = mapData.get(key);
            var = (var != null) ? (var.replace("\r", "\\r").replace("\n", "\\n")) : "";
            outLog.append(StringUtils.fixedString(' ', colWidth - key.length()));
            outLog.append(String.format("%s = %s", key, var));
            outLog.append('\n');
        }
        if (outLog.length() > 1) {
            outLog.deleteCharAt(outLog.length() - 1);
        }
        return outLog.toString();
    }
    //
    /* ------------------------------------------------------------------------------------- Env */
    @Override
    public void addEnvVar(final String varName, final String value) {
        if (StringUtils.isBlank(varName)) {
            if (logger.isWarnEnabled()) {
                logger.warn(varName + "{} env, name is empty.");
            }
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("var -> {} = {}.", varName, value);
        }
        this.envMap.put(varName.toUpperCase(), value);
    }
    @Override
    public void removeEnvVar(final String varName) {
        if (StringUtils.isBlank(varName)) {
            return;
        }
        this.envMap.remove(varName.toUpperCase());
        if (logger.isInfoEnabled()) {
            logger.info(varName + " env removed.");
        }
    }
    @Override
    public String evalString(String evalString) {
        if (StringUtils.isBlank(evalString)) {
            return "";
        }
        Pattern keyPattern = Pattern.compile("(?:%([\\w\\._-]+)%){1,1}");//  (?:%([\w\._-]+)%)
        Matcher keyM = keyPattern.matcher(evalString);
        Map<String, String> data = new HashMap<String, String>();
        while (keyM.find()) {
            String varKeyOri = keyM.group(1);
            String keyName = "%" + varKeyOri + "%";
            String var = this.envMap.get(varKeyOri.toUpperCase());
            if (var == null) {
                data.put(keyName, keyName);
            } else {
                data.put(keyName, evalString(var));
            }
        }
        String newEvalString = evalString;
        for (String key : data.keySet()) {
            newEvalString = newEvalString.replace(key, data.get(key));
        }
        if (logger.isInfoEnabled()) {
            logger.info("evalString '{}' eval to '{}'.", evalString, newEvalString);
        }
        return newEvalString;
    }
    //
    /* ------------------------------------------------------------------------------------ init */
    /**初始化方法*/
    protected final void initEnvironment(Map<String, String> frameworkEnvConfig, Map<String, String> customEnvConfig) throws IOException {
        // .load & init
        this.envMap = new ConcurrentHashMap<String, String>();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("load envVars...");
        }
        // .vars
        this.initEnvConfig(frameworkEnvConfig, customEnvConfig);
        this.refreshVariables();
        //
        // .Packages
        String[] spanPackages = this.getSettings().getStringArray("hasor.loadPackages", "net.hasor.core.*,net.hasor.plugins.*");
        Set<String> allPack = new HashSet<String>();
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
        ArrayList<String> spanPackagesArrays = new ArrayList<String>(allPack);
        Collections.sort(spanPackagesArrays);
        this.spanPackage = spanPackagesArrays.toArray(new String[spanPackagesArrays.size()]);
        if (this.logger.isInfoEnabled()) {
            StringBuilder packages = new StringBuilder("");
            for (int i = 0; i < this.spanPackage.length; i++) {
                if (i > 0) {
                    packages.append(", ");
                }
                packages.append(this.spanPackage[i]);
            }
            this.logger.info("loadPackages = " + packages);
        }
        //
        // .日志输出
        if (this.logger.isInfoEnabled()) {
            int keyMaxSize = 0;
            for (String key : this.envMap.keySet()) {
                keyMaxSize = key.length() >= keyMaxSize ? key.length() : keyMaxSize;
            }
            keyMaxSize = keyMaxSize + 2;
            StringBuilder sb = new StringBuilder();
            sb.append("EnvVars:");
            if (!this.envMap.isEmpty()) {
                sb.append("\n").append(this.formatMap4log(keyMaxSize, this.envMap));
                sb.append("\n").append(StringUtils.fixedString('-', 50));
            }
            this.logger.info(sb.toString());
        }
        this.workMode = this.evalString("%" + WORK_MODE + "%");
    }
    /**
     * 1st，System.getProperties()
     * 2st，System.getenv()
     * 3st，配置文件"hasor.environmentVar"
     * 4st，传入的配置
     * 5st，属性文件"env.config"
     * tips：如果指定了loadEnvConfig参数，那么将会忽略"env.config"配置文件。
     */
    private void initEnvConfig(Map<String, String> frameworkEnvConfig, Map<String, String> customEnvConfig) throws IOException {
        //
        // .1st，System.getProperties()
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("envVars.reload -> System.getProperties().");
        }
        Properties prop = System.getProperties();
        for (Object propKey : prop.keySet()) {
            String k = propKey.toString();
            Object v = prop.get(propKey);
            if (v != null) {
                this.envMap.put(k.toUpperCase(), v.toString());
            }
        }
        // .2st，System.getenv()
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("envVars.reload -> System.getenv().");
        }
        Map<String, String> envMap = System.getenv();
        for (String key : envMap.keySet()) {
            this.envMap.put(key.toUpperCase(), envMap.get(key));
        }
        // .3st，配置文件"hasor.environmentVar"
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("envVars.reload -> settings.");
        }
        Settings settings = getSettings();
        XmlNode[] xmlPropArray = settings.getXmlNodeArray("hasor.environmentVar");
        List<String> envNames = new ArrayList<String>();//用于收集环境变量名称
        for (XmlNode xmlProp : xmlPropArray) {
            for (XmlNode envItem : xmlProp.getChildren()) {
                envNames.add(envItem.getName().toUpperCase());
            }
        }
        for (String envItem : envNames) {
            if (this.envMap.containsKey(envItem)) {
                String val = this.envMap.get(envItem);
                if (StringUtils.isNotBlank(val)) {
                    this.logger.warn("environmentVar {} is define, ignored. value is {}", envItem, val);
                    continue;
                }
            }
            this.envMap.put(envItem.toUpperCase(), settings.getString("hasor.environmentVar." + envItem));
        }
        // .4st，传入的配置
        if (frameworkEnvConfig != null && !frameworkEnvConfig.isEmpty()) {
            this.logger.info("ignore 'env.config' use framework map, size = " + frameworkEnvConfig.size());
            for (String name : frameworkEnvConfig.keySet()) {
                this.envMap.put(name.toUpperCase(), frameworkEnvConfig.get(name));
            }
        }
        if (customEnvConfig != null && !customEnvConfig.isEmpty()) {
            this.logger.info("ignore 'env.config' use custom map, size = " + customEnvConfig.size());
            for (String name : customEnvConfig.keySet()) {
                this.envMap.put(name.toUpperCase(), customEnvConfig.get(name));
            }
            return;
        }
        //
        // .5st，外部属性文件"env.config"
        InputStream inStream = null;
        String workHome = this.evalString("%" + Environment.WORK_HOME + "%");
        File envFile = new File(workHome, EVN_FILE_NAME);
        String envFileName = envFile.getAbsolutePath();
        if (envFile.exists()) {
            if (envFile.isDirectory()) {
                this.logger.info("load 'env.config' failed(isDirectory) -> {}.", envFileName);
            } else if (!envFile.canRead()) {
                this.logger.info("load 'env.config' failed(can not read) -> {}.", envFileName);
            } else {
                inStream = new FileInputStream(envFile);
                this.logger.info("load 'env.config' form file -> {}.", envFileName);
            }
        }
        if (inStream == null) {
            URL inStreamURL = ResourcesUtils.getResource(EVN_FILE_NAME);
            this.logger.info("load 'env.config' use classpath -> {}.", (inStreamURL == null) ? "empty." : inStreamURL);
            if (inStreamURL != null) {
                inStream = ResourcesUtils.getResourceAsStream(inStreamURL);
            }
        }
        if (inStream != null) {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inStream, Settings.DefaultCharset));
            inStream.close();
            for (String name : properties.stringPropertyNames()) {
                String argKey = name.toUpperCase();
                String argVal = properties.getProperty(name);
                this.logger.info("load 'env.config' {} -> {}.", argKey, argVal);
                this.envMap.put(argKey, argVal);
            }
        }
    }
    //
    /* ------------------------------------------------------------------------------------ init */
    @Override
    public void refreshVariables() {
        this.getSettings().resetValues(new UpdateValue() {
            @Override
            public void update(SettingValue oldValue, Settings context) {
                ArrayList<Object> varArrays = new ArrayList<Object>(oldValue.getVarList());
                //
                for (int index = 0; index < varArrays.size(); index++) {
                    Object var = varArrays.get(index);
                    if (var instanceof DefaultXmlNode) {
                        DefaultXmlNode xmlVar = (DefaultXmlNode) var;
                        // .引用类型-直接更新引用对象的属性值。
                        String val = evalSettingString(xmlVar.getText());
                        xmlVar.setText(val);//引用类型
                        Map<String, String> attributeMap = xmlVar.getAttributeMap();
                        for (String attrKey : attributeMap.keySet()) {
                            String newValue = evalSettingString(attributeMap.get(attrKey));
                            attributeMap.put(attrKey, newValue);
                        }
                    } else if (var instanceof CharSequence) {
                        // .String类型-通过replace替换。
                        String oldVal = String.valueOf(var);
                        String newVal = evalSettingString(oldVal);
                        oldValue.replace(index, var, newVal);//值类型
                    } else {
                        //TODO
                    }
                }
            }
        });
    }
    private String evalSettingString(String evalString) {
        if (StringUtils.isBlank(evalString)) {
            return "";
        }
        Pattern keyPattern = Pattern.compile("(?:\\$\\{([\\w\\._-]+)\\}){1,1}");//  (?:\$\{([\w\._-]+)\}){1,1} -> ${...}
        Matcher keyM = keyPattern.matcher(evalString);
        Map<String, String> data = new HashMap<String, String>();
        while (keyM.find()) {
            String varKeyOri = keyM.group(1);
            String envKey = "%" + varKeyOri.toUpperCase() + "%";
            String var = this.evalString(envKey);
            if (envKey.equalsIgnoreCase(var)) {
                data.put("${" + varKeyOri + "}", envKey);
            } else {
                data.put("${" + varKeyOri + "}", var);
            }
        }
        String newEvalString = evalString;
        for (String key : data.keySet()) {
            newEvalString = newEvalString.replace(key, data.get(key));
        }
        if (this.logger.isInfoEnabled()) {
            if (!evalString.equalsIgnoreCase(newEvalString)) {
                this.logger.info("replace settingValue '{}' to '{}'.", evalString, newEvalString);
            }
        }
        return newEvalString;
    }
    //
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