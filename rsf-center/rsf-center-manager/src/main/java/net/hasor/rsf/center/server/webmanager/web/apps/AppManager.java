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
import org.more.bizcommon.PageResult;
import net.hasor.core.Inject;
import net.hasor.plugins.restful.api.MappingTo;
import net.hasor.plugins.restful.api.Params;
import net.hasor.rsf.center.server.core.daos.DaoProvider;
import net.hasor.rsf.center.server.domain.entity.AppDO;
import net.hasor.rsf.center.server.webmanager.core.ErrorCode;
import net.hasor.rsf.center.server.webmanager.core.controller.BaseController;
import net.hasor.rsf.center.server.webmanager.domain.form.apps.AppQueryForm;
/**
 * @version : 2015年7月27日 ˆ
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/apps/appManager")
public class AppManager extends BaseController {
    @Inject
    private DaoProvider daoProvider;
    //
    public void execute(@Params AppQueryForm queryForm) {
        logger.info("requestUIL:" + getRequestURI());
        if (queryForm == null) {
            queryForm = new AppQueryForm();
            queryForm.setCurrentPage(0);
        }
        if (queryForm.getPageSize() == 0) {
            queryForm.setPageSize(10);
        }
        //
        PageResult<AppDO> pageResult = daoProvider.getAppDao().queryAppDOByForm(queryForm);
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
    }
}