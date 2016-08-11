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
import net.demo.hasor.web.forms.LoginForm;
import net.hasor.restful.RenderData;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
import net.hasor.restful.api.PathParam;
import net.hasor.restful.api.Valid;
import org.more.util.StringUtils;

import java.io.IOException;
/**
 * 本地登陆
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/account/login.{action}")
public class Login extends Action {
    //
    public void execute(@PathParam("action") String action, @Valid("SignIn") @Params LoginForm loginForm, RenderData data) throws IOException {
        if (StringUtils.equalsIgnoreCase("do", action)) {
            //
            // - 登录请求
            this.putData("loginForm", loginForm);
            if (!data.isValid()) {
                renderTo("htm", "/account/login.htm");//验证失败
            } else {
                renderTo("htm", "/account/login.htm");//验证通过
            }
        } else {
            //
            // - 登录页面
            data.clearValidErrors();//清空验证信息,避免瞎显示
            if (this.isLogin()) {
                String ctx_path = data.getAppContext().getServletContext().getContextPath();
                data.getHttpResponse().sendRedirect(ctx_path + "/account/my.htm");
            }
        }
    }
}