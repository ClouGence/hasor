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
package net.hasor.rsf.address;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.Hasor;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.address.route.rule.ArgsKey;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.logger.LoggerHelper;
/**
 * 路由计算结果缓存
 * 
 * 接口级    方法级      参数级
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressCacheResult {
    private final BindCenter     bindCenter;
    private final AddressPool    addressPool;
    private volatile CacheResult cacheResultRef; //做引用切换
    private ArgsKey              argsKey;
    //
    public AddressCacheResult(AddressPool addressPool, BindCenter bindCenter) {
        this.bindCenter = Hasor.assertIsNotNull(bindCenter);
        this.addressPool = Hasor.assertIsNotNull(addressPool);
    }
    //
    /**从全部地址中计算执行动态计算并缓存计算结果.*/
    public List<InterAddress> getAddressList(RsfBindInfo<?> info, String methodSign, Object[] args) {
        if (cacheResultRef == null) {
            LoggerHelper.logSevere("getAddressList fail. resultRef is null.");
            return null;
        }
        String serviceID = info.getBindID();
        List<InterAddress> result = null;
        CacheResult resultRef = this.cacheResultRef;
        //
        //1.获取参数级地址列表
        if (this.argsKey != null) {
            Map<String, Map<Object, List<InterAddress>>> methodList = resultRef.argsLevel.get(serviceID);
            if (methodList != null) {
                Map<Object, List<InterAddress>> cacheList = methodList.get(methodSign);
                if (cacheList != null) {
                    Object key = argsKey.eval(args);
                    if (key != null) {
                        result = cacheList.get(methodSign);
                    }
                }
            }
        }
        //
        //2.获取方法级地址列表
        if (result == null) {
            Map<String, List<InterAddress>> cacheList = resultRef.methodLevel.get(serviceID);
            if (cacheList != null) {
                result = cacheList.get(methodSign);
            }
        }
        //3.获取服务级别地址列表
        if (result == null) {
            result = resultRef.serviceLevel.get(serviceID);
        }
        return result;
    }
    /**重置缓存结果*/
    public void reset() {
        Map<String, List<InterAddress>> allAddress = this.addressPool.allServicesSnapshot();
        Collection<String> allServiceIDs = this.addressPool.listServices();
        CacheResult cacheResultRef = new CacheResult();
        //
        for (String serviceID : allServiceIDs) {
            /*计算使用的地址列表(所有可用的/本单元的/本地网络的)*/
            List<InterAddress> all = allAddress.get(serviceID);
            List<InterAddress> unit = allAddress.get(serviceID + "_UNIT");
            RsfBindInfo<Object> binderInfo = this.bindCenter.getService(serviceID);
            if (binderInfo == null) {
                continue;
            }
            //
            //1.计算缓存的服务接口级,地址列表
            List<InterAddress> serviceLevelResult = evalServiceLevel(binderInfo, all, unit);
            serviceLevelResult = (serviceLevelResult == null || serviceLevelResult.isEmpty()) ? unit : serviceLevelResult;
            cacheResultRef.serviceLevel.put(serviceID, unit);
            //
            //2.计算缓存的服务方法级,地址列表
            Map<String, List<InterAddress>> methodLevelResult = new HashMap<String, List<InterAddress>>();
            Method[] mArrays = binderInfo.getBindType().getMethods();
            for (Method m : mArrays) {
                List<InterAddress> methodCache = evalMethodLevel(binderInfo, m, all, unit);
                if (methodCache != null && methodCache.isEmpty() == false) {
                    String key = RsfRuntimeUtils.evalMethodSign(m);
                    methodLevelResult.put(key, methodCache);
                }
            }
            if (methodLevelResult.isEmpty() == false) {
                cacheResultRef.methodLevel.put(serviceID, methodLevelResult);
            }
            //
            //3.计算缓存的服务参数级,地址列表
            Map<String, Map<Object, List<InterAddress>>> argsLevelResult = new HashMap<String, Map<Object, List<InterAddress>>>();
            for (Method m : mArrays) {
                Map<Object, List<InterAddress>> methodCache = evalArgsLevel(binderInfo, m, all, unit);
                if (methodCache != null && methodCache.isEmpty() == false) {
                    String key = RsfRuntimeUtils.evalMethodSign(m);
                    argsLevelResult.put(key, methodCache);
                }
            }
            if (argsLevelResult.isEmpty() == false) {
                cacheResultRef.argsLevel.put(serviceID, argsLevelResult);
            }
            //
        }
        this.cacheResultRef = cacheResultRef;
    }
    //
    //
    //
    //
    //
    //
    private List<InterAddress> evalServiceLevel(RsfBindInfo<?> serviceID, List<InterAddress> all, List<InterAddress> unit) {
        return null;// TODO Auto-generated method stub
    }
    private List<InterAddress> evalMethodLevel(RsfBindInfo<?> serviceID, Method m, List<InterAddress> all, List<InterAddress> unit) {
        return null;// TODO Auto-generated method stub
    }
    private Map<Object, List<InterAddress>> evalArgsLevel(RsfBindInfo<?> serviceID, Method m, List<InterAddress> all, List<InterAddress> unit) {
        return null;// TODO Auto-generated method stub
    }
}
//
class CacheResult {
    public final Map<String, List<InterAddress>>                           serviceLevel; //服务接口级
    public final Map<String, Map<String, List<InterAddress>>>              methodLevel; //方法级
    public final Map<String, Map<String, Map<Object, List<InterAddress>>>> argsLevel;   //参数级
    //
    public CacheResult() {
        this.serviceLevel = new HashMap<String, List<InterAddress>>(); //服务接口级
        this.methodLevel = new HashMap<String, Map<String, List<InterAddress>>>(); //方法级
        this.argsLevel = new HashMap<String, Map<String, Map<Object, List<InterAddress>>>>(); //参数级
    }
}