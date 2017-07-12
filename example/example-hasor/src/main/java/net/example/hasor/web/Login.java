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
package net.example.hasor.web;
import net.hasor.web.RenderInvoker;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Params;
import net.hasor.web.valid.Valid;
import net.hasor.web.valid.ValidInvoker;

import java.io.IOException;
/**
 * 登录
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/login.{action}")
public class Login {
    public void execute(@Valid() @Params LoginForm loginForm,//
            ValidInvoker valid, RenderInvoker render) throws IOException {
        //
        // .不处理非表单请求
        if (!"do".equalsIgnoreCase(loginForm.getAction())) {
            valid.clearValidErrors();
            return;
        }
        //
        // .帐号验证成功，跳转到 list 页
        if (valid.isValid()) {
            render.getHttpResponse().sendRedirect("/user_list.htm");
            return;
        }
        // .验证失败，回显数据
        render.put("loginForm", loginForm);
        render.renderTo("htm", "/login.htm");
    }
}