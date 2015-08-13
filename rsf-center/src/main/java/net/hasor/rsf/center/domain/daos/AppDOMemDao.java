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
package net.hasor.rsf.center.domain.daos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.rsf.center.core.dao.AbstractDao;
import net.hasor.rsf.center.core.dao.Dao;
import net.hasor.rsf.center.domain.constant.ErrorCode;
import net.hasor.rsf.center.domain.entity.AppDO;
import net.hasor.rsf.center.domain.form.apps.AppQueryForm;
import org.more.bizcommon.PageResult;
import org.more.bizcommon.ResultDO;
/**
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Dao
public class AppDOMemDao extends AbstractDao<AppDO> {
    /**新增应用*/
    public ResultDO<Integer> saveAsNew(AppDO appDO) {
        ResultDO<Integer> resultDO = new ResultDO<Integer>();
        try {
            int result = this.getSqlExecutor().insert("createByAppDO", appDO);
            resultDO.setResult(result);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setThrowable(e);
            resultDO.setSuccess(false);
            resultDO.addMessage(ErrorCode.DAO_INSERT.setParams());
        }
        return resultDO;
    }
    /**查询应用列表*/
    public PageResult<AppDO> queryAppDOByForm(AppQueryForm pageInfo) {
        PageResult<AppDO> resultDO = new PageResult<AppDO>(pageInfo);
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("pageInfo", pageInfo);
            List<AppDO> result = this.getSqlExecutor().selectList("queryAppDOByForm", parameter);
            resultDO.setResult(result);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setThrowable(e);
            resultDO.setSuccess(false);
            resultDO.addMessage(ErrorCode.DAO_SELECT.setParams());
        }
        return resultDO;
    }
}