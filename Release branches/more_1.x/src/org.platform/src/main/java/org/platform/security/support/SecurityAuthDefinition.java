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
package org.platform.security.support;
import org.platform.context.AppContext;
import org.platform.security.ISecurityAuth;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-28
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class SecurityAuthDefinition implements Provider<ISecurityAuth> {
    private String                       authSystem = null;
    private Key<? extends ISecurityAuth> authKey    = null;
    private ISecurityAuth                authObject = null;
    //  
    public SecurityAuthDefinition(String authSystem, Key<? extends ISecurityAuth> authKey) {
        this.authSystem = authSystem;
        this.authKey = authKey;
    }
    public void initAuth(AppContext appContext) {
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
    public Key<? extends ISecurityAuth> getSecurityAuthKey() {
        return this.authKey;
    }
    @Override
    public ISecurityAuth get() {
        return this.authObject;
    }
}