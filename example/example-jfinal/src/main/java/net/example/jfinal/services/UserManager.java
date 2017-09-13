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
package net.example.jfinal.services;
import net.example.jfinal.daos.UserDao;
import net.example.jfinal.domain.UserDTO;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.db.transaction.interceptor.Transactional;

import java.sql.SQLException;
import java.util.List;

import static net.hasor.db.transaction.Propagation.REQUIRED;
/**
 *
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class UserManager {
    @Inject
    private UserDao userDao;
    //
    /** 查询列表 */
    public List<UserDTO> queryList() throws Exception {
        return this.userDao.queryList();
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
}