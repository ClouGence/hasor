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
package net.hasor.registry.server.adapter;
import net.hasor.core.Singleton;
import net.hasor.registry.domain.server.ObjectDO;
import net.hasor.registry.server.domain.Result;
import net.hasor.registry.server.domain.ResultDO;
import net.hasor.rsf.utils.StringUtils;

import java.util.*;
/**
 * 内存Map形式保存Center数据。
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class HashMapDataAdapter implements DataAdapter {
    private Map<String, ObjectDO>    dataPool    = new HashMap<String, ObjectDO>();
    private Map<String, Set<String>> refDataPool = new HashMap<String, Set<String>>();
    @Override
    public Result<ObjectDO> queryObjectByID(String objectID) {
        ObjectDO data = this.dataPool.get(objectID);
        //
        ResultDO<ObjectDO> resultDO = new ResultDO<ObjectDO>();
        resultDO.setResult(data);
        resultDO.setSuccess(true);
        //
        //
        return resultDO;
    }
    @Override
    public Result<Boolean> removeObjectByID(String objectID) {
        this.dataPool.remove(objectID);
        //
        ResultDO<Boolean> resultDO = new ResultDO<Boolean>();
        resultDO.setResult(true);
        resultDO.setSuccess(true);
        return resultDO;
    }
    @Override
    public Result<Boolean> storeObject(ObjectDO object) {
        String objID = object.getObjectID();
        ObjectDO data = this.dataPool.get(objID);
        if (data != null) {
            data.setRefreshTime(new Date());
        } else {
            this.dataPool.put(objID, object);
        }
        //
        String refID = object.getRefObjectID();
        if (StringUtils.isNotBlank(refID)) {
            Set<String> refSet = this.refDataPool.get(refID);
            if (refSet == null) {
                refSet = new HashSet<String>();
                this.refDataPool.put(refID, refSet);
            }
            refSet.add(objID);
        }
        //
        ResultDO<Boolean> resultDO = new ResultDO<Boolean>();
        resultDO.setResult(true);
        resultDO.setSuccess(true);
        return resultDO;
    }
    @Override
    public Result<List<ObjectDO>> queryObjectListByID(String refObjectID, QueryOption option) {
        Set<String> dataSet = this.refDataPool.get(refObjectID);
        if (dataSet == null) {
            ResultDO<List<ObjectDO>> resultDO = new ResultDO<List<ObjectDO>>();
            resultDO.setResult(Collections.EMPTY_LIST);
            resultDO.setSuccess(true);
            return resultDO;
        }
        //
        List<ObjectDO> resultList = new ArrayList<ObjectDO>();
        for (String dataID : dataSet) {
            ObjectDO objectDO = this.dataPool.get(dataID);
            if (objectDO != null) {
                resultList.add(objectDO);
            }
        }
        ResultDO<List<ObjectDO>> resultDO = new ResultDO<List<ObjectDO>>();
        resultDO.setResult(resultList);
        resultDO.setSuccess(true);
        return resultDO;
    }
    @Override
    public Result<Boolean> clearRef(String objectID) {
        this.refDataPool.remove(objectID);
        //
        ResultDO<Boolean> resultDO = new ResultDO<Boolean>();
        resultDO.setResult(true);
        resultDO.setSuccess(true);
        return resultDO;
    }
    @Override
    public Result<Boolean> refreshObject(String objectID) {
        ObjectDO data = this.dataPool.get(objectID);
        ResultDO<Boolean> resultDO = new ResultDO<Boolean>();
        if (data != null) {
            data.setRefreshTime(new Date());
            resultDO.setResult(true);
        } else {
            resultDO.setResult(false);
        }
        resultDO.setSuccess(true);
        return resultDO;
    }
}