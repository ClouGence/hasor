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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Hasor;
import net.hasor.rsf.address.route.rule.ArgsKey;
/**
 * 路由计算结果缓存<br/>
 * 接口级    方法级      参数级
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressCacheResult {
    protected Logger             logger = LoggerFactory.getLogger(getClass());
    //做引用切换
    private volatile CacheResult cacheResultRef;
    private final AddressPool    addressPool;
    private ArgsKey              argsKeyBuilder;
    //
    public AddressCacheResult(AddressPool addressPool) {
        this.addressPool = Hasor.assertIsNotNull(addressPool);
        this.argsKeyBuilder = addressPool.getArgsKey();
    }
    //
    /**从全部地址中计算执行动态计算并缓存计算结果.*/
    public List<InterAddress> getAddressList(String serviceID, String methodName, Object[] args) {
        if (this.cacheResultRef == null) {
            logger.warn("getAddressList fail. resultRef is null.");
            return null;
        }
        List<InterAddress> result = null;
        CacheResult resultRef = this.cacheResultRef;
        //
        //1.获取参数级地址列表
        if (this.argsKeyBuilder != null) {
            Map<String, Map<String, List<InterAddress>>> methodList = resultRef.argsLevel.get(serviceID);
            if (methodList != null) {
                Map<String, List<InterAddress>> cacheList = methodList.get(methodName);
                if (cacheList != null) {
                    String key = argsKeyBuilder.eval(serviceID, methodName, args);
                    if (key != null) {
                        result = cacheList.get(methodName);
                    }
                }
            }
        }
        //
        //2.获取方法级地址列表
        if (result == null) {
            Map<String, List<InterAddress>> cacheList = resultRef.methodLevel.get(serviceID);
            if (cacheList != null) {
                result = cacheList.get(methodName);
            }
        }
        //
        //3.获取服务级别地址列表
        if (result == null) {
            result = resultRef.serviceLevel.get(serviceID);
        }
        return result;
    }
    /**重置缓存结果*/
    public void reset() {
        this.logger.info("reset addressCache.");
        Map<String, List<InterAddress>> allAddress = this.addressPool.allServiceAddressToSnapshot();
        Collection<String> allServiceIDs = this.addressPool.listServices();
        CacheResult cacheResultRef = new CacheResult();
        //
        for (String serviceID : allServiceIDs) {
            /*计算使用的地址列表(所有可用的/本单元的/本地网络的)*/
            List<InterAddress> all = allAddress.get(serviceID);
            List<InterAddress> unit = allAddress.get(serviceID + "_UNIT");
            List<String> allStrList = convertToStr(all);
            RefRule refRule = this.addressPool.getRefRule(serviceID);
            //
            //1.计算缓存的服务接口级,地址列表
            List<InterAddress> serviceLevelResult = null;
            if (refRule.serviceLevel.isEnable()) {
                logger.debug("eval routeScript [ServiceLevel], service {} route undefined.", serviceID);
            } else {
                List<String> serviceLevelResultStr = evalServiceLevel(serviceID, refRule, allStrList);
                serviceLevelResult = convertToAddress(all, serviceLevelResultStr);
            }
            if (serviceLevelResult == null || serviceLevelResult.isEmpty()) {
                serviceLevelResult = unit;/*如果计算结果为空，就使用单元化的地址 -> 如果单元化策略没有配置则单元化地址就是全量地址。*/
            }
            cacheResultRef.serviceLevel.put(serviceID, unit);
            //
            //2.计算缓存的服务方法级,地址列表
            if (refRule.methodLevel.isEnable()) {
                logger.debug("eval routeScript [MethodLevel], service {} route undefined.", serviceID);
            } else {
                Map<String, List<String>> methodLevelResultStr = evalMethodLevel(serviceID, refRule, allStrList);
                if (methodLevelResultStr.isEmpty() == false) {
                    Map<String, List<InterAddress>> methodLevelResult = convertToAddressMethod(all, methodLevelResultStr);
                    cacheResultRef.methodLevel.put(serviceID, methodLevelResult);/*保存计算结果*/
                }
            }
            //
            //3.计算缓存的服务参数级,地址列表
            if (refRule.argsLevel.isEnable()) {
                logger.debug("eval routeScript [ArgsLevel], service {} route undefined.", serviceID);
            } else if (this.argsKeyBuilder == null) {
                logger.error("argsKeyBuilder is null , evalArgsLevel failed.");
            } else {
                Map<String, Map<String, List<String>>> argsLevelResultStr = evalArgsLevel(serviceID, refRule, allStrList);
                if (argsLevelResultStr.isEmpty() == false) {
                    Map<String, Map<String, List<InterAddress>>> argsLevelResult = convertToAddressArgs(all, argsLevelResultStr);
                    cacheResultRef.argsLevel.put(serviceID, argsLevelResult);/*保存计算结果*/
                }
            }
            //
        }
        logger.debug("switch cacheResultRef.");
        this.cacheResultRef = cacheResultRef;
    }
    //
    //
    //
    private static Map<String, Map<String, List<InterAddress>>> convertToAddressArgs(List<InterAddress> all, Map<String, Map<String, List<String>>> argsLevelResult) {
        Map<String, Map<String, List<InterAddress>>> result = new HashMap<String, Map<String, List<InterAddress>>>();
        for (Entry<String, Map<String, List<String>>> ent : argsLevelResult.entrySet()) {
            String key = ent.getKey();
            Map<String, List<InterAddress>> val = convertToAddressMethod(all, ent.getValue());
            if (val != null && !val.isEmpty()) {
                result.put(key, val);
            }
        }
        return result;
    }
    private static Map<String, List<InterAddress>> convertToAddressMethod(List<InterAddress> all, Map<String, List<String>> methodLevelResult) {
        Map<String, List<InterAddress>> result = new HashMap<String, List<InterAddress>>();
        for (Entry<String, List<String>> ent : methodLevelResult.entrySet()) {
            String key = ent.getKey();
            List<InterAddress> val = convertToAddress(all, ent.getValue());
            if (val != null && !val.isEmpty()) {
                result.put(key, val);
            }
        }
        return result;
    }
    private static List<InterAddress> convertToAddress(List<InterAddress> all, List<String> serviceLevelResult) {
        List<InterAddress> result = new ArrayList<InterAddress>(serviceLevelResult.size());
        for (String evalResult : serviceLevelResult) {
            for (InterAddress address : all) {
                if (address.equalsHost(evalResult)) {
                    result.add(address);
                }
            }
        }
        return result;
    }
    private static List<String> convertToStr(List<InterAddress> all) {
        List<String> result = new ArrayList<String>();
        for (InterAddress address : all) {
            result.add(address.getHostPort());
        }
        return result;
    }
    //
    //
    //
    /** 脚本说明：
     * <pre>入参：
     *  serviceID   （String）
     *  allAddress  （List&lt;String&gt;）
     * 返回值
     *  List&lt;String&gt;
     * 
     * 样例：
     *  def List&lt;String&gt; evalAddress(String serviceID,List&lt;String&gt; allAddress)  {
     *      //
     *      //[RSF]sorg.mytest.FooFacse-1.0.0 ，组别：RSF，接口：sorg.mytest.FooFacse，版本：1.0.0
     *      if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
     *          return [
     *              "192.168.1.2:8000",
     *              "192.168.1.2:8001",
     *              "192.168.1.3:8000"
     *          ]
     *      }
     *      return null
     *  }</pre>
     * */
    private List<String> evalServiceLevel(String serviceID, RefRule refRule, List<String> all) {
        try {
            ScriptEngine engine = createEngine();
            Object obj = engine.eval(refRule.serviceLevel.getScript());
            Object[] params = new Object[] { serviceID, all };
            Object result = ((Invocable) engine).invokeFunction("evalAddress", params);
            return (List<String>) result;
        } catch (Throwable e) {
            logger.error("evalServiceLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    //
    /** 脚本说明：
     * <pre>入参：
     *  serviceID   （String）
     *  allAddress  （List&lt;String&gt;）
     * 返回值
     *  Map&lt;String,List&lt;String&gt;&gt;
     * 
     * 样例：
     *  def Map&lt;String,List&lt;String&gt;&gt; evalAddress(String serviceID,List&lt;String&gt; allAddress)  {
     *      //
     *      //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
     *      if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
     *          return [
     *              "println":[
     *                  "192.168.1.2:8000",
     *                  "192.168.1.2:8001",
     *                  "192.168.1.3:8000"
     *              ],
     *              "sayEcho":[
     *                  "192.168.1.2:8000",
     *              ],
     *              "testUserTag":[
     *                  "192.168.1.2:8000",
     *                  "192.168.1.3:8000"
     *              ]
     *          ]
     *      }
     *      return null
     *  }</pre>
     * */
    private Map<String, List<String>> evalMethodLevel(String serviceID, RefRule refRule, List<String> all) {
        try {
            ScriptEngine engine = createEngine();
            engine.eval(refRule.methodLevel);
            Object[] params = new Object[] { serviceID, all };
            Object result = ((Invocable) engine).invokeFunction("evalAddress", params);
            return (Map<String, List<String>>) result;
        } catch (Throwable e) {
            logger.error("evalMethodLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    //
    /** 脚本说明：
     * <pre>入参：
     *  serviceID   （String）
     *  allAddress  （List&lt;String&gt;）
     * 返回值
     *  Map&lt;String, Map&lt;String, List&lt;String&gt;&gt;&gt;
     * 
     * 样例：
     *  def Map&lt;String, Map&lt;String, List&lt;String&gt;&gt;&gt; evalAddress(String serviceID,List&lt;String&gt; allAddress)  {
     *      //
     *      //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
     *      if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
     *          return [
     *              "println":[
     *                  "192.168.1.2:8000",
     *                  "192.168.1.2:8001",
     *                  "192.168.1.3:8000"
     *              ],
     *              "sayEcho":[
     *                  "192.168.1.2:8000",
     *              ],
     *              "testUserTag":[
     *                  "192.168.1.2:8000",
     *                  "192.168.1.3:8000"
     *              ]
     *          ]
     *      }
     *      return null
     *  }</pre>
     * */
    private Map<String, Map<String, List<String>>> evalArgsLevel(String serviceID, RefRule refRule, List<String> all) {
        try {
            ScriptEngine engine = createEngine();
            engine.eval(refRule.argsLevel);
            Object[] params = new Object[] { serviceID, all };
            Object result = ((Invocable) engine).invokeFunction("evalAddress", params);
            return (Map<String, Map<String, List<String>>>) result;
        } catch (Throwable e) {
            logger.error("evalArgsLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    //
    protected ScriptEngine createEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
        Bindings binding = engine.createBindings();
        engine.setBindings(binding, ScriptContext.GLOBAL_SCOPE);
        //
        return engine;
    }
}
//
class CacheResult {
    public final Map<String, List<InterAddress>>                           serviceLevel; //服务接口级
    public final Map<String, Map<String, List<InterAddress>>>              methodLevel;  //方法级
    public final Map<String, Map<String, Map<String, List<InterAddress>>>> argsLevel;    //参数级
    //
    public CacheResult() {
        this.serviceLevel = new HashMap<String, List<InterAddress>>(); //服务接口级
        this.methodLevel = new HashMap<String, Map<String, List<InterAddress>>>(); //方法级
        this.argsLevel = new HashMap<String, Map<String, Map<String, List<InterAddress>>>>(); //参数级
    }
}