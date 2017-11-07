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
import net.hasor.registry.access.pusher.RsfPusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * 任务调度
 * @version : 2016年9月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class TaskManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfPusher rsfPusher;
    //
    public void addTask(String serviceID, Task task) {
    }
    //    final int rowCount = this.dataAdapter.getPointCount(serviceID);
    //    final int limitSize = 100;
    //    int rowIndex = 0;
    //        while (rowIndex <= rowCount) {
    //        List<String> targetList = this.dataAdapter.getPoint(serviceID, rowIndex, limitSize);
    //        if (targetList == null || targetList.isEmpty()) {
    //            break;
    //        }
    //        rowIndex = rowIndex + limitSize;
    //        // .推送新的提供者地址
    //        boolean result = this.rsfPusher.removeAddress(serviceID, invalidAddressSet, targetList); // 第一次尝试
    //        if (!result) {
    //            result = this.rsfPusher.removeAddress(serviceID, invalidAddressSet, targetList);     // 第二次尝试
    //            if (!result) {
    //                result = this.rsfPusher.removeAddress(serviceID, invalidAddressSet, targetList); // 第三次尝试
    //            }
    //        }
    //        //
    //        if (!result) {
    //            // TODO
    //        }
    //    }
    //    /* 对订阅做请求全量推送提供者列表 */
    //    private Result<Boolean> requestProviders(InterAddress targetRsfAddress, String serviceID, String protocol) {
    //        //
    //        // .查询提供者列表
    //        QueryOption opt = new QueryOption();
    //        opt.setObjectType(RsfCenterConstants.Center_DataKey_Provider);//尝试过滤结果,只保留Provider数据
    //        String serviceObjectID = RsfCenterConstants.Center_DataKey_Service + serviceID;
    //        Result<List<AbstractInfo>> refList = this.dataAdapter.queryObjectListByID(serviceObjectID, opt);
    //        if (refList == null || !refList.isSuccess()) {
    //            return DateCenterUtils.buildFailedResult(refList);
    //        }
    //        List<AbstractInfo> providerDataList = refList.getResult();
    //        List<InterAddress> providerList = this.filterProviderList(providerDataList, protocol);
    //        //
    //        // .推送提供者地址(三次尝试),即使全部失败也不用担心,依靠客户端主动拉取来换的最终成功
    //        boolean result = false;
    //        if (providerList != null && !providerList.isEmpty()) {
    //            Result<AbstractInfo> serviceResult = this.dataAdapter.queryObjectByID(serviceObjectID);
    //            if (serviceResult == null || !serviceResult.isSuccess() || serviceResult.getResult() == null) {
    //                return DateCenterUtils.buildFailedResult(serviceResult);
    //            }
    //            List<InterAddress> target = Collections.singletonList(targetRsfAddress);
    //            result = this.rsfPusher.refreshAddress(serviceID, providerList, target);            // 第一次尝试
    //            if (!result) {
    //                result = this.rsfPusher.refreshAddress(serviceID, providerList, target);        // 第二次尝试
    //                if (!result) {
    //                    result = this.rsfPusher.refreshAddress(serviceID, providerList, target);    // 第三次尝试
    //                }
    //            }
    //        }
    //        //
    //        // .返回结果
    //        ResultDO<Boolean> requestResult = new ResultDO<Boolean>();
    //        requestResult.setSuccess(true);
    //        if (!result) {
    //            requestResult.setResult(false);
    //            requestResult.setErrorInfo(ErrorCode.PushAddressFailed_TooBusy);
    //        } else {
    //            requestResult.setResult(true);
    //            requestResult.setErrorInfo(ErrorCode.OK);
    //        }
    //        return requestResult;
    //    }
    //
    //
    /** 任务 */
    public static class Task {
    }
    /** 增量推送服务地址给全部消费者 or 特定机器 */
    public static class PublishTask extends Task {
        public void setAddressList(List<String> addressList) {
            this.addressList = addressList;
        }
        public void setPublishRange(List<String> publishRange) {
            this.publishRange = publishRange;
        }
    }
    /** 增量推送失效的地址 */
    public static class RemoveTask extends Task {
        public void setAddressList(List<String> addressList) {
            this.addressList = addressList;
        }
    }
}