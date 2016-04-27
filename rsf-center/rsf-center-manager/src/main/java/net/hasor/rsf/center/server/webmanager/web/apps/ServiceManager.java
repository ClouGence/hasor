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
package net.hasor.rsf.center.server.webmanager.web.apps;
import java.util.Date;
import org.more.bizcommon.PageResult;
import org.more.bizcommon.ResultDO;
import net.hasor.core.Inject;
import net.hasor.plugins.restful.api.MappingTo;
import net.hasor.plugins.restful.api.Params;
import net.hasor.rsf.center.server.webmanager.core.ErrorCode;
import net.hasor.rsf.center.server.webmanager.core.controller.BaseController;
import net.hasor.rsf.center.server.webmanager.domain.daos.DaoProvider;
import net.hasor.rsf.center.server.webmanager.domain.entity.AppDO;
import net.hasor.rsf.center.server.webmanager.domain.entity.ServiceInfoDO;
import net.hasor.rsf.center.server.webmanager.domain.form.apps.ServiceQueryForm;
/**
 * @version : 2015年7月27日 ˆ
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/apps/serviceManager")
public class ServiceManager extends BaseController {
    @Inject
    private DaoProvider daoProvider;
    //
    public void execute(@Params ServiceInfoDO serviceInfo, @Params ServiceQueryForm serviceForm) {
        logger.info("request :" + getRequestURI());
        if (!this.getRequestURI().endsWith(".do")) {
            // 查询
            ResultDO<AppDO> appDO = daoProvider.getAppDao().queryAppDOByID(serviceForm.getAppID());
            getContextMap().put("appDO", appDO.getResult());
            //
            PageResult<ServiceInfoDO> pageResult = daoProvider.getServiceInfoDao().queryServiceInfoDOByForm(serviceForm);
            if (pageResult.getResult() == null || pageResult.getResult().isEmpty()) {
                pageResult.setSuccess(false);
                pageResult.addMessage(ErrorCode.DAO_SELECT_EMPTY.setParams());
            }
            //
            if (!pageResult.isSuccess()) {
                this.getContextMap().put("message", pageResult.firstMessage());
            } else {
                this.getContextMap().put("resultList", pageResult);
                this.getContextMap().put("message", ErrorCode.OK.setParams());
            }
            return;
        } else {
            // 新增
            serviceInfo.setCreateTime(new Date());
            serviceInfo.setModifyTime(new Date());
            serviceInfo.setOnwer(this.getLoginUser().getUserName());
            //
            // ValidData validData = this.validForm("NewApp", appDO);//验证是否可以录入到数据库。
            // if (validData.isValid()) {
            // ResultDO<Integer> resultDO = daoProvider.getAppDao().saveAsNew(appDO);
            // if (!resultDO.isSuccess()) {
            // logger.error("registerApp error->", resultDO.getThrowable());
            // }
            // }
            // System.out.println("/apps/registerApp - " + validData.isValid());
        }
    }
}