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
import net.example.hasor.domain.UserDTO;
import net.example.hasor.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.web.WebController;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Params;
import net.hasor.web.annotation.Post;

import java.util.Date;
/**
 * Hasor 方式新增用户
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/addUser.do")
public class AddUser extends WebController {
    @Inject
    private UserManager userManager;
    //
    //
    /* 当http post 方式访问“/user_list.htm”页面时，执行该方法. */
    @Post
    public void addUser(@Params() UserDTO userParam) throws Exception {
        //
        userParam.setCreate_time(new Date());
        userParam.setModify_time(new Date());
        userManager.addUser(userParam);
        //
        String contextPath = getRequest().getContextPath();
        getResponse().sendRedirect(contextPath + "/user_list.htm");
    }
}