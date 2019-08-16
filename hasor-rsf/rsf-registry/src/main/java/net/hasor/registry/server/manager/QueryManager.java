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
package net.hasor.registry.server.manager;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.client.domain.ConsumerPublishInfo;
import net.hasor.registry.client.domain.ProviderPublishInfo;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.server.domain.RsfCenterConstants;
import net.hasor.registry.storage.DataAdapter;
import net.hasor.registry.storage.DataEntity;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.hasor.registry.server.utils.CenterUtils.getDataKey;

/**
 * 提供服务的查询能力
 * @version : 2016年9月18日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class QueryManager {
    protected Logger      logger = LoggerFactory.getLogger(getClass());
    @Inject
    private   DataAdapter dataAdapter;
    //

    /** 得到 Consumer 列表，用于异步推送服务的提供者列表 */
    public List<ConsumerPublishInfo> queryConsumerList(List<String> protocol, ServiceID serviceID) {
        String dataKey = getDataKey(serviceID) + "/Consumer/";
        List<DataEntity> consumerDataList = this.dataAdapter.listData(dataKey, target -> {
            long tags = target.getTags();
            return tags == (tags | RsfCenterConstants.TAG_Consumer);
        });
        List<ConsumerPublishInfo> resultList = new ArrayList<ConsumerPublishInfo>(consumerDataList.size());
        //
        begin:
        for (DataEntity data : consumerDataList) {
            ConsumerPublishInfo consumerInfo = JSON.parseObject(data.getDataValue(), ConsumerPublishInfo.class);
            for (String prot : protocol) {
                for (String prot2 : consumerInfo.getProtocol()) {
                    if (prot2.equals(prot)) {
                        resultList.add(consumerInfo);
                        break begin;
                    }
                }
            }
        }
        //
        return resultList;
    }
    //

    /** 查询提供者列表 */
    public List<String> queryProviderList(List<String> protocol, ServiceID serviceID) {
        String dataKey = getDataKey(serviceID) + "/Provider/";
        List<DataEntity> providerDataList = this.dataAdapter.listData(dataKey, target -> {
            long tags = target.getTags();
            return tags == (tags | RsfCenterConstants.TAG_Provider);
        });
        //
        List<String> resultList = new ArrayList<>(providerDataList.size());
        for (DataEntity data : providerDataList) {
            ProviderPublishInfo providerInfo = JSON.parseObject(data.getDataValue(), ProviderPublishInfo.class);
            if (providerInfo == null || providerInfo.getAddressMap() == null) {
                continue;
            }
            //
            Map<String, String> rsfAddressMap = providerInfo.getAddressMap();
            for (String prot : protocol) {
                String addressStr = rsfAddressMap.get(prot);
                if (StringUtils.isNotBlank(addressStr)) {
                    resultList.add(addressStr);
                }
            }
        }
        //
        return resultList;
    }
}