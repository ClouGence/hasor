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
package net.hasor.registry.access.manager;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.InstanceInfo;
import net.hasor.registry.access.adapter.DataAdapter;
import net.hasor.registry.access.domain.DateCenterUtils;
import net.hasor.registry.access.domain.ErrorCode;
import net.hasor.registry.access.domain.Result;
import net.hasor.registry.access.domain.ResultDO;
import net.hasor.registry.access.pusher.RsfPusher;
import net.hasor.rsf.domain.RsfServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 提供服务的查询能力
 * @version : 2016年9月18日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class QueryManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private DataAdapter dataAdapter;
    @Inject
    private RsfPusher   rsfPusher;
    //
    /** 请求Center做一次全量推送 */
    public Result<Void> requestProviders(InstanceInfo instance, String serviceID) {
        Result<List<String>> listResult = this.queryProviders(serviceID, instance);
        if (!listResult.isSuccess()) {
            return DateCenterUtils.buildFailedResult(listResult);
        }
        //
        // .异步任务，要求推送所有提供者地址给消费者(全量)
        boolean address = this.rsfPusher.refreshAddress(//
                serviceID, //
                listResult.getResult(),//
                Collections.singletonList(instance.getRsfAddress())//
        );
        //
        ResultDO<Void> result = new ResultDO<Void>();
        result.setSuccess(address);
        result.setErrorInfo(ErrorCode.OK);
        if (!address) {
            result.setErrorInfo(ErrorCode.Failed_PushAddress_System_TooBusy);
        }
        return result;
    }
    //
    /** 查询提供者列表 */
    public Result<List<String>> queryProviders(String serviceID, InstanceInfo target) {
        //
        final int rowCount = this.dataAdapter.getPointCountByServiceID(serviceID, RsfServiceType.Provider);
        final List<String> resultList = new ArrayList<String>(rowCount);
        final int limitSize = 100;
        int rowIndex = 0;
        while (rowIndex <= rowCount) {
            List<String> targetList = this.dataAdapter.getPointByServiceID(serviceID, RsfServiceType.Provider, rowIndex, limitSize);
            rowIndex = rowIndex + limitSize;
            if (targetList == null || targetList.isEmpty()) {
                continue;
            }
            resultList.addAll(targetList);
        }
        //
        ResultDO<List<String>> result = new ResultDO<List<String>>();
        result.setSuccess(true);
        result.setResult(resultList);
        result.setErrorInfo(ErrorCode.OK);
        return result;
    }
}