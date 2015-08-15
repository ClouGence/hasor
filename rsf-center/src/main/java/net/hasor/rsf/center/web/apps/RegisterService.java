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
package net.hasor.rsf.center.web.apps;
import java.util.Date;
import org.more.bizcommon.ResultDO;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.api.Params;
import net.hasor.mvc.api.ReqParam;
import net.hasor.rsf.center.core.controller.BaseController;
import net.hasor.rsf.center.core.ioc.Inject;
import net.hasor.rsf.center.domain.daos.DaoProvider;
import net.hasor.rsf.center.domain.entity.AppDO;
import net.hasor.rsf.center.domain.entity.ServiceInfoDO;
/**
 * 
 * @version : 2015年7月27日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/apps/registerService")
public class RegisterService extends BaseController {
    @Inject
    private DaoProvider daoProvider;
    //
    public void execute(@ReqParam("appID") Long appID, @Params ServiceInfoDO serviceInfo) {
        logger.info("request :" + getRequestURI());
        if (!this.getRequestURI().endsWith(".do")) {
            ResultDO<AppDO> appDO = daoProvider.getAppDao().queryAppDOByID(appID);
            getContextMap().put("appDO", appDO.getResult());
            return;
        }
        //
        serviceInfo.setCreateTime(new Date());
        serviceInfo.setModifyTime(new Date());
        serviceInfo.setOnwer(this.getLoginUser().getUserName());
        //
        //        ValidData validData = this.validForm("NewApp", appDO);//验证是否可以录入到数据库。
        //        if (validData.isValid()) {
        //            ResultDO<Integer> resultDO = daoProvider.getAppDao().saveAsNew(appDO);
        //            if (!resultDO.isSuccess()) {
        //                logger.error("registerApp error->", resultDO.getThrowable());
        //            }
        //        }
        //        System.out.println("/apps/registerApp - " + validData.isValid());
    }
}