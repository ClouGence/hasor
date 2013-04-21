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
package org.platform.icache;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletContext;
import org.more.global.Global;
import org.more.util.Iterators;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.context.setting.Config;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-216
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class KeyBuilderDefinition implements Provider<IKeyBuilder> {
    private Class<?>                   type             = null;
    private Key<? extends IKeyBuilder> keyBuilderKey    = null;
    private Map<String, String>        initParams       = null;
    private IKeyBuilder                keyBuilderObject = null;
    //
    //
    public KeyBuilderDefinition(Class<?> type, Key<? extends IKeyBuilder> keyBuilderKey, Map<String, String> initParams) {
        this.type = type;
        this.keyBuilderKey = keyBuilderKey;
        this.initParams = initParams;
    }
    public Key<? extends IKeyBuilder> getKeyBuilderKey() {
        return this.keyBuilderKey;
    }
    public void initKeyBuilder(final AppContext appContext) {
        this.keyBuilderObject = appContext.getGuice().getInstance(this.keyBuilderKey);
        this.keyBuilderObject.initKeyBuilder(appContext, new Config() {
            public ServletContext getServletContext() {
                return appContext.getInitContext().getServletContext();
            }
            public String getInitParameter(String s) {
                return initParams.get(s);
            }
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
            @Override
            public Global getSettings() {
                return appContext.getSettings();
            }
            @Override
            public void addSettingsListener(SettingListener settingsListener) {
                appContext.getInitContext().getConfig().addSettingsListener(settingsListener);
            }
            @Override
            public void removeSettingsListener(SettingListener settingsListener) {
                appContext.getInitContext().getConfig().removeSettingsListener(settingsListener);
            }
        });
    }
    public boolean canSupport(Class<?> targetType) {
        if (targetType == null)
            return false;
        return this.type.isAssignableFrom(targetType);
    }
    @Override
    public IKeyBuilder get() {
        return this.keyBuilderObject;
    }
    public void destroy() {
        if (this.keyBuilderObject != null)
            this.keyBuilderObject.destroy();
    }
}