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
import org.platform.security.AuthSession;
import org.platform.security.SecurityContext;
import org.platform.security.SessionData;
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登入之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultAuthSession extends AuthSession {
    public DefaultAuthSession(SessionData authSessionData, SecurityContext securityContext) {
        super(authSessionData, securityContext);
    }
}a