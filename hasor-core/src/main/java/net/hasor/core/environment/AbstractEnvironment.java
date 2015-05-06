/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.util.HashSet;
import java.util.Set;
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.core.Settings;
import net.hasor.core.event.StandardEventManager;
import org.more.UnhandledException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * {@link Environment}接口实现类，集成该类的子类需要调用{@link #initEnvironment()}方法以初始化。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractEnvironment implements Environment {
    private String[]     spanPackage  = null;
    private Settings     settings     = null;
    private Object       context      = null;
    private EventContext eventManager = null;
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
        return this.settings.getBoolean("hasor.debug", false);
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
    /*----------------------------------------------------------------------------------------Env*/
    /**初始化方法*/
    protected final void initEnvironment() {
        LoggerHelper.logInfo("init Environment.");
        //
        try {
            this.settings = this.createSettings();
            this.settings.refresh();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new UnhandledException(e);
        }
        this.envVars = this.createEnvVars();
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
        this.spanPackage = allPack.toArray(new String[allPack.size()]);
        LoggerHelper.logInfo("loadPackages : %s", ReflectionToStringBuilder.toString(this.spanPackage, ToStringStyle.SIMPLE_STYLE));
        //
        if (this.getSettingURI() == null) {
            LoggerHelper.logWarn("no need to monitor configuration file.");
            return;
        }
    }
    /**创建{@link Settings}接口对象*/
    protected abstract Settings createSettings() throws IOException;
    /**在缓存目录内创建一个不重名的临时文件名。 */
    public synchronized File uniqueTempFile() throws IOException {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {}
        long markTime = System.currentTimeMillis();
        String atPath = this.genPath(markTime, 512);
        String fileName = atPath.substring(0, atPath.length() - 1) + "_" + String.valueOf(markTime) + ".tmp";
        File tmpFile = new File(this.envVar(Environment.HASOR_TEMP_PATH), fileName);
        tmpFile.getParentFile().mkdirs();
        tmpFile.createNewFile();
        LoggerHelper.logInfo("create Temp File at %s.", tmpFile);
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