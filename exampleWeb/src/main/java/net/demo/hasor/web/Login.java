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
package net.demo.hasor.web;
import net.demo.hasor.domain.LoginForm;
import net.hasor.restful.RenderData;
import net.hasor.restful.WebController;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
import net.hasor.restful.api.Valid;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/login.do")
public class Login extends WebController {
    //
    public void execute(@Valid("SignIn") @Params LoginForm loginForm, RenderData data) {
        if (!data.isValid()) {
            //
            //验证失败
        } else {
            //
            //验证通过
        }
        data.viewName("/login.htm");
    }
}