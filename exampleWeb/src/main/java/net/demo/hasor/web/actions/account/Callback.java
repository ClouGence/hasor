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
package net.demo.hasor.web.actions.account;
import net.demo.hasor.core.Action;
import net.demo.hasor.web.forms.LoginCallBackForm;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
/**
 * OAuth : 登录回调地址
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/account/callback.do")
public class Callback extends Action {
    public void execute(@Params LoginCallBackForm loginForm) {
        //
        // .跳转回来立刻展示一个登录中的页面,由这个承接页展现"登陆中...", 然后异步请求后台进行登陆。
        this.putData("loginForm", loginForm);
        this.putData("csrfToken", this.csrfTokenString());
        this.renderTo("htm", "/account/callback.htm");
        return;
    }
}