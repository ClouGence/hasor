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
import net.example.hasor.daos.MyDAO;
import net.example.hasor.domain.UserDTO;
import net.hasor.core.Inject;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.web.WebController;
import net.hasor.web.annotation.Any;
import net.hasor.web.annotation.MappingTo;

import java.io.IOException;
import java.sql.SQLException;
/**
 * 登录
 * @version : 2016年11月07日
 * @author 赵永春 (zyc@hasor.net)
 */
@MappingTo("/login.do")
public class Login extends WebController {
    @Inject
    private MyDAO myDAO;
    //
    @Any
    @Transactional // 数据库事务控制注解
    public void execute() throws IOException, SQLException {
        String account = getPara("username");
        String password = getPara("password");
        UserDTO userInfo = myDAO.getUserByAccount(account);
        password = password == null ? "" : password;
        getResponse().setCharacterEncoding("UTF-8");
        if (userInfo != null && password.equals(userInfo.getPassword())) {
            putData("messageInfo", "login ok.");
            putData("userInfo", userInfo);
            renderTo("htm", "succeed.htm");
        } else {
            putData("messageInfo", "login faile.");
            renderTo("htm", "failed.htm");
        }
    }
}