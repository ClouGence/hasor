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
package net.demo.hasor.datadao;
import net.demo.hasor.core.AbstractDao;
import net.demo.hasor.domain.UserDO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @version : 2016年08月08日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDAO extends AbstractDao {
    //
    /** 根据用户ID查询用户信息 */
    public UserDO queryById(long userID) throws SQLException {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("userID", userID);
            UserDO result = this.getSqlExecutor().selectOne("user_queryById", parameter);
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    //
    /** 新增用户 */
    public int insertUser(UserDO userDO) throws SQLException {
        try {
            int result = this.getSqlExecutor().insert("user_insert", userDO);
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    //
    /** 登录更新 */
    public int loginUpdate(long userID) throws SQLException {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("userID", userID);
            int result = this.getSqlExecutor().update("user_loginUpdate", parameter);
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    //
    /** 更新基础信息 */
    public long updateUser(long userID, UserDO userDO) throws SQLException {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("userID", userID);
            parameter.put("userInfo", userDO);
            int result = this.getSqlExecutor().update("user_updateInfo", parameter);
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    //
    //    /** 查询应用列表 */
    //    public PageResult<AppDO> queryAppDOByForm(AppQueryForm pageInfo) {
    //        PageResult<AppDO> resultDO = new PageResult<AppDO>(pageInfo);
    //        try {
    //            if (pageInfo.getPageSize() == 0) {
    //                pageInfo.setPageSize(10);
    //            }
    //            Map<String, Object> parameter = new HashMap<String, Object>();
    //            parameter.put("pageInfo", pageInfo);
    //            List<AppDO> result = this.getSqlExecutor().selectList("queryAppDOByForm", parameter);
    //            int resultCount = this.getSqlExecutor().selectOne("queryAppDOCountByForm", parameter);
    //            resultDO.setTotalCount(resultCount);
    //            resultDO.setResult(result);
    //            resultDO.setSuccess(true);
    //        } catch (Exception e) {
    //            resultDO.setThrowable(e);
    //            resultDO.setSuccess(false);
    //            resultDO.addMessage(ErrorCode.DAO_SELECT.setParams(e.getMessage()));
    //        }
    //        return resultDO;
    //    }
}
