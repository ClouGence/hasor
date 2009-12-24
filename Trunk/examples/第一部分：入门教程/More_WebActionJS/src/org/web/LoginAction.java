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
package org.web;
import org.more.submit.ActionStack;
/**
 * 作为Action类必须是共有的非抽象的，同时也不能是接口。
 * Date : 2009-12-11
 */
public class LoginAction {
    /**当帐号和密码一致时登陆成功*/
    public String login(ActionStack stack) {
        String account = stack.getParamString("account");//获取帐号
        String password = stack.getParamString("password");//获取密码
        if (account.equals(password) == true)
            return "登陆成功！";
        else
            return "登陆失败，帐号和密码不一致！";
    }
}