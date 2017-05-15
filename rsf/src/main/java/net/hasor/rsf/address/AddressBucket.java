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
package net.hasor.rsf.address;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.StringUtils;
import net.hasor.rsf.utils.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static net.hasor.rsf.domain.RsfConstants.*;
/**
 * 描述：用于接收地址更新同时也用来计算有效和无效地址。
 * 也负责提供服务地址列表集，负责分类存储和处理同一个服务的各种类型的服务地址数据，比如：
 * <ol>
 *  <li>同单元服务地址</li>
 *  <li>有效服务地址</li>
 *  <li>不可用服务地址</li>
 *  <li>全部服务地址</li>
 * </ol>
 * 所有对服务地址的进一 步处理都需要使用{@link #getAvailableAddresses()}获得的地址列表。
 * 如果应用了本地机房策略，则本地
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressBucket extends Observable {
    protected static final Logger addressLogger = LoggerFactory.getLogger(RsfConstants.LoggerName_Address);
    protected static final Logger logger        = LoggerFactory.getLogger(AddressBucket.class);
    //
    //流控&路由
    private final    RsfSettings                                   rsfSettings;        //配置信息
    private final    RsfEnvironment                                rsfEnvironment;     //环境信息
    private volatile FlowControlRef                                flowControlRef;     //默认流控规则引用
    private volatile RuleRef                                       ruleRef;
    //原始数据
    private final    String                                        serviceID;          //服务ID
    private final    String                                        unitName;           //服务所属单元
    private final    List<InterAddress>                            allAddressList;     //所有备选地址
    private final    List<InterAddress>                            staticAddressList;  //不会失效的地址（即使是注册中心推送也不会失效）
    //
    //运行时动态更新的地址
    private          ConcurrentMap<InterAddress, InnerInvalidInfo> invalidAddresses;   //失效状态统计信息
    //下面时计算出来的数据
    private          List<InterAddress>                            localUnitAddresses; //本单元地址
    private          List<InterAddress>                            availableAddresses; //所有可用地址（包括本地单元）
    //
    public AddressBucket(String serviceID, RsfEnvironment rsfEnvironment) {
        this.rsfSettings = rsfEnvironment.getSettings();
        this.rsfEnvironment = rsfEnvironment;
        this.flowControlRef = FlowControlRef.defaultRef(rsfEnvironment);
        this.ruleRef = new RuleRef(null);
        this.serviceID = serviceID;
        this.unitName = rsfSettings.getUnitName();
        this.allAddressList = new CopyOnWriteArrayList<InterAddress>();
        this.staticAddressList = new CopyOnWriteArrayList<InterAddress>();
        this.invalidAddresses = new ConcurrentHashMap<InterAddress, InnerInvalidInfo>();
        this.localUnitAddresses = new ArrayList<InterAddress>();
        this.availableAddresses = new ArrayList<InterAddress>();
        this.refreshAddress();
    }
    //
    public String getServiceID() {
        return serviceID;
    }
    FlowControlRef getFlowControlRef() {
        return this.flowControlRef;
    }
    RuleRef getRuleRef() {
        return this.ruleRef;
    }
    //
    /**获取所有地址（包括本地的和无效的）。*/
    public synchronized List<InterAddress> getAllAddresses() {
        return new ArrayList<InterAddress>(this.allAddressList);
    }
    /**获取计算之后可用的地址。*/
    public synchronized List<InterAddress> getAvailableAddresses() {
        return new ArrayList<InterAddress>(this.availableAddresses);
    }
    /**失效地址。*/
    public synchronized List<InterAddress> getInvalidAddresses() {
        return new ArrayList<InterAddress>(this.invalidAddresses.keySet());
    }
    /**获取计算之后同一单元地址。*/
    public synchronized List<InterAddress> getLocalUnitAddresses() {
        return this.localUnitAddresses;
    }
    //
    /**新增地址支持动态新增*/
    public void newAddress(Collection<InterAddress> newHostSet, AddressTypeEnum type) {
        if (addressLogger.isInfoEnabled()) {
            StringBuilder strBuffer = new StringBuilder();
            for (InterAddress addr : newHostSet) {
                strBuffer.append(addr.toHostSchema());
                strBuffer.append(",");
            }
            addressLogger.info("newAddress({}) -> {}, [{}].", serviceID, type.name(), strBuffer);
        }
        //
        if (newHostSet == null || newHostSet.isEmpty()) {
            logger.warn("address({}) -> newAddress, newHostList is empty. type is {}", serviceID, type.name());
            return;
        }
        //
        List<InterAddress> newAddress = new ArrayList<InterAddress>();
        List<InterAddress> newStaticAddress = new ArrayList<InterAddress>();
        List<InterAddress> toAvailable = new ArrayList<InterAddress>();
        for (InterAddress newHost : newHostSet) {
            if (newHost == null) {
                continue;
            }
            //1.保证不要重复添加。
            boolean doAdd = true;
            for (InterAddress hasAddress : this.allAddressList) {
                if (newHost.equals(hasAddress)) {
                    doAdd = false;
                    break;
                }
            }
            //2.确定是否需要再次激活。
            for (InterAddress hasAddress : this.invalidAddresses.keySet()) {
                if (newHost.equals(hasAddress)) {
                    toAvailable.add(newHost);
                }
            }
            //
            if (doAdd) {
                if (AddressTypeEnum.Static.equals(type)) {
                    newStaticAddress.add(newHost);
                }
                newAddress.add(newHost);
            }
        }
        //
        //添加新地址
        this.allAddressList.addAll(newAddress);
        this.staticAddressList.addAll(newStaticAddress);
        //激活已经失效的地址
        for (InterAddress hasAddress : toAvailable) {
            this.invalidAddresses.remove(hasAddress);
        }
        this.refreshAvailableAddress();
    }
    //
    /**
     * 将地址置为失效的(对于静态地址,该方法无效)。
     * @param newInvalid 失效的地址。
     * @param timeout 失效时长
     */
    public void invalidAddress(InterAddress newInvalid, long timeout) {
        if (this.staticAddressList.contains(newInvalid)) {
            addressLogger.info("invalidAddress({}) -> targetAddress ={} ,addr is static.", serviceID, newInvalid);
            return;//对于静态地址,该方法无效
        }
        if (!this.allAddressList.contains(newInvalid)) {
            addressLogger.warn("invalidAddress({}) -> targetAddress ={} ,addr is not exist.", serviceID, newInvalid);
            return;
        }
        InnerInvalidInfo invalidInfo = this.invalidAddresses.get(newInvalid);
        if ((invalidInfo = this.invalidAddresses.putIfAbsent(newInvalid, new InnerInvalidInfo(timeout))) != null) {
            addressLogger.info("invalidAddress({}) -> targetAddress ={} ,timeout ={}.", serviceID, newInvalid, timeout);
            invalidInfo.invalid(timeout);
        } else {
            try {
                synchronized (this) {
                    refreshAvailableAddress();
                }
            } catch (Exception e) {
                logger.error("address({}) -> invalid Address error -> {}.", serviceID, e.getMessage(), e);
            }
        }
    }
    /**
     * 将地址从地址本中删除。
     * @param address 要被删除的地址。
     */
    public void removeAddress(InterAddress address) {
        if (!this.allAddressList.contains(address)) {
            addressLogger.warn("removeAddress({}) -> targetAddress ={} ,addr is not exist.", serviceID, address);
            return;
        } else {
            addressLogger.info("removeAddress({}) -> targetAddress ={}.", serviceID, address);
        }
        this.allAddressList.remove(address);
        this.staticAddressList.remove(address);
        this.invalidAddresses.remove(address);
        synchronized (this) {
            refreshAvailableAddress();
        }
    }
    /**刷新地址计算结果。*/
    public void refreshAddress() {
        synchronized (this) {
            refreshAvailableAddress();
        }
    }
    public void refreshAddressToNew(List<InterAddress> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            return;
        }
        synchronized (this) {
            if (addressLogger.isInfoEnabled()) {
                StringBuilder strBuffer = new StringBuilder();
                for (InterAddress addr : addressList) {
                    strBuffer.append(addr.toHostSchema());
                    strBuffer.append(",");
                }
                addressLogger.info("refreshAddressToNew({}) -> {}.", serviceID, strBuffer);
            }
            this.allAddressList.clear();
            this.allAddressList.addAll(addressList);
            this.invalidAddresses.clear();
            refreshAvailableAddress();
        }
    }
    //
    /**刷新地址*/
    private void refreshAvailableAddress() {
        //
        //1.计算出有效的地址。
        List<InterAddress> availableList = new ArrayList<InterAddress>();
        for (InterAddress addressInfo : this.allAddressList) {
            boolean doAdd = true;
            for (InterAddress invalid : this.invalidAddresses.keySet()) {
                if (addressInfo.equals(invalid)) {
                    doAdd = false;
                    break;
                }
            }
            //
            //当失效的地址达到重试时间之后，再次刷新地址时候不被列入失效名单。
            InnerInvalidInfo info = this.invalidAddresses.get(addressInfo);
            if (info != null && info.reTry()) {
                doAdd = true;
            }
            if (doAdd) {
                availableList.add(addressInfo);//有效的
            }
        }
        //
        //2.机房单元化过滤
        List<InterAddress> unitList = availableList;
        if (this.flowControlRef != null && this.flowControlRef.unitFlowControl != null) {
            UnitFlowControl unitFlowControl = this.flowControlRef.unitFlowControl;
            unitList = unitFlowControl.siftUnitAddress(unitName, availableList);
            if (unitList == null || unitList.isEmpty()) {
                unitList = availableList;
            }
            if (!unitFlowControl.isLocalUnit(availableList.size(), unitList.size())) {
                unitList = availableList;
            }
        }
        //
        if (addressLogger.isInfoEnabled()) {
            if (addressLogger.isInfoEnabled()) {
                //
                StringBuilder strBuffer1 = new StringBuilder();
                for (InterAddress addr : availableList) {
                    strBuffer1.append(addr.toHostSchema());
                    strBuffer1.append(",");
                }
                addressLogger.info("refreshAvailableAddress({}) -> availableList =[{}].", serviceID, strBuffer1);
                //
                StringBuilder strBuffer2 = new StringBuilder();
                for (InterAddress addr : unitList) {
                    strBuffer2.append(addr.toHostSchema());
                    strBuffer2.append(",");
                }
                addressLogger.info("refreshAvailableAddress({}) -> unitList =[{}].", serviceID, strBuffer2);
            }
        }
        this.availableAddresses = availableList;
        this.localUnitAddresses = unitList;
        this.notifyObservers(this);//发出消息通知自己的状态变化了
    }
    //
    /** 更新服务的流控规则。 */
    public boolean updateFlowControl(String flowControl) {
        if (StringUtils.isBlank(flowControl)) {
            return false;
        }
        FlowControlRef newRef = FlowControlRef.newRef(this.rsfEnvironment, this.flowControlRef);
        newRef.updateFlowControl(flowControl);
        this.flowControlRef = newRef;
        this.refreshAddress();
        return true;
    }
    /** 更新服务的路由脚本。 */
    public boolean updateRoute(RouteTypeEnum routeType, String script) {
        RuleRef newRuleRef = new RuleRef(this.ruleRef);
        boolean updated = RouteTypeEnum.updateScript(routeType, script, newRuleRef);
        if (!updated) {
            logger.warn("address({}) -> update rules -> no change.", serviceID);
            return false;
        } else {
            logger.info("address({}) -> update rules -> update ok", serviceID);
            this.ruleRef = newRuleRef;
            this.refreshAddress();
            return true;
        }
    }
    //
    @Override
    public String toString() {
        return "AddressBucket - " + this.getServiceID() + //
                " ,unit = " + this.unitName + //
                " ,allAddress size = " + this.allAddressList.size();
    }
    //
    //
    // ----------------------------------------- 配置的保存与恢复 -----------------------------------------
    //
    //
    /**保存地址列表到zip流中。*/
    public void saveToZip(OutputStream outStream) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(outStream);
        zipStream.setComment("this config of " + this.getServiceID());
        //
        //1.服务地址本
        if (!this.allAddressList.isEmpty()) {
            StringBuilder strLogs = new StringBuilder();
            StringWriter strWriter = new StringWriter();
            BufferedWriter bfwriter = new BufferedWriter(strWriter);
            for (InterAddress inter : this.allAddressList) {
                if (this.staticAddressList.contains(inter)) {
                    strLogs.append(AddressTypeEnum.Static.getShortType());
                    bfwriter.append(AddressTypeEnum.Static.getShortType());
                } else {
                    strLogs.append(AddressTypeEnum.Dynamic.getShortType());
                    bfwriter.append(AddressTypeEnum.Dynamic.getShortType());
                }
                strLogs.append(inter.toString());
                strLogs.append(" , ");
                bfwriter.write(inter.toString());
                bfwriter.newLine();
            }
            bfwriter.flush();
            logger.info("bucket save list -> {}", strLogs.toString());
            try {
                String comment = "the address List of [" + this.serviceID + "] service.";
                ZipUtils.writeEntry(zipStream, strWriter.toString(), AddressList_ZipEntry, comment);
                logger.info("bucket save to entry -> {} ,finish.", this.serviceID);
            } catch (Exception e) {
                logger.error("bucket save to entry -> {} ,error -> {}", this.serviceID, e.getMessage(), e);
            }
        }
        //
        //2.保存流控规则
        FlowControlRef flowControlRef = this.flowControlRef;
        if (flowControlRef != null && StringUtils.isNotBlank(flowControlRef.flowControlScript)) {
            try {
                String comment = "the flowControlRef of [" + this.serviceID + "] service.";
                ZipUtils.writeEntry(zipStream, flowControlRef.flowControlScript, FlowControlRef_ZipEntry, comment);
                logger.info("flowControlRef save to entry -> {} ,finish.", this.serviceID);
            } catch (Exception e) {
                logger.error("flowControlRef save to entry -> {} ,error -> {}", this.serviceID, e.getMessage(), e);
            }
        }
        //
        //3.保存路由脚本
        RuleRef ruleRef = this.ruleRef;
        if (ruleRef != null) {
            // - 服务级路由脚本
            try {
                String comment = "the ServiceLevelScript of [" + this.serviceID + "] service.";
                String script = ruleRef.getServiceLevel().getScript();
                ZipUtils.writeEntry(zipStream, script, ServiceLevelScript_ZipEntry, comment);
                logger.info("ServiceLevelScript save to entry -> {} ,finish.", this.serviceID);
            } catch (Exception e) {
                logger.error("ServiceLevelScript save to entry -> {} ,error -> {}", this.serviceID, e.getMessage(), e);
            }
            // - 方法级路由脚本
            try {
                String comment = "the MethodLevelScript of [" + this.serviceID + "] service.";
                String script = ruleRef.getMethodLevel().getScript();
                ZipUtils.writeEntry(zipStream, script, MethodLevelScript_ZipEntry, comment);
                logger.info("MethodLevelScript save to entry -> {} ,finish.", this.serviceID);
            } catch (Exception e) {
                logger.error("MethodLevelScript save to entry -> {} ,error -> {}", this.serviceID, e.getMessage(), e);
            }
            // - 参数级路由脚本
            try {
                String comment = "the ArgsLevelScript of [" + this.serviceID + "] service.";
                String script = ruleRef.getArgsLevel().getScript();
                ZipUtils.writeEntry(zipStream, script, ArgsLevelScript_ZipEntry, comment);
                logger.info("ArgsLevelScript save to entry -> {} ,finish.", this.serviceID);
            } catch (Exception e) {
                logger.error("ArgsLevelScript save to entry -> {} ,error -> {}", this.serviceID, e.getMessage(), e);
            }
        }
        //
        //4.关闭输出
        zipStream.finish();
        zipStream.closeEntry();
    }
    //
    /**从流中读取地址列表地址列表到zip流中。*/
    public void readFromZip(InputStream inStream) throws IOException {
        ZipInputStream zipStream = new ZipInputStream(inStream);
        Map<String, byte[]> dataMaps = new HashMap<String, byte[]>();
        ZipEntry zipEntry = null;
        while ((zipEntry = zipStream.getNextEntry()) != null) {
            ByteArrayOutputStream outArray = new ByteArrayOutputStream();
            IOUtils.copy(zipStream, outArray);
            dataMaps.put(zipEntry.getName(), outArray.toByteArray());
        }
        //
        //1.服务地址本
        try {
            if (dataMaps.containsKey(AddressList_ZipEntry)) {                                               // 通
                InputStream dataIn = new ByteArrayInputStream(dataMaps.get(AddressList_ZipEntry));          // 用
                List<String> dataBody = IOUtils.readLines(dataIn);                                          // 模
                if (dataBody != null && !dataBody.isEmpty()) {                                              // 式
                    logger.info("service {} read address form stream", this.serviceID);
                    StringBuilder strBuffer = new StringBuilder();
                    ArrayList<InterAddress> staticNewHostSet = new ArrayList<InterAddress>();
                    ArrayList<InterAddress> dynamicNewHostSet = new ArrayList<InterAddress>();
                    for (String line : dataBody) {
                        if (StringUtils.isBlank(line) || line.startsWith("#")) {
                            continue;
                        }
                        try {
                            if (line.startsWith(AddressTypeEnum.Static.getShortType())) {
                                staticNewHostSet.add(new InterAddress(line.substring(2)));
                                strBuffer.append(line);
                                strBuffer.append(" , ");
                            } else if (line.startsWith(AddressTypeEnum.Dynamic.getShortType())) {
                                dynamicNewHostSet.add(new InterAddress(line.substring(2)));
                                strBuffer.append(line);
                                strBuffer.append(" , ");
                            }
                        } catch (URISyntaxException e) {
                            logger.info("read address '{}' has URISyntaxException.", line);
                        }
                    }
                    logger.info("bucket read list -> {}", strBuffer.toString());
                    this.newAddress(staticNewHostSet, AddressTypeEnum.Static);
                    this.newAddress(dynamicNewHostSet, AddressTypeEnum.Dynamic);
                }
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig address,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //
        //2.流控规则
        try {
            if (dataMaps.containsKey(FlowControlRef_ZipEntry)) {                                            // 通
                InputStream dataIn = new ByteArrayInputStream(dataMaps.get(FlowControlRef_ZipEntry));       // 用
                List<String> dataBody = IOUtils.readLines(dataIn);                                          // 模
                if (dataBody != null && !dataBody.isEmpty()) {                                              // 式
                    String flowControl = StringUtils.join(dataBody.toArray(), "\n");
                    if (StringUtils.isNotBlank(flowControl)) {
                        this.updateFlowControl(flowControl);
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig flowControl,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //
        //3.服务级路由脚本策略
        try {
            if (dataMaps.containsKey(ServiceLevelScript_ZipEntry)) {                                        // 通
                InputStream dataIn = new ByteArrayInputStream(dataMaps.get(ServiceLevelScript_ZipEntry));   // 用
                List<String> dataBody = IOUtils.readLines(dataIn);                                          // 模
                if (dataBody != null && !dataBody.isEmpty()) {                                              // 式
                    String scriptBody = StringUtils.join(dataBody.toArray(), "\n");
                    updateRoute(RouteTypeEnum.ServiceLevel, scriptBody);
                }
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig serviceRoute,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //
        //4.方法级路由脚本策略
        try {
            if (dataMaps.containsKey(MethodLevelScript_ZipEntry)) {                                         // 通
                InputStream dataIn = new ByteArrayInputStream(dataMaps.get(MethodLevelScript_ZipEntry));    // 用
                List<String> dataBody = IOUtils.readLines(dataIn);                                          // 模
                if (dataBody != null && !dataBody.isEmpty()) {                                              // 式
                    String scriptBody = StringUtils.join(dataBody.toArray(), "\n");
                    updateRoute(RouteTypeEnum.MethodLevel, scriptBody);
                }
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig methodRoute,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //
        //5.参数级路由脚本策略
        try {
            if (dataMaps.containsKey(ArgsLevelScript_ZipEntry)) {                                           // 通
                InputStream dataIn = new ByteArrayInputStream(dataMaps.get(ArgsLevelScript_ZipEntry));      // 用
                List<String> dataBody = IOUtils.readLines(dataIn);                                          // 模
                if (dataBody != null && !dataBody.isEmpty()) {                                              // 式
                    String scriptBody = StringUtils.join(dataBody.toArray(), "\n");
                    updateRoute(RouteTypeEnum.ArgsLevel, scriptBody);
                }
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig argsRoute,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
    }
}