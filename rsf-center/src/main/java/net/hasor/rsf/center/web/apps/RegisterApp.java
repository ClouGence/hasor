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
package net.hasor.rsf.center.web.apps;
import java.util.Date;
import org.more.bizcommon.ResultDO;
import net.hasor.core.Inject;
import net.hasor.plugins.restful.api.MappingTo;
import net.hasor.plugins.restful.api.Params;
import net.hasor.plugins.valid.ValidData;
import net.hasor.rsf.center.core.controller.BaseController;
import net.hasor.rsf.center.domain.daos.DaoProvider;
import net.hasor.rsf.center.domain.entity.AppDO;
/**
 * @version : 2015年7月27日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/apps/registerApp")
public class RegisterApp extends BaseController {
    @Inject
    private DaoProvider daoProvider;
    //
    public void execute(@Params AppDO appDO) {
        logger.info("request :" + getRequestURI());
        if (!this.getRequestURI().endsWith(".do")) {
            return;
        }
        //
        appDO.setCreateTime(new Date());
        appDO.setModifyTime(new Date());
        appDO.setOnwer(this.getLoginUser().getUserName());
        //
        ValidData validData = this.validForm("NewApp", appDO);// 验证是否可以录入到数据库。
        if (validData.isValid()) {
            ResultDO<Integer> resultDO = daoProvider.getAppDao().saveAsNew(appDO);
            if (!resultDO.isSuccess()) {
                logger.error("registerApp error->", resultDO.getThrowable());
            }
        }
        System.out.println("/apps/registerApp - " + validData.isValid());
    }
}