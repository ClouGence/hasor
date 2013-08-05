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
package org.hasor.security.support;
import java.util.HashMap;
import java.util.Map;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.security.Digest;
/**
 * 
 * @version : 2013-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalCodeDigestManager {
    private SecuritySettings    securitySettings    = null;
    private Map<String, Digest> codeDigestObjectMap = new HashMap<String, Digest>();
    public Digest getCodeDigest(String name) {
        Map<String, Class<Digest>> codeDigestMap = this.securitySettings.getDigestMap();
        if (codeDigestMap.containsKey(name) == false)
            return null;
        //  
        if (this.codeDigestObjectMap.containsKey(name) == true)
            return this.codeDigestObjectMap.get(name);
        try {
            Class<Digest> digestType = codeDigestMap.get(name);
            Digest digestObject = digestType.newInstance();
            this.codeDigestObjectMap.put(name, digestObject);
            return digestObject;
        } catch (Exception e) {
            Hasor.error("create CodeDigest an error!\t%s", e);
            return null;
        }
    }
    public void initManager(AppContext appContext) {
        this.securitySettings = appContext.getInstance(SecuritySettings.class);
    }
    public void destroyManager(AppContext appContext) {}
}