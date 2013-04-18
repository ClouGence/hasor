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
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
/**
 * ÷¥––»®œﬁºÏ≤‚
 * @version : 2013-4-17
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class SecuritySessionListener implements HttpSessionListener {
    private SecurityService secService = null;
    public void init(SecurityService secService) {
        this.secService = secService;
    }
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        this.secService.getAuthSession(se.getSession(), true);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        AuthSession authSession = this.secService.getAuthSession(se.getSession(), false);
        if (authSession != null)
            authSession.close();
    }
}