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
package net.demo.hasor.web.actions.scene;
import net.demo.hasor.web.forms.LoginForm4Scene;
import net.hasor.restful.RenderData;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Params;
import net.hasor.restful.api.Valid;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/scene/login.do")
public class Login4Scene {
    public void execute(@Valid("login") @Params LoginForm4Scene loginForm, RenderData data) {
        if (data.isValid()) {
            data.renderTo("htm", "/userInfo.htm");
        } else {
            data.put("loginForm", loginForm);
            data.renderTo("htm", "/scene.htm");//使用 htm 引擎渲染页面。
        }
    }
}