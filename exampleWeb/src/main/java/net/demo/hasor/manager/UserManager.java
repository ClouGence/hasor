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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.demo.hasor.manager;
import net.demo.hasor.datadao.UserDAO;
import net.demo.hasor.datadao.UserSourceDAO;
import net.demo.hasor.domain.UserDO;
import net.demo.hasor.domain.UserSourceDO;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.db.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * 用户Manager
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class UserManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private UserSourceDAO userSourceDAO;
    @Inject
    private UserDAO       userDAO;
    //
    public UserDO getUserByProvider(String provider, String uniqueID) throws SQLException {
        UserSourceDO sourceDO = this.userSourceDAO.queryByUnique(provider, uniqueID);
        if (sourceDO == null || sourceDO.getUserID() <= 0) {
            return null;
        }
        UserDO userDO = this.userDAO.queryById(sourceDO.getUserID());
        if (userDO == null) {
            return null;
        }
        //
        if (userDO.getUserSourceList() == null) {
            userDO.setUserSourceList(new ArrayList<UserSourceDO>());
        }
        userDO.getUserSourceList().add(sourceDO);
        //
        return userDO;
    }
    //
    @Transactional
    public long newUser(UserDO userDO) throws SQLException {
        // 1. 保存用户数据 2. 保存携带的外部登录信息数据
        int userResult = this.userDAO.insertUser(userDO);
        if (userResult > 0) {
            List<UserSourceDO> sourceList = userDO.getUserSourceList();
            if (sourceList != null) {
                for (UserSourceDO sourceDO : sourceList) {
                    sourceDO.setUserID(userDO.getUserID());
                    //
                    int sourceResult = this.userSourceDAO.insertUserSource(sourceDO);
                    if (sourceResult <= 0) {
                        throw new IllegalStateException("登录信息保存失败。");
                    }
                }
            }
        }
        return userDO.getUserID();
    }
    @Transactional
    public int updateAccessInfo(UserDO userDO, String provider, UserSourceDO result) throws SQLException {
        return this.userSourceDAO.updateUserSource(provider, userDO.getUserID(), result);
    }
    @Transactional
    public void loginUpdate(UserDO userDO, String provider) throws SQLException {
        this.userDAO.loginUpdate(userDO.getUserID());
        this.userSourceDAO.loginUpdateByUserID(provider, userDO.getUserID());
    }
}