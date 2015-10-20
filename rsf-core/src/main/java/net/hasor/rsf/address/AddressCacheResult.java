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
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
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
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.address.route.rule.ArgsKey;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 路由计算结果缓存
 * 
 * 接口级    方法级      参数级
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressCacheResult {
    protected Logger             logger = LoggerFactory.getLogger(getClass());
    //做引用切换
    private volatile CacheResult cacheResultRef;
    private final BindCenter     bindCenter;
    private final AddressPool    addressPool;
    private ArgsKey              argsKeyBuilder;
    //
    public AddressCacheResult(AddressPool addressPool, BindCenter bindCenter) {
        this.bindCenter = Hasor.assertIsNotNull(bindCenter);
        this.addressPool = Hasor.assertIsNotNull(addressPool);
        this.argsKeyBuilder = addressPool.getArgsKey();
    }
    //
    /**从全部地址中计算执行动态计算并缓存计算结果.*/
    public List<InterAddress> getAddressList(RsfBindInfo<?> info, String methodSign, Object[] args) {
        if (cacheResultRef == null) {
            logger.warn("getAddressList fail. resultRef is null.");
            return null;
        }
        String serviceID = info.getBindID();
        List<InterAddress> result = null;
        CacheResult resultRef = this.cacheResultRef;
        //
        //1.获取参数级地址列表
        if (this.argsKeyBuilder != null) {
            Map<String, Map<Object, List<InterAddress>>> methodList = resultRef.argsLevel.get(serviceID);
            if (methodList != null) {
                Map<Object, List<InterAddress>> cacheList = methodList.get(methodSign);
                if (cacheList != null) {
                    Object key = argsKeyBuilder.eval(args);
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
        this.logger.info("reset addressCache.");
        Map<String, List<InterAddress>> allAddress = this.addressPool.allServicesSnapshot();
        Collection<String> allServiceIDs = this.addressPool.listServices();
        CacheResult cacheResultRef = new CacheResult();
        //
        for (String serviceID : allServiceIDs) {
            /*计算使用的地址列表(所有可用的/本单元的/本地网络的)*/
            List<InterAddress> all = allAddress.get(serviceID);
            List<InterAddress> unit = allAddress.get(serviceID + "_UNIT");
            RsfBindInfo<Object> binderInfo = this.bindCenter.getService(serviceID);
            ScriptResource scriptName = this.addressPool.getScriptResources(serviceID);
            if (binderInfo == null) {
                continue;
            }
            Method[] mArrays = binderInfo.getBindType().getDeclaredMethods();
            //
            //1.计算缓存的服务接口级,地址列表
            List<InterAddress> serviceLevelResult = null;
            if (StringUtils.isBlank(scriptName.serviceLevel)) {
                logger.info("not specified serviceLevel script.");
            } else if (new File(scriptName.serviceLevel).exists() == false) {
                logger.error("file not found -> " + scriptName.serviceLevel);
            } else {
                serviceLevelResult = evalServiceLevel(scriptName.serviceLevel, binderInfo, all, unit);
            }
            serviceLevelResult = (serviceLevelResult == null || serviceLevelResult.isEmpty()) ? unit : serviceLevelResult;
            cacheResultRef.serviceLevel.put(serviceID, unit);
            //
            //2.计算缓存的服务方法级,地址列表
            if (StringUtils.isBlank(scriptName.methodLevel)) {
                logger.info("not specified methodLevel script.");
            } else if (new File(scriptName.methodLevel).exists() == false) {
                logger.error("file not found -> " + scriptName.methodLevel);
            } else {
                Map<String, List<InterAddress>> methodLevelResult = new HashMap<String, List<InterAddress>>();
                for (Method m : mArrays) {
                    List<InterAddress> methodCache = evalMethodLevel(scriptName.methodLevel, binderInfo, m, all, unit);
                    if (methodCache != null && methodCache.isEmpty() == false) {
                        String key = RsfRuntimeUtils.evalMethodSign(m);
                        methodLevelResult.put(key, methodCache);
                    }
                }
                if (methodLevelResult.isEmpty() == false) {
                    cacheResultRef.methodLevel.put(serviceID, methodLevelResult);
                }
            }
            //
            //3.计算缓存的服务参数级,地址列表
            if (StringUtils.isBlank(scriptName.argsLevel)) {
                logger.info("not specified argsLevel script.");
            } else if (new File(scriptName.argsLevel).exists() == false) {
                logger.error("file not found -> " + scriptName.argsLevel);
            } else if (this.argsKeyBuilder == null) {
                logger.error("argsKeyBuilder is null , evalArgsLevel failed.");
            } else {
                Map<String, Map<Object, List<InterAddress>>> argsLevelResult = new HashMap<String, Map<Object, List<InterAddress>>>();
                for (Method m : mArrays) {
                    Map<Object, List<InterAddress>> methodCache = evalArgsLevel(scriptName.argsLevel, binderInfo, m, all, unit);
                    if (methodCache != null && methodCache.isEmpty() == false) {
                        String key = RsfRuntimeUtils.evalMethodSign(m);
                        argsLevelResult.put(key, methodCache);
                    }
                }
                if (argsLevelResult.isEmpty() == false) {
                    cacheResultRef.argsLevel.put(serviceID, argsLevelResult);
                }
            }
            //
        }
        logger.info("switch cacheResultRef.");
        this.cacheResultRef = cacheResultRef;
    }
    //
    /*
     * 参数说明：
     *  bindInfo    （net.hasor.rsf.RsfBindInfo）
     *  allAddress  （java.util.List<net.hasor.rsf.address.InterAddress>）
     *  unitAddress （java.util.List<net.hasor.rsf.address.InterAddress>）
     * 脚本样例：
     *  def evalAddress(bindInfo, allAddress, unitAddress) { return allAddress; }
     * */
    private List<InterAddress> evalServiceLevel(String scriptName, RsfBindInfo<?> bindInfo, List<InterAddress> all, List<InterAddress> unit) {
        try {
            ScriptEngine engine = createEngine();
            Reader scriptReader = new FileReader(scriptName);// def evalAddress(bindInfo , allAddress , unitAddress) { return allAddress; }
            engine.eval(scriptReader);
            Object[] params = new Object[] { bindInfo, all, unit };
            List<InterAddress> result = (List<InterAddress>) ((Invocable) engine).invokeFunction("evalAddress", params);
            return result;
        } catch (Throwable e) {
            logger.error("evalServiceLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    /*
     * 参数说明：
     *  bindInfo    （net.hasor.rsf.RsfBindInfo）
     *  method      （java.lang.reflect.Method）
     *  allAddress  （java.util.List<net.hasor.rsf.address.InterAddress>）
     *  unitAddress （java.util.List<net.hasor.rsf.address.InterAddress>）
     * 脚本样例：
     *  def evalAddress(bindInfo, method, allAddress, unitAddress) { return allAddress; }
     * */
    private List<InterAddress> evalMethodLevel(String scriptName, RsfBindInfo<?> bindInfo, Method m, List<InterAddress> all, List<InterAddress> unit) {
        try {
            ScriptEngine engine = createEngine();
            Reader scriptReader = new FileReader(scriptName);// def evalAddress(bindInfo, method, allAddress, unitAddress) { return allAddress; }
            engine.eval(scriptReader);
            Object[] params = new Object[] { bindInfo, m, all, unit };
            List<InterAddress> result = (List<InterAddress>) ((Invocable) engine).invokeFunction("evalAddress", params);
            return result;
        } catch (Throwable e) {
            logger.error("evalMethodLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
    /*
     * 参数说明：
     *  bindInfo    （net.hasor.rsf.RsfBindInfo）
     *  argsKey     （net.hasor.rsf.address.route.rule.ArgsKey）
     *  method      （java.lang.reflect.Method）
     *  allAddress  （java.util.List<net.hasor.rsf.address.InterAddress>）
     *  unitAddress （java.util.List<net.hasor.rsf.address.InterAddress>）
     * 脚本样例：
     *  def evalAddress(bindInfo, argsKey, method, allAddress, unitAddress) {
     *      def targetType = method.getDeclaringClass().getName();
     *      if ( targetType == "org.mytest.FooFacse" ) {
     *          def mapData = []
     *          mapData[ argsKey.eval(...) ] = allAddress
     *          mapData[ argsKey.eval(...) ] = unitAddress
     *          return mapData
     *      } else {
     *          ...
     *          return ...
     *      }
     *      return null;
     *  }
     * */
    private Map<Object, List<InterAddress>> evalArgsLevel(String scriptName, RsfBindInfo<?> bindInfo, Method m, List<InterAddress> all, List<InterAddress> unit) {
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
        binding.put("env", addressPool.getRsfEnvironment());
        binding.put("localUnit", addressPool.getUnitName());
        binding.put("debug", addressPool.getRsfEnvironment().isDebug());
        //
        return engine;
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