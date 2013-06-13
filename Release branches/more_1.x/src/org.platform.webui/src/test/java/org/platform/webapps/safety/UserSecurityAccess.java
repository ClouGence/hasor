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
package org.platform.webapps.safety;
import java.util.List;
import org.platform.context.AppContext;
import org.platform.security.SecurityAccess;
import org.platform.security.Permission;
import org.platform.security.SecAccess;
import org.platform.security.UserInfo;
/**
 * 
 * @version : 2013-4-28
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SecAccess(authSystem = "UserAuth")
public class UserSecurityAccess implements SecurityAccess {
    //
    @Override
    public void initAccess(AppContext appContext) {}
    @Override
    public List<Permission> loadPermission(UserInfo userInfo) {
        return null;
    }
    @Override
    public void destroyAccess(AppContext appContext) {}
}