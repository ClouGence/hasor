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
 * 负责登录认证操作，通过该接口可以验证用户身份正确性。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Authentication {
    /**通过userCode登陆系统*/
    public Token getUserInfo(String userCode);
}