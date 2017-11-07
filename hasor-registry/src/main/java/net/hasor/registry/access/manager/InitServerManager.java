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
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.access.adapter111222.DataAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 负责启动时将服务器的信息登记到数据库。
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Deprecated
@Singleton
public class InitServerManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AppContext  appContext;
    @Inject
    private DataAdapter dataAdapter;
    private long        serverID;
    //
    @Init
    public void init() throws Throwable {
        //        logger.info("init rsf-center to db.");
        //        //
        //        // -Server信息
        //        RsfContext rsfContext = this.appContext.getInstance(RsfContext.class);
        //        InterAddress rsfHost = rsfContext.bindAddress();
        //        //
        //        // -预先查询ServerDO
        //        Result<ServerDO> queryResult = this.dataAdapter.queryServerByHost(rsfHost.getHost(), rsfHost.getPort());
        //        if (queryResult == null || !queryResult.isSuccess()) {
        //            throw new IllegalStateException("init server info to db failed -> query error ," + queryResult.firstMessage());
        //        }
        //        //
        //        // -RsfContext到ServerDO的转换
        //        DataChainContext<RsfContext, ServerDO> dataChainContext = new DataChainContext<RsfContext, ServerDO>() {
        //        };
        //        //        dataChainContext.addDataFilter("dataFilter", this.appContext.getInstance(RsfContext2ServerDODataFilter.class));
        //        ServerDO serverDO = dataChainContext.doChain(rsfContext, queryResult.getResult());
        //        //
        //        // -根据预查结果决定是新增还是更新
        //        if (queryResult.getResult() == null) {
        //            Result<Long> insertResult = this.dataAdapter.insertServer(serverDO);
        //            if (insertResult == null || !insertResult.isSuccess() || insertResult.getResult() == null || insertResult.getResult() <= 0) {
        //                IllegalStateException err = null;
        //                if (insertResult != null) {
        //                    err = new IllegalStateException("init server info to db failed -> insert Server error ," + queryResult.firstMessage());
        //                } else {
        //                    err = new IllegalStateException("init server info to db failed -> insert Result is null.");
        //                }
        //                this.logger.error(err.getMessage(), err);
        //                throw err;
        //            }
        //        } else {
        //            Result<Boolean> updateResult = this.dataAdapter.updateServer(serverDO);
        //            if (updateResult == null || !updateResult.isSuccess() || updateResult.getResult() == null || !updateResult.getResult()) {
        //                IllegalStateException err = null;
        //                if (updateResult != null) {
        //                    err = new IllegalStateException("init server info to db failed -> update Server error ," + queryResult.firstMessage());
        //                } else {
        //                    err = new IllegalStateException("init server info to db failed -> update Result is null.");
        //                }
        //                this.logger.error(err.getMessage(), err);
        //                throw err;
        //            }
        //        }
        //        //
        //        // -记录盐值和serverID
        //        this.serverID = serverDO.getId();
        //        //
    }
    public void beatServer() throws IllegalStateException {
        //        Date beatDate = new Date();
        //        logger.info("rsfCenter beat -> {}", DateCenterUtils.timestamp(beatDate));
        //        Result<Boolean> beatResult = this.dataAdapter.beatOfServer(this.serverID, beatDate);
        //        //
        //        if (beatResult == null || !beatResult.isSuccess() || beatResult.getResult() == null || !beatResult.getResult()) {
        //            IllegalStateException err = null;
        //            if (beatResult != null) {
        //                err = new IllegalStateException("beatOfServer to db failed -> update Server error ," + beatResult.firstMessage());
        //            } else {
        //                err = new IllegalStateException("beatOfServer to db failed -> update Result is null.");
        //            }
        //            throw err;
        //        } else {
        //            this.logger.info("beatOfServer to db success -> serverID= " + this.serverID);
        //        }
        //        //
    }
}
//   服务提供者地址推送客户端
//
//
//    List<InterAddress> pushData = new ArrayList<InterAddress>();
//        for (String atData : info.getRsfAddress()) {
//                pushData.add(new InterAddress(atData));
//                }
////
//// .查询订阅者列表
//final int dataRowCount = this.dataAdapter.queryListCountByType(instanceID, serviceID, RsfServiceType.Consumer.name());
//        if (dataRowCount <= 0) {
//        ResultDO<Void> result = new ResultDO<Void>();
//        result.setSuccess(true);
//        return result;
//        }
//        int dataRowIndex = 0;
//        int pageSize = 100;
//        while (dataRowIndex <= dataRowCount) {
//        List<String> dataList = this.dataAdapter.queryListByType(instanceID, serviceID, RsfServiceType.Consumer.name(), dataRowIndex, pageSize);
//        if (dataList == null || dataList.isEmpty()) {
//        dataRowIndex = dataRowIndex + pageSize;
//        continue;
//        }
//        for (String jsonData : dataList) {
//        // .解析Json获取推送地址
//        InterAddress toPush = null;
//        try {
//        ConsumerInfo consumerInfo = JsonUtils.converToService(jsonData, ConsumerInfo.class);
//        if (consumerInfo != null && StringUtils.isNotBlank(consumerInfo.getRsfAddress())) {
//        toPush = new InterAddress(consumerInfo.getRsfAddress());
//        }
//        } catch (Exception e) {
//        this.logger.error(e.getMessage(), e);
//        continue;
//        }
//        // .推送新的提供者地址
//        boolean result = this.rsfPusher.appendAddress(serviceID, pushData, Arrays.asList(toPush)); // 第一次尝试
//        if (!result) {
//        result = this.rsfPusher.appendAddress(serviceID, pushData, Arrays.asList(toPush));     // 第二次尝试
//        if (!result) {
//        result = this.rsfPusher.appendAddress(serviceID, pushData, Arrays.asList(toPush)); // 第三次尝试
//        }
//        }
//        //
//        if (!result) {
//        // TODO
//        }
//        }
//        }