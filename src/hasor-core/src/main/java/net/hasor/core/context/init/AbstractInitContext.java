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
package net.hasor.core.context.init;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.Environment;
import net.hasor.core.EventManager;
import net.hasor.core.InitContext;
import net.hasor.core.Settings;
import net.hasor.core.SettingsListener;
import org.more.util.ResourceWatch;
import org.more.util.ScanClassPath;
import org.more.util.StringUtils;
/**
 * {@link InitContext}接口实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractInitContext implements InitContext {
    private long         startTime;   //系统启动时间
    private Object       context;
    private String[]     spanPackage;
    private Settings     settings;
    private Environment  environment;
    private EventManager eventManager;
    //---------------------------------------------------------------------------------Basic Method
    public long getAppStartTime() {
        return this.startTime;
    }
    public Object getContext() {
        return this.context;
    }
    /**设置上下文*/
    public void setContext(Object context) {
        this.context = context;
    }
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        return ScanClassPath.getClassSet(this.spanPackage, featureType);
    }
    //
    public Settings getSettings() {
        return this.settings;
    }
    public Environment getEnvironment() {
        return this.environment;
    }
    public EventManager getEventManager() {
        return this.eventManager;
    }
    //
    /**初始化方法*/
    protected void initContext() throws IOException {
        this.startTime = System.currentTimeMillis();
        this.settingListenerList = new ArrayList<SettingsListener>();
        this.settings = this.createSettings(this.getSettingURI());
        this.settings.refresh();
        //
        this.environment = this.createEnvironment();
        this.eventManager = this.createEventManager();
        //
        String[] spanPackages = this.getSettings().getStringArray("hasor.loadPackages");
        ArrayList<String> allPack = new ArrayList<String>();
        for (String packs : spanPackages) {
            if (StringUtils.isBlank(packs) == true)
                continue;
            String[] packArray = packs.split(",");
            for (String pack : packArray) {
                if (StringUtils.isBlank(packs) == true)
                    continue;
                allPack.add(pack.trim());
            }
        }
        //排序的目的是避免类似“org”，“com”，“net”这样的包声明排在首位。
        Collections.sort(allPack, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return -o1.compareToIgnoreCase(o2);
            }
        });
        this.spanPackage = allPack.toArray(new String[allPack.size()]);
        Hasor.info("loadPackages : " + Hasor.logString(this.spanPackage));
        //
        //
        URI uri = this.getSettingURI();
        if (uri != null) {
            this.mainConfigWatch = new InitContextResourceWatch(this);
            long interval = this.getSettings().getLong("hasor.settingsMonitor.interval", 15000L);
            this.mainConfigWatch.setCheckSeepTime(interval);
            this.mainConfigWatch.start();
        }
    }
    /**创建{@link Settings}接口对象*/
    protected abstract Settings createSettings(URI settingURI) throws IOException;
    /**创建{@link Environment}接口对象*/
    protected abstract Environment createEnvironment();
    /**创建{@link EventManager}接口对象*/
    protected abstract EventManager createEventManager();
    //-------------------------------------------------------------------------HasorSettingListener
    private InitContextResourceWatch mainConfigWatch     = null;
    private List<SettingsListener>   settingListenerList = null;
    /**触发配置文件重载事件。*/
    private void onSettingChangeEvent() {
        for (SettingsListener listener : this.settingListenerList)
            listener.onLoadConfig(this.getSettings());
    }
    /**添加配置文件变更监听器。*/
    public void addSettingsListener(SettingsListener settingsListener) {
        if (this.settingListenerList.contains(settingsListener) == false)
            this.settingListenerList.add(settingsListener);
    }
    /**删除配置文件监听器。*/
    public void removeSettingsListener(SettingsListener settingsListener) {
        if (this.settingListenerList.contains(settingsListener) == true)
            this.settingListenerList.remove(settingsListener);
    }
    public SettingsListener[] getSettingListeners() {
        return this.settingListenerList.toArray(new SettingsListener[this.settingListenerList.size()]);
    }
    //--------------------------------------------------------------------------------Config Loader
    //
    public void destroy() throws Throwable {
        if (this.mainConfigWatch != null)
            this.mainConfigWatch.stop();
        super.finalize();
    }
    //
    private static class InitContextResourceWatch extends ResourceWatch {
        private AbstractInitContext initContext = null;
        //
        public InitContextResourceWatch(AbstractInitContext initContext) {
            this.initContext = initContext;
        }
        public void firstStart(URI resourceURI) throws IOException {}
        /**当配置文件被检测到有修改迹象时，调用刷新进行重载。*/
        public final void onChange(URI resourceURI) throws IOException {
            this.initContext.getSettings().refresh();
            this.initContext.onSettingChangeEvent();
            //
            //尝试更新interval
            long interval = this.initContext.getSettings().getLong("hasor.settingsMonitor.interval", 15000L);
            this.setCheckSeepTime(interval);
        }
        /**检测主配置文件是否被修改*/
        public long lastModify(URI resourceURI) throws IOException {
            if ("file".equals(resourceURI.getScheme()) == true)
                return new File(resourceURI).lastModified();
            return 0;
        }
        public synchronized void start() {
            this.setName("MasterConfiguration-Watch");
            Hasor.warning("settings Watch started thread name is %s.", this.getName());
            this.setDaemon(true);
            URI mainConfig = this.initContext.getSettingURI();
            //2.启动监听器
            try {
                if (mainConfig == null) {
                    Hasor.warning("do not loading master settings file.");
                    return;
                }
                this.setResourceURI(this.initContext.getSettingURI());
            } catch (Exception e) {
                Hasor.error("settings Watch start error, on : %s Settings file !%s", mainConfig, e);
            }
            //
            super.start();
        }
    }
}