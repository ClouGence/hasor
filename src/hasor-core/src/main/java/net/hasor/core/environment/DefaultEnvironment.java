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
import java.net.URI;
import net.hasor.Hasor;
import net.hasor.core.Environment;
import net.hasor.core.EventManager;
import net.hasor.core.Settings;
import net.hasor.core.SettingsListener;
import net.hasor.core.event.StandardEventManager;
import net.hasor.core.setting.FileSettings;
/**
 * {@link Environment}接口实现类。
 * @version : 2013-9-11
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultEnvironment extends AbstractEnvironment {
    public DefaultEnvironment() {
        this(null, null);
    }
    public DefaultEnvironment(URI mainSettings) {
        this(mainSettings, null);
    }
    public DefaultEnvironment(URI mainSettings, Object context) {
        if (context != null)
            this.setContext(context);
        if (mainSettings != null)
            this.settingURI = mainSettings;
        this.initEnvironment();
    }
    //---------------------------------------------------------------------------------Basic Method
    protected URI settingURI = null;
    public URI getSettingURI() {
        return this.settingURI;
    }
    protected SettingWatch createSettingWatch(URI settingURI) {
        final SettingWatch settingWatch = new SettingWatch(this) {};
        /*设置监听器检测间隔*/
        long interval = this.getSettings().getLong("hasor.settingsMonitor.interval", 15000L);
        settingWatch.setCheckSeepTime(interval);
        /*注册一个配置文件监听器，当配置文件更新时通知监听器更新检测间隔*/
        this.addSettingsListener(new SettingsListener() {
            public void onLoadConfig(Settings newConfig) {
                long interval = newConfig.getLong("hasor.settingsMonitor.interval", 15000L);
                if (interval != settingWatch.getCheckSeepTime()) {
                    Hasor.info("SettingWatch to monitor configuration updates, set interval new Value is %s", interval);
                    settingWatch.setCheckSeepTime(interval);
                }
            }
        });
        return settingWatch;
    }
    protected EnvVars createEnvVars() {
        return new EnvVars(this) {};
    }
    protected EventManager createEventManager() {
        return new StandardEventManager(this);
    }
    protected Settings createSettings(URI settingURI) throws IOException {
        return new FileSettings(new File(settingURI));
    }
}