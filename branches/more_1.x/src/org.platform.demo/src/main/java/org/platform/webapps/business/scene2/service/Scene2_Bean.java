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
package org.platform.webapps.business.scene2.service;
import org.platform.general.Bean;
import org.platform.security.AuthSession;
import org.platform.security.RoleIdentity;
import org.platform.security.RoleIdentityUtil;
import org.platform.security.SecurityContext;
import org.platform.webapps.safety.BBSUser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 
 * @version : 2013-5-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
@Bean("Scene2_ServiceBean")
public class Scene2_Bean {
    @Inject
    private SecurityContext securityContext = null;
    public void print() {
        RoleIdentity adminIdentity = RoleIdentityUtil.getTypeIdentity(BBSUser.class);
        AuthSession[] auth = securityContext.findCurrentAuthSession(adminIdentity);
        System.out.println("this is message from Scene2_Bean. Current AuthSession RoleIdentity is BBSUser count=" + auth.length);
    }
}