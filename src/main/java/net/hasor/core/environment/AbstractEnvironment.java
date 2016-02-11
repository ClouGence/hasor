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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.core.Settings;
import net.hasor.core.event.StandardEventManager;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * {@link Environment}接口实现类，集成该类的子类需要调用{@link #initEnvironment(Settings)}方法以初始化。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractEnvironment implements Environment {
    protected Logger     logger       = LoggerFactory.getLogger(getClass());
    private String[]     spanPackage  = null;
    private Settings     settings     = null;
    private Object       context      = null;
    private EventContext eventManager = null;
    //
    //---------------------------------------------------------------------------------Basic Method
    public AbstractEnvironment(Object context) {
        this.context = context;
    }
    //---------------------------------------------------------------------------------Basic Method
    @Override
    public Object getContext() {
        return this.context;
    }
    public void setContext(final Object context) {
        this.context = context;
    }
    @Override
    public boolean isDebug() {
        return this.getSettings().getBoolean("hasor.debug", false);
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
    public Set<Class<?>> findClass(final Class<?> featureType) {
        return this.getSettings().findClass(featureType, this.spanPackage);
    }
    @Override
    public Settings getSettings() {
        return this.settings;
    }
    @Override
    public EventContext getEventContext() {
        if (this.eventManager == null) {
            int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
            this.eventManager = new StandardEventManager(eventThreadPoolSize);
        }
        return this.eventManager;
    }
    //
    /*----------------------------------------------------------------------------------------Dir*/
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
    /*----------------------------------------------------------------------------------------Env*/
    /**初始化方法*/
    protected final void initEnvironment(Settings settings) {
        try {
            logger.debug("init Environment...");
            this.settings = settings;
            this.getSettings().refresh();
            logger.info("init Environment , use Settings = {}", settings);
        } catch (IOException e) {
            logger.error("init Environment , has IOException -> " + e.getMessage(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
        logger.debug("create envVars...");
        this.envVars = this.createEnvVars();
        logger.debug("reload envVars...");
        this.envVars.reload(getSettings());
        //
        String[] spanPackages = this.getSettings().getStringArray("hasor.loadPackages", "net.hasor.core.*,net.hasor.plugins.*");
        Set<String> allPack = new HashSet<String>();
        for (String packs : spanPackages) {
            if (StringUtils.isBlank(packs) == true) {
                continue;
            }
            String[] packArray = packs.split(",");
            for (String pack : packArray) {
                if (StringUtils.isBlank(packs) == true) {
                    continue;
                }
                allPack.add(pack.trim());
            }
        }
        ArrayList<String> spanPackagesArrays = new ArrayList<String>(allPack);
        Collections.sort(spanPackagesArrays);
        this.spanPackage = spanPackagesArrays.toArray(new String[spanPackagesArrays.size()]);
        logger.info("loadPackages = " + ReflectionToStringBuilder.toString(this.spanPackage, ToStringStyle.SIMPLE_STYLE));
    }
    //
    private static volatile long lastLong = 0;
    private static long nextLong() {
        long lastLongTemp = System.currentTimeMillis();
        while (true) {
            if (lastLongTemp != lastLong) {
                lastLong = lastLongTemp;
                break;
            }
        }
        return lastLong;
    }
    /**在缓存目录内创建一个不重名的临时文件名。 */
    public synchronized File uniqueTempFile() throws IOException {
        long markTime = nextLong();
        String atPath = this.genPath(markTime, 512);
        String fileName = atPath.substring(0, atPath.length() - 1) + "_" + String.valueOf(markTime) + ".tmp";
        File tmpFile = new File(this.envVar(Environment.HASOR_TEMP_PATH), fileName);
        tmpFile.getParentFile().mkdirs();
        tmpFile.createNewFile();
        if (logger.isInfoEnabled()) {
            logger.info("create Temp File at :" + tmpFile);
        }
        return tmpFile;
    }
    /**
    * 生成路径算法生成一个Path
    * @param number 参考数字
    * @param size 每个目录下可以拥有的子目录或文件数目。
    */
    public String genPath(long number, final int size) {
        StringBuffer buffer = new StringBuffer();
        long b = size;
        long c = number;
        do {
            long m = number % b;
            buffer.append(m + File.separator);
            c = number / b;
            number = c;
        } while (c > 0);
        return buffer.reverse().toString();
    }
    //
    /*-----------------------------------------------------------------------------------Env Vars*/
    private EnvVars envVars;
    @Override
    public String evalString(String eval) {
        return this.envVars.evalString(eval);
    }
    @Override
    public String envVar(String varName) {
        return this.envVars.envVar(varName);
    }
    @Override
    public void addEnvVar(final String varName, final String value) {
        this.envVars.addEnvVar(varName, value);
    }
    @Override
    public void remoteEnvVar(final String varName) {
        this.envVars.remoteEnvVar(varName);
    }
    @Override
    public void refreshVariables() {
        this.envVars.reload(this.getSettings());
    }
    /**创建{@link EnvVars}接口对象*/
    protected EnvVars createEnvVars() {
        return new EnvVars(this);
    }
}