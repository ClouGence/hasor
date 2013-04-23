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
package org.platform;
/**
 * 配置信息Key
 * @version : 2013-4-18
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PlatformConfigEnum {
    /**装载的class包范畴，逗号间隔.*/
    public static final String Platform_LoadPackages                = "framework.loadPackages";
    //
    //
    /**异常处理程序总迭代次数(配置Code).*/
    public static final String HttpServlet_ErrorCaseCount           = "httpServlet.errorCaseCount";
    //
    //
    /**程序工作空间基础目录（绝对地址）*/
    public static final String Workspace_WorkDir                    = "workspace.workDir";
    /** 程序的文件数据目录（默认相对workDir地址，可以通过设置absolute属性为true表示一个绝对地址）*/
    public static final String Workspace_DataDir                    = "workspace.dataDir";
    public static final String Workspace_DataDir_Absolute           = "workspace.dataDir.absolute";
    /** 程序运行期间所需的临时数据存放地址（默认相对baseDir地址，可以通过设置absolute属性为true表示一个绝对地址）*/
    public static final String Workspace_TempDir                    = "workspace.tempDir";
    public static final String Workspace_TempDir_Absolute           = "workspace.tempDir.absolute";
    /** 程序运行时生成的缓存数据存放位置（默认相对baseDir地址，可以通过设置absolute属性为true表示一个绝对地址）*/
    public static final String Workspace_CacheDir                   = "workspace.cacheDir";
    public static final String Workspace_CacheDir_Absolute          = "workspace.cacheDir.absolute";
    //
    //
    /**是否启用权限系统*/
    public static final String Security_Enable                      = "security.enable";
    /**当Security_Enable启用之后，该值决定是否启用针对URL部分的权限过滤。*/
    public static final String Security_EnableURL                   = "security.enableURL";
    /**当Security_Enable启用之后，该值决定是否启用针对方法调用中的权限过滤。*/
    public static final String Security_EnableMethod                = "security.enableMethod";
    /**登入地址*/
    public static final String Security_LoginURL                    = "security.loginURL";
    /**登出地址*/
    public static final String Security_LogoutURL                   = "security.logoutURL";
    /**登入表单，用户名*/
    public static final String Security_LoginFormData_AccountField  = "security.loginFormData.accountField";
    /**登入表单，密码*/
    public static final String Security_LoginFormData_PasswordField = "security.loginFormData.passwordField";
    /**request请求包含权限检查的URL*/
    public static final String Security_Rules_Includes              = "security.rules.includes";
    /**request请求排除权限检查的URL*/
    public static final String Security_Rules_Excludes              = "security.rules.excludes";
    /**逃出规则匹配的请求路径默认配置：NeedCheck、NoCheck*/
    public static final String Security_Rules_DefaultRuleModel      = "security.rules.defaultRuleModel";
    //
    //
    /**是否启用缓存系统.*/
    public static final String CacheConfig_Enable                   = "cacheConfig.enable";
    /**MapCache，设置的超时时间.*/
    public static final String CacheConfig_WeakMapCache_Timeout     = "cacheConfig.weakMapCache.timeout";
    /**MapCache，缓存是否永远不销毁.*/
    public static final String CacheConfig_WeakMapCache_Eternal     = "cacheConfig.weakMapCache.eternal";
    /**MapCache，每当访问缓存对象时是否自动对其续约（续约时间同加入时缓存超时时间）.*/
    public static final String CacheConfig_WeakMapCache_AutoRenewal = "cacheConfig.weakMapCache.autoRenewal";
    /**MapCache，缓存回收线程工作的时间频率(毫秒).*/
    public static final String CacheConfig_WeakMapCache_ThreadSeep  = "cacheConfig.weakMapCache.threadSeep";
}