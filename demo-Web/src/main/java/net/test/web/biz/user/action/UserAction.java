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
package net.test.web.biz.user.action;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.db.orm.PageResult;
import net.hasor.mvc.api.AbstractWebController;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.api.QueryParam;
import net.hasor.mvc.result.Forword;
import net.test.web.biz.user.entity.UserBean;
import net.test.web.biz.user.service.UserService;
/**
 * View层控制器
 * http://localhost:8080/user/execute.do
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserAction extends AbstractWebController implements InjectMembers {
    private UserService userService;
    public void doInject(AppContext appContext) {
        this.userService = appContext.getInstance(UserService.class);
    }
    //
    @Forword
    @MappingTo("/users/index.do")
    public String userList(@QueryParam("pageSize") int pageSize, @QueryParam("index") int pageIndex) {
        //
        if (pageSize < 5) {
            pageSize = 5;
        }
        if (pageIndex < 0) {
            pageSize = 0;
        }
        //
        PageResult<UserBean> pageData = userService.userList(pageSize, pageIndex);
        if (pageData.isSuccess() == false) {
            pageData.setMessage(pageData.getThrowable().getMessage());
        }
        //
        this.setAttr("pageData", pageData);
        //
        return "/user/userList-view.jsp";
    }
}