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
import org.platform.context.AppContext;
import org.platform.security.DefaultSecurityQuery.CheckPermission;
import com.google.inject.Inject;
/**
 * 
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalSecurityQueryBuilder {
    private AppContext appContext = null;
    @Inject
    public InternalSecurityQueryBuilder(AppContext appContext) {
        this.appContext = appContext;
    }
    /**将Permission注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(Permission permission) {
        return new CheckPermission(permission);
    }
    /**将String注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(String permissionCode) {
        return new CheckPermission(new Permission(permissionCode));
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery() {
        return this.appContext.getGuice().getInstance(SecurityQuery.class);
    };
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(Permission permission) {
        return this.newSecurityQuery().and(permission);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(String permissionCode) {
        return this.newSecurityQuery().and(permissionCode);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(SecurityNode testNode) {
        return this.newSecurityQuery().andCustomer(testNode);
    }
}