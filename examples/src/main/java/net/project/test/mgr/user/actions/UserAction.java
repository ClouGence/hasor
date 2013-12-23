/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.project.test.mgr.user.actions;
import java.util.List;
import javax.inject.Inject;
import net.hasor.jdbc.core.JdbcTemplate;
import net.hasor.plugins.controller.AbstractController;
import net.hasor.plugins.controller.Controller;
import net.hasor.plugins.result.ext.Forword;
import net.project.test.mgr.user.entity.UserBean;
/**
 * 
 * @version : 2013-12-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Controller("/mgr/user")
public class UserAction extends AbstractController {
    @Inject
    private JdbcTemplate jdbcTemplate;
    //
    @Forword
    public String userList() {
        List<UserBean> userList = jdbcTemplate.queryForList("select * from TB_User", UserBean.class);
        this.setAttr("userList", userList);
        return "/mgr/user/userList.jsp";
    }
}