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
import org.platform.security._.IUser;
/**
 * 当前权限会话
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AuthSession {
    /**执行登陆，返回值为{@link AuthorResult}类型枚举。*/
    public abstract void doLogin(IUser user);
    /**执行退出，返回值为{@link AuthorResult}类型枚举。*/
    public abstract void doLogout();
    //
    //
    /**是否已经登陆*/
    public abstract boolean isLogin();
    /**使用对策略判断权限。*/
    public abstract boolean checkPolicy(Power powerAnno);
}