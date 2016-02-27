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
package net.hasor.rsf.center.domain.daos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.bizcommon.PageResult;
import net.hasor.rsf.center.core.dao.AbstractDao;
import net.hasor.rsf.center.core.dao.Dao;
import net.hasor.rsf.center.domain.constant.ErrorCode;
import net.hasor.rsf.center.domain.entity.ServiceInfoDO;
import net.hasor.rsf.center.domain.form.ServiceQueryForm;
/**
 * 表示为一个Service
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Dao
public class ServiceInfoDODao extends AbstractDao<ServiceInfoDO> {
    /** 查询应用列表 */
    public PageResult<ServiceInfoDO> queryServiceInfoDOByForm(ServiceQueryForm pageInfo) {
        PageResult<ServiceInfoDO> resultDO = new PageResult<ServiceInfoDO>(pageInfo);
        try {
            if (pageInfo.getPageSize() == 0) {
                pageInfo.setPageSize(10);
            }
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("pageInfo", pageInfo);
            List<ServiceInfoDO> result = this.getSqlExecutor().selectList("queryServiceInfoDOByForm", parameter);
            resultDO.setResult(result);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setThrowable(e);
            resultDO.setSuccess(false);
            resultDO.addMessage(ErrorCode.DAO_SELECT.setParams(e.getMessage()));
        }
        return resultDO;
    }
}