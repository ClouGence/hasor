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
package net.demo.springboot.services;
import net.example.domain.domain.UserDO;
import net.demo.springboot.daos.UserDao;
import net.demo.springboot.domain.UserDTO;
import net.hasor.db.Transactional;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.hasor.db.transaction.Propagation.REQUIRED;
/**
 *
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@Component()
public class UserManager {
    @Autowired
    private UserDao userDao;
    //
    /** 查询列表 */
    public List<UserDO> queryList() throws Exception {
        List<UserDTO> userDOs = userDao.queryList();
        List<UserDO> userList = new ArrayList<UserDO>();
        for (UserDTO dto : userDOs) {
            UserDO userDO = new UserDO();
            BeanUtils.copyProperties(userDO, dto);
            userDO.setCreateTime(new Date(dto.getCreate_time().getTime()));
            userDO.setModifyTime(new Date(dto.getModify_time().getTime()));
            userList.add(userDO);
        }
        return userList;
    }
    //
    /** 添加用户（单条数据操作事务性无意义，这里纯属演示） */
    @Transactional(propagation = REQUIRED)
    public void addUser(UserDTO userDO) throws Exception {
        boolean save = this.userDao.insertUser(userDO);
        if (!save) {
            throw new SQLException("保存失败。");
        }
    }
    //
    /** 校验用户 */
    public boolean checkLogin(String account, String password) {
        if (account == null || password == null) {
            return false;
        }
        try {
            UserDTO userDTO = this.userDao.queryUserInfoByAccount(account);
            if (userDTO == null) {
                return false;
            }
            return password.equals(userDTO.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}