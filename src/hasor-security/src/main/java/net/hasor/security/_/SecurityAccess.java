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
package net.hasor.security._;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.security.Permission;
import net.hasor.security.Token;
/**
 * 负责授权操作，通过该接口可以将权限控制框架连接到不同的权限模型。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityAccess {
    /**通过userCode登陆系统*/
    public Token getUserInfo(String userCode);
    /**通过帐号密码登陆系统*/
    public Token getUserInfo(String account, String password);
    /**装载用户的权限。*/
    public List<Permission> loadPermission(Token userInfo);
}