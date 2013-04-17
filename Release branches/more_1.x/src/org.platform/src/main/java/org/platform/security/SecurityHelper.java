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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 该类提供了获取与当前线程进行绑定的{@link AuthSession}，其子类通过调用initHelper和clearHelper两个静态方法以管理绑定对象。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SecurityHelper {
    private static final ThreadLocal<AuthSession> currentSession = new ThreadLocal<AuthSession>();
    //
    /**判断{@link SecurityHelper}类是否可以使用。*/
    public static boolean canUse() {
        AuthSession authSession = getAuthSession();
        if (authSession == null)
            return false;
        else
            return true;
    }
    /**获取当前线程下的{@link AuthSession}*/
    public static AuthSession getAuthSession() {
        return currentSession.get();
    }
    /**该方法由runtime保护起来不允许开发者直接调用，调用该方法会导致环境中{@link HttpServletRequest}、{@link HttpServletResponse}对象混乱。*/
    protected synchronized static void initHelper(AuthSession authSession) {
        clearHelper();
        currentSession.set(authSession);
    }
    /**清空WebHelper中与当前线程关联的{@link AuthSession}对象。*/
    protected synchronized static void clearHelper() {
        if (currentSession.get() != null)
            currentSession.remove();
    }
}