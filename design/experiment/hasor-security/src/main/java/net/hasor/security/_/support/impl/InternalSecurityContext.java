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
package net.hasor.security._.support.impl;
import static net.hasor.security._.SecurityConfig.Security_AuthSessionCache;
import static net.hasor.security._.SecurityConfig.Security_AuthSessionTimeout;
import net.hasor.security._.support.AbstractSecurityContext;
import net.hasor.security._.support.SessionData;
import org.hasor.context.AppContext;
import org.hasor.context.Settings;
import org.hasor.icache.Cache;
import org.hasor.icache.CacheManager;
import com.google.inject.Singleton;
/**
 * 内置SecurityContext类实现
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class InternalSecurityContext extends AbstractSecurityContext {
    private Cache<SessionData> authSessionCache = null;
    private long               sessionTimeOut   = 0;
    //
    @Override
    public synchronized void initSecurity(AppContext appContext) {
        super.initSecurity(appContext);
        Settings config = appContext.getSettings();
        //
        String cacheName = config.getString(Security_AuthSessionCache);
        CacheManager cacheManager = getAppContext().getInstance(CacheManager.class);
        authSessionCache = cacheManager.getCache(cacheName);
        if (authSessionCache == null)
            throw new NullPointerException("not load AuthSessionCache ‘" + cacheName + "’");
        sessionTimeOut = config.getLong(Security_AuthSessionTimeout);
    }
    @Override
    public synchronized void destroySecurity(AppContext appContext) {
        super.destroySecurity(appContext);
    }
    @Override
    protected void removeSessionData(String sessionDataID) {
        this.authSessionCache.remove(sessionDataID);
    }
    @Override
    protected void updateSessionData(String sessionID) {
        this.authSessionCache.refreshCache(sessionID);
    }
    @Override
    protected void updateSessionData(String sessionDataID, SessionData newSessionData) {
        this.authSessionCache.toCache(sessionDataID, newSessionData, this.sessionTimeOut);
    }
    @Override
    protected SessionData getSessionData(String sessionDataID) {
        return this.authSessionCache.fromCache(sessionDataID);
    }
    @Override
    protected void throwEvent(String eventType, Object... objects) {
        this.getAppContext().getEventManager().doSyncEvent(eventType, objects);
    }
}