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
import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.hasor.core.Hasor;
import net.hasor.rsf.address.route.rule.ArgsKey;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public List<InterAddress> getAddressList(String serviceID, String methodSign, Object[] args) {
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
                Map<String, List<InterAddress>> cacheList = methodList.get(methodSign);
                if (cacheList != null) {
                    String key = argsKeyBuilder.eval(args);
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
            InnerScriptResourceRef scriptName = this.addressPool.getScriptResources(serviceID);
            //
            //1.计算缓存的服务接口级,地址列表
            List<InterAddress> serviceLevelResult = null;
            if (StringUtils.isBlank(scriptName.serviceLevel)) {
                logger.info("eval routeScript [ServiceLevel], service {} route undefined.", serviceID);
            } else {
                serviceLevelResult = evalServiceLevel(serviceID, scriptName.serviceLevel, all, unit);
            }
            if (serviceLevelResult == null || serviceLevelResult.isEmpty()) {
                serviceLevelResult = unit;/*如果计算结果为空*/
            }
            cacheResultRef.serviceLevel.put(serviceID, unit);
            //
            //2.计算缓存的服务方法级,地址列表
            if (StringUtils.isBlank(scriptName.methodLevel)) {
                logger.info("eval routeScript [MethodLevel], service {} route undefined.", serviceID);
            } else {
                Map<String, List<InterAddress>> methodLevelResult = evalMethodLevel(serviceID, scriptName.serviceLevel, all, unit);
                if (methodLevelResult.isEmpty() == false) {
                    cacheResultRef.methodLevel.put(serviceID, methodLevelResult);/*保存计算结果*/
                }
            }
            //
            //3.计算缓存的服务参数级,地址列表
            if (StringUtils.isBlank(scriptName.argsLevel)) {
                logger.info("eval routeScript [ArgsLevel], service {} route undefined.", serviceID);
            } else if (this.argsKeyBuilder == null) {
                logger.error("argsKeyBuilder is null , evalArgsLevel failed.");
            } else {
                Map<String, Map<String, List<InterAddress>>> argsLevelResult = evalArgsLevel(serviceID, scriptName.serviceLevel, all, unit);
                if (argsLevelResult.isEmpty() == false) {
                    cacheResultRef.argsLevel.put(serviceID, argsLevelResult);/*保存计算结果*/
                }
            }
            //
        }
        logger.info("switch cacheResultRef.");
        this.cacheResultRef = cacheResultRef;
    }
    //
    /* 脚本说明：
     * 
     * 入参：
     *  serviceID   （java.lang.String）
     *  allAddress  （java.util.List<net.hasor.rsf.address.InterAddress>）
     *  unitAddress （java.util.List<net.hasor.rsf.address.InterAddress>）
     * 返回值
     *  java.util.List<net.hasor.rsf.address.InterAddress>
     * 
     * 样例：
     *  def evalAddress(serviceID, allAddress, unitAddress) {
     *      return unitAddress;
     *  }
     * */
    private List<InterAddress> evalServiceLevel(String serviceID, String scriptText, List<InterAddress> all, List<InterAddress> unit) {
        try {
            ScriptEngine engine = createEngine();
            Reader scriptReader = new FileReader(scriptText);
            engine.eval(scriptReader);
            Object[] params = new Object[] { serviceID, all, unit };
            List<InterAddress> result = (List<InterAddress>) ((Invocable) engine).invokeFunction("evalAddress", params);
            return result;
        } catch (Throwable e) {
            logger.error("evalServiceLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    //
    /* 脚本说明：
     * 
     * 入参：
     *  serviceID   （java.lang.String）
     *  allAddress  （java.util.List<net.hasor.rsf.address.InterAddress>）
     *  unitAddress （java.util.List<net.hasor.rsf.address.InterAddress>）
     * 返回值
     *  java.util.Map<java.lang.String,java.util.List<net.hasor.rsf.address.InterAddress>>
     * 
     * 样例：
     *  def evalAddress(serviceID, allAddress, unitAddress) {
     *      //[RSF]sorg.mytest.FooFacse-1.0.0 ，组别：RSF，接口：sorg.mytest.FooFacse，版本：1.0.0
     *      if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
     *          def resultData = []
     *          resultData["insert"]        = [ new InterAddress()]
     *          resultData["queryUserByID"] = unitAddress
     *          return resultData
     *      }
     *      return null;
     *  }
     * */
    private Map<String, List<InterAddress>> evalMethodLevel(String serviceID, String scriptText, List<InterAddress> all, List<InterAddress> unit) {
        try {
            ScriptEngine engine = createEngine();
            Reader scriptReader = new FileReader(scriptName);
            engine.eval(scriptReader);
            Object[] params = new Object[] { bindInfo, m, all, unit };
            List<InterAddress> result = (List<InterAddress>) ((Invocable) engine).invokeFunction("evalAddress", params);
            return result;
        } catch (Throwable e) {
            logger.error("evalMethodLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    //
    /* 脚本说明：
     * 
     * 入参：
     *  serviceID   （java.lang.String）
     *  keyBuilder  （net.hasor.rsf.address.route.rule.ArgsKey）
     *  allAddress  （java.util.List<net.hasor.rsf.address.InterAddress>）
     *  unitAddress （java.util.List<net.hasor.rsf.address.InterAddress>）
     * 返回值
     *  java.util.Map<java.lang.String,java.util.List<net.hasor.rsf.address.InterAddress>>
     * 
     * 样例：
     *  def evalAddress(serviceID, allAddress, unitAddress) {
     *      //[RSF]sorg.mytest.FooFacse-1.0.0 ，组别：RSF，接口：sorg.mytest.FooFacse，版本：1.0.0
     *      if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
     *          def resultData = []
     *          resultData["insert"]        = []
     *          resultData["queryUserByID"] = []
     *          //
     *          resultData["insert"][keyBuilder.eval([])]
     *          
     *          return resultData
     *      }
     *      return null;
     *  }
     * */
    private Map<String, Map<String, List<InterAddress>>> evalArgsLevel(String serviceID, String scriptName, List<InterAddress> all, List<InterAddress> unit) {
        try {
            ScriptEngine engine = createEngine();
            Reader scriptReader = new FileReader(scriptName);// def evalAddress(bindInfo, argsKey, method, allAddress, unitAddress) { return allAddress; }
            engine.eval(scriptReader);
            Object[] params = new Object[] { bindInfo, this.argsKeyBuilder, m, all, unit };
            Map<Object, List<InterAddress>> result = (Map<Object, List<InterAddress>>) ((Invocable) engine).invokeFunction("evalAddress", params);
            return result;
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
    public final Map<String, Map<String, List<InterAddress>>>              methodLevel; //方法级
    public final Map<String, Map<String, Map<String, List<InterAddress>>>> argsLevel;   //参数级
    //
    public CacheResult() {
        this.serviceLevel = new HashMap<String, List<InterAddress>>(); //服务接口级
        this.methodLevel = new HashMap<String, Map<String, List<InterAddress>>>(); //方法级
        this.argsLevel = new HashMap<String, Map<String, Map<String, List<InterAddress>>>>(); //参数级
    }
}