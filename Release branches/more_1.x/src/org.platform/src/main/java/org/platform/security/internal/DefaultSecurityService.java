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
package org.platform.security.internal;
import java.util.List;
import org.platform.security.AuthSession;
import org.platform.security.SecurityContext;
import org.platform.security.SessionData;
/**
 *  
 * @version : 2013-4-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultSecurityService extends SecurityContext {
    @Override
    protected AuthSession newAuthSession(SessionData sessionData) {
        return new DefaultAuthSession(sessionData, this);
    }
    //  //
    //  //
    //  Key cacheKey = Key.get(TypeLiteral.get(ICache.class), Names.named(appContext.getSettings().getString(Security_AuthSessionCache)));
    //  this.authSessionCache = appContext.getGuice().getInstance(cacheKey);
    @Override
    protected SessionData createSessionData() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected void removeSessionData(SessionData authSessionID) {
        // TODO Auto-generated method stub
    }
    @Override
    protected void updateSessionData(SessionData sessionData) {
        // TODO Auto-generated method stub
    }
    @Override
    protected SessionData getSessionDataByID(String authSessionID) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected List<SessionData> getSessionDataList() {
        // TODO Auto-generated method stub
        return null;
    }
}s