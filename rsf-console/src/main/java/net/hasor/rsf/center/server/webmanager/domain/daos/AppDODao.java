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
package net.hasor.rsf.center.server.webmanager.domain.daos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.bizcommon.PageResult;
import org.more.bizcommon.ResultDO;
import net.hasor.rsf.center.server.domain.entity.AppDO;
import net.hasor.rsf.center.server.webmanager.core.ErrorCode;
import net.hasor.rsf.center.server.webmanager.core.dao.AbstractDao;
import net.hasor.rsf.center.server.webmanager.core.dao.Dao;
import net.hasor.rsf.center.server.webmanager.domain.form.apps.AppQueryForm;
/**
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Dao
public class AppDODao extends AbstractDao<AppDO> {
    /** 新增应用 */
    public ResultDO<Integer> saveAsNew(AppDO appDO) {
        ResultDO<Integer> resultDO = new ResultDO<Integer>();
        try {
            int result = this.getSqlExecutor().insert("createAppDO", appDO);
            resultDO.setResult(result);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setThrowable(e);
            resultDO.setSuccess(false);
            resultDO.addMessage(ErrorCode.DAO_INSERT.setParams(e.getMessage()));
        }
        return resultDO;
    }
    /** 查询应用列表 */
    public PageResult<AppDO> queryAppDOByForm(AppQueryForm pageInfo) {
        PageResult<AppDO> resultDO = new PageResult<AppDO>(pageInfo);
        try {
            if (pageInfo.getPageSize() == 0) {
                pageInfo.setPageSize(10);
            }
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("pageInfo", pageInfo);
            List<AppDO> result = this.getSqlExecutor().selectList("queryAppDOByForm", parameter);
            int resultCount = this.getSqlExecutor().selectOne("queryAppDOCountByForm", parameter);
            resultDO.setTotalCount(resultCount);
            resultDO.setResult(result);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setThrowable(e);
            resultDO.setSuccess(false);
            resultDO.addMessage(ErrorCode.DAO_SELECT.setParams(e.getMessage()));
        }
        return resultDO;
    }
    /** 新增应用 */
    public ResultDO<AppDO> queryAppDOByID(Long appID) {
        ResultDO<AppDO> resultDO = new ResultDO<AppDO>();
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("appID", appID);
            AppDO result = this.getSqlExecutor().selectOne("queryAppDOByID", parameter);
            if (result == null) {
                resultDO.setSuccess(false);
                resultDO.addMessage(ErrorCode.DAO_SELECT_NODATA.setParams(appID));
                return resultDO;
            }
            resultDO.setResult(result);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setThrowable(e);
            resultDO.setSuccess(false);
            resultDO.addMessage(ErrorCode.DAO_INSERT.setParams(e.getMessage()));
        }
        return resultDO;
    }
}