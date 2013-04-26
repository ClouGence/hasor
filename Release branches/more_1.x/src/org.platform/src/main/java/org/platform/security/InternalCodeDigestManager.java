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
package org.platform.security;
import java.util.HashMap;
import java.util.Map;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.context.setting.Settings;
/**
 * 
 * @version : 2013-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalCodeDigestManager implements SettingListener {
    private SecuritySettings        securitySettings    = null;
    private Map<String, CodeDigest> codeDigestObjectMap = new HashMap<String, CodeDigest>();
    public void initManager(AppContext appContext) {
        this.securitySettings = appContext.getBean(SecuritySettings.class);
        appContext.getInitContext().getConfig().addSettingsListener(this);
    }
    public CodeDigest getCodeDigest(String name) {
        Map<String, Class<CodeDigest>> codeDigestMap = this.securitySettings.getDigestMap();
        if (codeDigestMap.containsKey(name) == false)
            return null;
        //
        if (this.codeDigestObjectMap.containsKey(name) == true)
            return this.codeDigestObjectMap.get(name);
        try {
            Class<CodeDigest> digestType = codeDigestMap.get(name);
            CodeDigest digestObject = digestType.newInstance();
            this.codeDigestObjectMap.put(name, digestObject);
            return digestObject;
        } catch (Exception e) {
            Platform.error("create CodeDigest an error!\t" + Platform.logString(e));
            return null;
        }
    }
    public void destroyManager(AppContext appContext) {
        appContext.getInitContext().getConfig().removeSettingsListener(this);
    }
    @Override
    public void loadConfig(Settings newConfig) {
        this.codeDigestObjectMap.clear();
    };
}