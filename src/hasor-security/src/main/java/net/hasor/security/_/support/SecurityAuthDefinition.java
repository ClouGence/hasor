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
package net.hasor.security._.support;
import net.hasor.security._.SecurityAuth;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-28
 * @author 赵永春 (zyc@byshell.org)
 */
class SecurityAuthDefinition implements Provider<SecurityAuth> {
    private String                      authSystem = null;
    private Key<? extends SecurityAuth> authKey    = null;
    private SecurityAuth                authObject = null;
    //  
    public SecurityAuthDefinition(String authSystem, Key<? extends SecurityAuth> authKey) {
        this.authSystem = authSystem;
        this.authKey = authKey;
    }
    public void initAuth(AppContext appContext) {
        Hasor.info("init SecurityAuth %s bind %s.", authSystem, authKey);
        this.authObject = appContext.getGuice().getInstance(this.authKey);
        this.authObject.initAuth(appContext);
    }
    public void destroyAuth(AppContext appContext) {
        if (this.authObject != null)
            this.authObject.destroyAuth(appContext);
    }
    public String getAuthSystem() {
        return this.authSystem;
    }
    public Key<? extends SecurityAuth> getSecurityAuthKey() {
        return this.authKey;
    }
    @Override
    public SecurityAuth get() {
        return this.authObject;
    }
}