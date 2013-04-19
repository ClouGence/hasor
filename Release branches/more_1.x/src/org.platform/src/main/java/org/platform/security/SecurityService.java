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
import javax.servlet.http.HttpSession;
/**
 * 该类提供了获取与当前线程进行绑定的{@link AuthSession}，其子类通过调用initHelper和clearHelper两个静态方法以管理绑定对象。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityService {
    /**获取或创建一个{@link AuthSession}接口。*/
    public abstract AuthSession getAuthSession(HttpServletRequest request, HttpServletResponse response, boolean created);
    /**获取或创建一个{@link AuthSession}接口。*/
    public abstract AuthSession getAuthSession(String authSessionID, boolean created);
    /**获取或创建一个{@link AuthSession}接口。*/
    public abstract AuthSession getAuthSession(HttpSession session, boolean created);
    /**获取当前的权限会话。*/
    public abstract AuthSession getCurrentAuthSession();
    /**根据uri获取用于判断权限的功能接口。*/
    public abstract UriPatternMatcher getUriMatcher(String requestPath);
    /**根据uri获取可用于跳转工具类。*/
    public abstract SecurityDispatcher getDispatcher(String requestPath);
    /**创建{@link PowerTest} 类，该类可以用来测试用户的权限。*/
    public PowerTest newTest();
    /**用户权限测试接口。*/
    public interface PowerTest {
        /*-*/
        public PowerTest and(Power powerAnno);
        public PowerTest or(Power permission);
        public PowerTest not(Power powerCode);
        /*-*/
        public PowerTest and(Permission powerAnno);
        public PowerTest or(Permission permission);
        public PowerTest not(Permission powerCode);
        /*-*/
        public PowerTest and(String powerAnno);
        public PowerTest or(String permission);
        public PowerTest not(String powerCode);
        /*-----------------------------------------*/
        /**测试权限*/
        public boolean test(AuthSession authSession);
    }
}