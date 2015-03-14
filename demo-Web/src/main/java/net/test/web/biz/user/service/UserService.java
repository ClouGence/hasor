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
package net.test.web.biz.user.service;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.db.orm.PageResult;
import net.hasor.db.orm.Paginator;
import net.test.web.biz.user.dao.UserDao;
import net.test.web.biz.user.entity.UserBean;
import org.more.bizcommon.Result;
/**
 * 服务层类。
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserService implements InjectMembers {
    private UserDao userDao = null;
    //
    public void doInject(AppContext appContext) {
        this.userDao = appContext.getInstance(UserDao.class);
    }
    //
    /**列表查询*/
    public PageResult<UserBean> userList(int pageSize, int pageIndex) {
        Paginator page = new Paginator();
        page.setCurrentPage(pageIndex);
        page.setPageSize(pageSize);
        page.setEnable(true);
        //
        try {
            return this.userDao.userList(page);
        } catch (Exception e) {
            return new PageResult<UserBean>(page).setSuccess(false).setMessage(e.getMessage());
        }
    }
    //
    /**新增用户*/
    public Result<Boolean> addUser(String name, String loginName, String loginPassword, String email) {
        UserBean userBean = new UserBean();
        userBean.setName(loginName);
        userBean.setLoginName(loginName);
        userBean.setLoginPassword(loginPassword);
        userBean.setEmail(email);
        //
        userBean.setRegisterTime(new Date());
        userBean.setUserUUID(UUID.randomUUID().toString());
        //
        return this.userDao.createUser(userBean);
    }
    //
    /**删除用户*/
    public Result<Boolean> deleteUser(String userUUID) {
        UserBean userBean = new UserBean();
        userBean.setUserUUID(userUUID);
        return this.userDao.deleteUser(userBean);
    }
    //
    /**更新用户信息*/
    public Result<Boolean> updateUser(String userUUID, Map<String, String> userMap) {
        UserBean userBean = new UserBean();
        userBean.setUserUUID(userUUID);
        userBean.setName(userMap.get("name"));
        userBean.setLoginPassword(userMap.get("password"));
        userBean.setEmail(userMap.get("email"));
        //
        return this.userDao.updateUser(userBean);
    }
}