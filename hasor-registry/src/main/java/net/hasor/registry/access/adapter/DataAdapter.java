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
package net.hasor.registry.access.adapter;
import com.alibaba.fastjson.JSONObject;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.access.ServerSettings;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 服务数据存储检索适配器，负责将数据的操作对应到 DataDao 接口上。
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class DataAdapter {
    @Inject
    private StorageDao     storageDao;
    @Inject
    private ServerSettings centerCfg;
    //
    private ObjectData fillData(String instanceID, String serviceID, ObjectData data) {
        data.setServiceID(serviceID);
        data.setInstanceID(instanceID);
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }
    public boolean storeService(String serviceID, String dataBody) {
        ObjectData data = new ObjectData(dataBody);
        data = this.fillData(null, serviceID, data);
        data.setDataType("Service");
        //
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0”
        data.setDataPath("/" + serviceID);
        return this.storageDao.saveData(data.getDataPath(), data);
    }
    public boolean storePoint(String instanceID, String serviceID, List<String> addressSet, RsfServiceType asType) {
        ObjectData data = new ObjectData(StringUtils.join(addressSet.toArray(), ","));
        data = this.fillData(instanceID, serviceID, data);
        data.setDataType(RsfServiceType.Provider == asType ? "Provider" : "Consumer");
        //
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider/XXXXXXXXXXXXX”
        data.setDataPath("/" + serviceID + "/" + data.getDataType() + "/" + instanceID);
        return this.storageDao.saveData(data.getDataPath(), data);
    }
    public boolean remove(String instanceID, String serviceID) {
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider/XXXXXXXXXXXXX”
        String dataPath = "/" + serviceID + "/" + instanceID;
        return this.storageDao.deleteData(dataPath);
    }
    public boolean storeAddition(String instanceID, String serviceID, RsfServiceType asType, String dataBody) {
        ObjectData data = new ObjectData(dataBody);
        data = this.fillData(instanceID, serviceID, data);
        data.setDataType("Extend");
        //
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider/XXXXXXXXXXXXX/Extend”
        String dataType = RsfServiceType.Provider == asType ? "Provider" : "Consumer";
        data.setDataPath("/" + serviceID + "/" + dataType + "/" + instanceID + "/Extend");
        return this.storageDao.saveData(data.getDataPath(), data);
    }
    //
    //
    public List<String> getPointByServiceID(String serviceID, RsfServiceType asType, int rowIndex, int limit) {
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider”
        String dataType = RsfServiceType.Provider == asType ? "Provider" : "Consumer";
        String dataPath = "/" + serviceID + "/" + dataType;
        //
        List<String> itemList = this.storageDao.querySubList(dataPath, rowIndex, limit);
        if (itemList == null) {
            return null;
        }
        ArrayList<String> dataList = new ArrayList<String>(itemList.size());
        for (String item : itemList) {
            ObjectData dataInfo = this.storageDao.getByPath(item);
            if (testInvalid(dataInfo)) {
                continue;
            }
            String dataBody = dataInfo.getDataBody();
            if (dataBody == null) {
                continue;
            }
            String[] split = dataBody.split(",");
            dataList.addAll(Arrays.asList(split));
        }
        return dataList;
    }
    public int getPointCountByServiceID(String serviceID, RsfServiceType asType) {
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider”
        String dataType = RsfServiceType.Provider == asType ? "Provider" : "Consumer";
        String dataPath = "/" + serviceID + "/" + dataType;
        return this.storageDao.querySubCount(dataPath);
    }
    //
    public RsfServiceType getPointTypeByID(String instanceID, String serviceID) {
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider/XXXXXXXXXXXXX”
        if (this.storageDao.getByPath("/" + serviceID + "/Provider/" + instanceID) != null) {
            return RsfServiceType.Provider;
        }
        if (this.storageDao.getByPath("/" + serviceID + "/Consumer/" + instanceID) != null) {
            return RsfServiceType.Consumer;
        }
        return null;
    }
    public List<String> getPointByID(String instanceID, String serviceID, RsfServiceType asType) {
        //  例：“/[RSF]xxxx.xxxx.xxx-1.0.0/Provider/XXXXXXXXXXXXX”
        String dataType = RsfServiceType.Provider == asType ? "Provider" : "Consumer";
        String dataPath = "/" + serviceID + "/" + dataType + "/" + instanceID;
        ObjectData objectData = this.storageDao.getByPath(dataPath);
        //
        if (objectData != null && !testInvalid(objectData)) {
            return JSONObject.parseArray(objectData.getDataBody(), String.class);
        }
        return null;
    }
    private boolean testInvalid(ObjectData dataInfo) {
        int expireTime = this.centerCfg.getDataExpireTime();
        return dataInfo == null || !((dataInfo.getTimestamp() + expireTime) > System.currentTimeMillis());
    }
}