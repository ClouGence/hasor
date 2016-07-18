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
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import org.more.util.ExceptionUtils;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 该类负责处理环境变量相关操作。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class EnvVars {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Environment                       environment;
    /*最终使用的环境变量Map*/
    private ConcurrentHashMap<String, String> envMap;
    /*用户通过Api添加的环境变量Map*/
    private ConcurrentHashMap<String, String> userEnvMap;
    //
    public EnvVars(final Environment environment) {
        this.environment = environment;
        this.envMap = new ConcurrentHashMap<String, String>();
        this.userEnvMap = new ConcurrentHashMap<String, String>();
    }
    public void addEnvVar(final String envName, final String envValue) {
        if (StringUtils.isBlank(envName)) {
            if (logger.isWarnEnabled()) {
                logger.warn(envName + "{} env, name is empty.");
            }
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("var -> {} = {}.", envName, envValue);
        }
        this.userEnvMap.put(envName.toUpperCase(), StringUtils.isBlank(envValue) ? "" : envValue);
    }
    public void remoteEnvVar(final String varName) {
        if (StringUtils.isBlank(varName)) {
            return;
        }
        this.userEnvMap.remove(varName.toUpperCase());
        if (logger.isInfoEnabled()) {
            logger.info(varName + " env removed.");
        }
    }
    //
    /**特殊配置的环境变量*/
    protected void configEnvironment(Map<String, String> envMap) {
        // .内部配置文件配置
        Settings settings = environment.getSettings();
        XmlNode[] xmlPropArray = settings.getXmlNodeArray("hasor.environmentVar");
        List<String> envNames = new ArrayList<String>();//用于收集环境变量名称
        for (XmlNode xmlProp : xmlPropArray) {
            for (XmlNode envItem : xmlProp.getChildren()) {
                envNames.add(envItem.getName().toUpperCase());
            }
        }
        for (String envItem : envNames) {
            envMap.put(envItem.toUpperCase(), settings.getString("hasor.environmentVar." + envItem));
        }
        // .外部环境变量配置
        try {
            InputStream inStream = ResourcesUtils.getResourceAsStream(AbstractEnvironment.EVN_FILE_NAME);
            if (inStream == null) {
                String workHome = this.envVar(Environment.WORK_HOME);
                File envFile = new File(workHome, AbstractEnvironment.EVN_FILE_NAME);
                if (envFile.isFile() && envFile.canRead()) {
                    inStream = new FileInputStream(envFile);
                }
            }
            if (inStream != null) {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(inStream, Settings.DefaultCharset));
                inStream.close();
                for (String name : properties.stringPropertyNames()) {
                    envMap.put(name.toUpperCase(), properties.getProperty(name));
                }
            }
        } catch (IOException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        /*单独处理RUN_PATH*/
        String runPath = new File("").getAbsolutePath();
        envMap.put("RUN_PATH", runPath);
        logger.info("runPath at {}", runPath);
    }
    /*
     * SettingListener 接口实现
     *   实现该接口的目的是，通过注册SettingListener动态更新环境变量相关信息。
     */
    public void reload(final Settings newConfig) {
        // .系统环境变量 & Java系统属性
        logger.debug("envVars.reload -> System.getenv().");
        Map<String, String> envMap = System.getenv();
        for (String key : envMap.keySet()) {
            this.envMap.put(key.toUpperCase(), envMap.get(key));
        }
        // .Java属性
        logger.debug("envVars.reload -> System.getProperties().");
        Properties prop = System.getProperties();
        for (Object propKey : prop.keySet()) {
            String k = propKey.toString();
            Object v = prop.get(propKey);
            if (v != null) {
                this.envMap.put(k.toUpperCase(), v.toString());
            }
        }
        // .Hasor 特有变量
        logger.debug("envVars.reload -> configEnvironment().");
        this.configEnvironment(this.envMap);
        //
        // .日志输出
        if (logger.isInfoEnabled()) {
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
            if (!this.userEnvMap.isEmpty()) {
                sb.append("\n").append(this.formatMap4log(keyMaxSize, this.userEnvMap));
                sb.append("\n").append(StringUtils.fixedString('-', 50));
            }
            logger.info(sb.toString());
        }
    }
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
    public String envVar(final String envName) {
        return this.evalEnvVar(envName, new HashMap<String, String>());
    }
    public String evalString(final String evalString) {
        return this.evalString(evalString, new HashMap<String, String>());
    }
    //
    private String evalString(String evalString, final Map<String, String> paramMap) {
        if (StringUtils.isBlank(evalString)) {
            return "";
        }
        Pattern keyPattern = Pattern.compile("(?:%([\\w\\._-]+)%){1,1}");//  (?:%([\w\._-]+)%)
        Matcher keyM = keyPattern.matcher(evalString);
        Map<String, String> data = new HashMap<String, String>();
        while (keyM.find()) {
            String varKey = keyM.group(1);
            String var = this.envVar(varKey);
            var = StringUtils.isBlank(var) ? "%" + varKey + "%" : var;
            data.put("%" + varKey + "%", var);
        }
        String newEvalString = evalString;
        for (String key : data.keySet()) {
            newEvalString = StringUtils.replace(newEvalString, key, data.get(key));
        }
        if (logger.isInfoEnabled()) {
            logger.info("evalString '{}' eval to '{}'.", evalString, newEvalString);
        }
        return newEvalString;
    }
    private String evalEnvVar(String varName, final Map<String, String> paramMap) {
        varName = varName.toUpperCase();
        if (paramMap.containsKey(varName)) {
            return paramMap.get(varName);
        }
        paramMap.put(varName, "");/*预处理值*/
        //
        String varValue = this.userEnvMap.get(varName);
        if (StringUtils.isBlank(varValue)) {
            varValue = this.envMap.get(varName);
        }
        if (StringUtils.isBlank(varValue)) {
            varValue = "";
        } else {
            varValue = this.evalString(varValue, paramMap);
        }
        paramMap.put(varName, varValue);/*覆盖预处理值*/
        return varValue;
    }
}