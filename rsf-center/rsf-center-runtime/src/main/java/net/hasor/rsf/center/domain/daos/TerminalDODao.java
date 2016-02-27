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
import org.more.bizcommon.ResultDO;
import net.hasor.rsf.center.core.dao.AbstractDao;
import net.hasor.rsf.center.core.dao.Dao;
import net.hasor.rsf.center.domain.constant.ErrorCode;
import net.hasor.rsf.center.domain.entity.TerminalDO;
/**
 * 终端
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Dao
public class TerminalDODao extends AbstractDao<TerminalDO> {
    public ResultDO<TerminalDO> queryTerminalByIDAndSecret(String terminalID, String secretKey) {
        ResultDO<TerminalDO> resultDO = new ResultDO<TerminalDO>();
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("terminalID", terminalID);
            params.put("accessKey", secretKey);
            TerminalDO terminalDO = this.getSqlExecutor().selectOne("queryTerminalByIDAndSecret", params);
            //
            resultDO.setSuccess(true).setResult(terminalDO);
        } catch (Exception e) {
            resultDO.addMessage(ErrorCode.DAO_SELECT.setParams("queryTerminalByIDAndSecret", //
                    "terminalID= " + terminalID + ",secretKey=" + secretKey + ",errorMessage = " + e.getMessage()));
            resultDO.setSuccess(false).setThrowable(e);
        }
        return resultDO;
    }
}