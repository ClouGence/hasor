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
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.enums.ErrorCodes;
import net.demo.hasor.manager.UserManager;
import net.hasor.core.Inject;
import net.hasor.restful.RenderData;
import net.hasor.restful.api.MappingTo;

import java.io.IOException;
/**
 * 个人首页
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/account/my.htm")
public class My extends Action {
    @Inject
    private UserManager userManager;
    //
    public void execute(RenderData data) throws IOException {
        //
        if (!isLogin()) {
            String ctx_path = data.getAppContext().getServletContext().getContextPath();
            data.getHttpResponse().sendRedirect(ctx_path + "/account/login.htm?redirectURI=" + ctx_path + "/account/my.htm");
        }
        //
        UserDO user = this.userManager.getUserByID(this.getUserID());
        if (user == null) {
            sendError(ErrorCodes.RESULT_NULL.getMsg());
            return;
        }
        this.putData("userData", user);
    }
}