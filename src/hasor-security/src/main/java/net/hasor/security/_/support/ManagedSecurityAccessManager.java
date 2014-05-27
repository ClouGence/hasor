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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.hasor.security._.SecurityAccess;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * SecurityAccess管理器。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
class ManagedSecurityAccessManager {
    private Map<String, SecurityAccessDefinition> accessDefinitionMap = null;
    //
    public void initManager(AppContext appContext) {
        this.accessDefinitionMap = collectSecurityAccessDefinitionMap(appContext.getGuice());
        for (Entry<String, SecurityAccessDefinition> definitionEnt : this.accessDefinitionMap.entrySet()) {
            definitionEnt.getValue().initAccess(appContext);
        }
        Hasor.info("managedSecurityAccessManager initialized.");
    }
    private Map<String, SecurityAccessDefinition> collectSecurityAccessDefinitionMap(Injector injector) {
        Map<String, SecurityAccessDefinition> accessDefinitionMap = new HashMap<String, SecurityAccessDefinition>();
        TypeLiteral<SecurityAccessDefinition> ACCESS_DEFS = TypeLiteral.get(SecurityAccessDefinition.class);
        for (Binding<SecurityAccessDefinition> entry : injector.findBindingsByType(ACCESS_DEFS)) {
            SecurityAccessDefinition define = entry.getProvider().get();
            accessDefinitionMap.put(define.getAuthSystem(), define);
        }
        return accessDefinitionMap;
    }
    public void destroyManager(AppContext appContext) {
        Hasor.info("destroy ManagedSecurityAccessManager...");
        for (Entry<String, SecurityAccessDefinition> definitionEnt : accessDefinitionMap.entrySet()) {
            definitionEnt.getValue().destroyAccess(appContext);
        }
    }
    public SecurityAccess getSecurityAccess(String authSystem, AppContext appContext) {
        SecurityAccessDefinition define = this.accessDefinitionMap.get(authSystem);
        if (define == null)
            return null;
        return define.get();
    }
}