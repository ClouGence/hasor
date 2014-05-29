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
package net.hasor.security;
/**
 * 用户权限测试接口，该接口如果没有加入任何测试条件则测试结果为true。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityQuery extends SecurityNode {
    /**逻辑与*/
    public SecurityQuery and(String permissionCode);
    public SecurityQuery and(Permission permission);
    public SecurityQuery and(SecurityQuery testNode);
    /**逻辑或*/
    public SecurityQuery or(String permissionCode);
    public SecurityQuery or(Permission permission);
    public SecurityQuery or(SecurityQuery testNode);
    /*-*/
    /**整个查询结果取非*/
    public SecurityQuery not();
    /**结果中必须不包含.*/
    public SecurityQuery not(String permissionCode);
    /**结果中必须不包含.*/
    public SecurityQuery not(Permission permission);
    /**结果中必须不包含.*/
    public SecurityQuery not(SecurityQuery testNode);
    /*-*/
    /**需要登入系统*/
    public SecurityQuery andLogin();
    public SecurityQuery orLogin();
    /**需要登出系统*/
    public SecurityQuery andLogout();
    public SecurityQuery orLogout();
    /**需要来宾身份*/
    public SecurityQuery andGuest();
    /**或者为来宾身份*/
    public SecurityQuery orGuest();
    /**非来宾身份*/
    public SecurityQuery notGuest();
    /**自定义检测（与关系）*/
    public SecurityQuery andCustomer(SecurityNode customerTest);
    /**自定义检测（或关系）*/
    public SecurityQuery orCustomer(SecurityNode customerTest);
    /**自定义检测（非关系）*/
    public SecurityQuery notCustomer(SecurityNode customerTest);
};