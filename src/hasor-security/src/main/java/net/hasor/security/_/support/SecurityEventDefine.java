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
/**
 * 
 * @version : 2013-5-8
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityEventDefine {
    /**当请求检测URL权限时，在事件中可以操控AuthSession临时赋予路径访问权限。*/
    public static final String TestURLPermission        = "Security_TestURLPermission";
    /**登入事件*/
    public static final String Login                    = "Security_Login";
    /**登出事件*/
    public static final String Logout                   = "Security_Logout";
    /**AuthSession被关闭*/
    public static final String AuthSession_Close        = "Security_AuthSession_Close";
    /**AuthSession被创建*/
    public static final String AuthSession_New          = "Security_AuthSession_New";
    /**当前线程激活的AuthSession。*/
    public static final String AuthSession_Activate     = "Security_AuthSession_Activate";
    /**当前线程钝化的AuthSession。*/
    public static final String AuthSession_Inactivation = "Security_AuthSession_Inactivation";
}