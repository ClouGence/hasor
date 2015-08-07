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
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.api.Params;
import net.hasor.rsf.center.core.controller.BaseController;
import net.hasor.rsf.center.core.ioc.Inject;
import net.hasor.rsf.center.domain.constant.ErrorCode;
import net.hasor.rsf.center.domain.daos.DaoProvider;
import net.hasor.rsf.center.domain.entity.AppDO;
import org.more.bizcommon.PageResult;
import org.more.bizcommon.Paginator;
/**
 * 
 * @version : 2015年7月27日	ˆ	
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/apps/appManager")
public class AppManager extends BaseController {
    @Inject
    private DaoProvider daoProvider;
    //
    public void execute(@Params Paginator pageInfo) {
        logger.info("requestUIL:" + getRequestURI());
        if (pageInfo == null) {
            pageInfo = new Paginator();
            pageInfo.setCurrentPage(0);
            pageInfo.setPageSize(20);
        }
        PageResult<AppDO> pageResult = daoProvider.getAppDOMemDao().queryList(pageInfo);
        if (!pageResult.isSuccess()) {
            this.getContextMap().put("message", pageResult.firstMessage());
        } else {
            this.getContextMap().put("resultList", pageResult);
            this.getContextMap().put("message", ErrorCode.OK.setParams());
        }
    }
}