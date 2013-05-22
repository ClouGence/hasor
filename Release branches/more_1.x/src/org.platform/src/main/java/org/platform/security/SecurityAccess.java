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
import java.util.List;
import org.platform.context.AppContext;
/**
 * 负责授权操作，通过该接口可以将权限控制框架连接到不同的权限模型。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityAccess {
    /**初始化授权模块。*/
    public void initAccess(AppContext appContext);
    /**装载用户的权限。*/
    public List<Permission> loadPermission(UserInfo userInfo);
    /**销毁授权模块。*/
    public void destroyAccess(AppContext appContext);
}