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
package org.moreframework.security.support;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.moreframework.MoreFramework;
import org.moreframework.context.AppContext;
import org.moreframework.security.SecurityAuth;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * SecurityAuthπ‹¿Ì∆˜°£
 * @version : 2013-4-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ManagedSecurityAuthManager {
    private Map<String, SecurityAuthDefinition> authDefinitionMap = null;
    //
    public void initManager(AppContext appContext) {
        this.authDefinitionMap = collectSecurityAuthDefinitionMap(appContext.getGuice());
        for (Entry<String, SecurityAuthDefinition> definitionEnt : this.authDefinitionMap.entrySet()) {
            definitionEnt.getValue().initAuth(appContext);
        }
        MoreFramework.info("managedSecurityAuthManager initialized.");
    }
    private Map<String, SecurityAuthDefinition> collectSecurityAuthDefinitionMap(Injector injector) {
        Map<String, SecurityAuthDefinition> authDefinitionMap = new HashMap<String, SecurityAuthDefinition>();
        TypeLiteral<SecurityAuthDefinition> AUTH_DEFS = TypeLiteral.get(SecurityAuthDefinition.class);
        for (Binding<SecurityAuthDefinition> entry : injector.findBindingsByType(AUTH_DEFS)) {
            SecurityAuthDefinition define = entry.getProvider().get();
            authDefinitionMap.put(define.getAuthSystem(), define);
        }
        return authDefinitionMap;
    }
    public void destroyManager(AppContext appContext) {
        MoreFramework.info("destroy ManagedSecurityAuthManager...");
        for (Entry<String, SecurityAuthDefinition> definitionEnt : authDefinitionMap.entrySet()) {
            definitionEnt.getValue().destroyAuth(appContext);
        }
    }
    public SecurityAuth getSecurityAuth(String authSystem, AppContext appContext) {
        SecurityAuthDefinition define = this.authDefinitionMap.get(authSystem);
        if (define == null)
            return null;
        return define.get();
    }
}