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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.ZipUtils;
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
class AddressBucket {
    protected static final Logger logger;
    public static final String    Dynamic = "dynamic";
    public static final String    Static  = "static";
    static {
        logger = LoggerFactory.getLogger(RsfConstants.RsfAddress_Logger);
    }
    //流控&路由
    private volatile FlowControlRef                       flowControlRef;     //默认流控规则引用
    private volatile RuleRef                              ruleRef;
    //原始数据
    private final String                                  serviceID;          //服务ID
    private final String                                  unitName;           //服务所属单元
    private final List<InterAddress>                      allAddressList;     //所有备选地址
    private final List<InterAddress>                      staticAddressList;  //不会失效的备选地址
    private ConcurrentMap<InterAddress, InnerInvalidInfo> invalidAddresses;   //失效状态统计信息
    //
    //下面时计算出来的数据
    private List<InterAddress>                            localUnitAddresses; //本单元地址
    private List<InterAddress>                            availableAddresses; //所有可用地址（包括本地单元）
    //
    public AddressBucket(String serviceID, String unitName) {
        this.serviceID = serviceID;
        this.unitName = unitName;
        this.allAddressList = new CopyOnWriteArrayList<InterAddress>();
        this.staticAddressList = new CopyOnWriteArrayList<InterAddress>();
        this.invalidAddresses = new ConcurrentHashMap<InterAddress, InnerInvalidInfo>();
        this.localUnitAddresses = new ArrayList<InterAddress>();
        this.availableAddresses = new ArrayList<InterAddress>();
        this.refreshAddress();
    }
    /**保存地址列表到zip流中。*/
    public void saveToZip(ZipOutputStream outStream) throws IOException {
        //1.服务地址本
        if (this.allAddressList.isEmpty() == false) {
            StringBuilder strLogs = new StringBuilder();
            StringWriter strWriter = new StringWriter();
            BufferedWriter bfwriter = new BufferedWriter(strWriter);
            for (InterAddress inter : this.allAddressList) {
                if (this.staticAddressList.contains(inter)) {
                    strLogs.append("S|");
                } else {
                    strLogs.append("D|");
                }
                strLogs.append(inter.toString() + " , ");
                bfwriter.write(inter.toString());
                bfwriter.newLine();
            }
            bfwriter.flush();
            logger.info("bucket save list -> {}", strLogs.toString());
            String salName = this.serviceID + RsfConstants.ServiceAddressList_ZipEntry;
            try {
                String comment = "the address List of [" + salName + "] service.";
                ZipUtils.writeEntry(outStream, strWriter.toString(), salName, comment);
                logger.info("bucket save to entry -> {} ,finish.", salName);
            } catch (Exception e) {
                logger.error("bucket save to entry -> {} ,error -> {}", salName, e.getMessage(), e);
            }
        }
        //2.保存流控规则
        if (this.flowControlRef != null && StringUtils.isNotBlank(this.flowControlRef.flowControlScript)) {
            String fclName = this.serviceID + RsfConstants.FlowControlRef_ZipEntry;
            try {
                String comment = "the flowControlRef of [" + this.serviceID + "] service.";
                ZipUtils.writeEntry(outStream, this.flowControlRef.flowControlScript, fclName, comment);
                logger.info("flowControlRef save to entry -> {} ,finish.", fclName);
            } catch (Exception e) {
                logger.error("flowControlRef save to entry -> {} ,error -> {}", fclName, e.getMessage(), e);
            }
        }
        //3.保存路由脚本
        if (this.ruleRef != null) {
            String slsName = this.serviceID + RsfConstants.ServiceLevelScript_ZipEntry;//服务级路由脚本
            try {
                String comment = "the ServiceLevelScript of [" + this.serviceID + "] service.";
                String script = this.ruleRef.getServiceLevel().getScript();
                ZipUtils.writeEntry(outStream, script, slsName, comment);
                logger.info("ServiceLevelScript save to entry -> {} ,finish.", slsName);
            } catch (Exception e) {
                logger.error("ServiceLevelScript save to entry -> {} ,error -> {}", slsName, e.getMessage(), e);
            }
            String mlsName = this.serviceID + RsfConstants.MethodLevelScript_ZipEntry;//方法级路由脚本
            try {
                String comment = "the MethodLevelScript of [" + this.serviceID + "] service.";
                String script = this.ruleRef.getMethodLevel().getScript();
                ZipUtils.writeEntry(outStream, script, mlsName, comment);
                logger.info("MethodLevelScript save to entry -> {} ,finish.", mlsName);
            } catch (Exception e) {
                logger.error("MethodLevelScript save to entry -> {} ,error -> {}", mlsName, e.getMessage(), e);
            }
            String alsName = this.serviceID + RsfConstants.ArgsLevelScript_ZipEntry;//参数级路由脚本
            try {
                String comment = "the ArgsLevelScript of [" + this.serviceID + "] service.";
                String script = this.ruleRef.getArgsLevel().getScript();
                ZipUtils.writeEntry(outStream, script, alsName, comment);
                logger.info("ArgsLevelScript save to entry -> {} ,finish.", alsName);
            } catch (Exception e) {
                logger.error("ArgsLevelScript save to entry -> {} ,error -> {}", alsName, e.getMessage(), e);
            }
        }
    }
    /**保存地址列表到zip流中。*/
    public void readAddressFromZip(ZipFile zipFile) throws IOException {
        //服务地址本
        String salName = this.serviceID + RsfConstants.ServiceAddressList_ZipEntry;
        List<String> dataBody = ZipUtils.readToList(zipFile, salName);
        if (dataBody != null && dataBody.isEmpty() == false) {
            logger.info("service {} read address form {}", salName, zipFile.getName());
            StringBuilder strBuffer = new StringBuilder();
            ArrayList<InterAddress> staticNewHostSet = new ArrayList<InterAddress>();
            ArrayList<InterAddress> dynamicNewHostSet = new ArrayList<InterAddress>();
            for (String line : dataBody) {
                if (StringUtils.isBlank(line) || line.startsWith("#")) {
                    continue;
                }
                try {
                    if (line.startsWith("S|")) {
                        staticNewHostSet.add(new InterAddress(line.substring(1)));
                        strBuffer.append(line + " , ");
                    } else if (line.startsWith("D|")) {
                        dynamicNewHostSet.add(new InterAddress(line.substring(1)));
                        strBuffer.append(line + " , ");
                    }
                } catch (URISyntaxException e) {
                    logger.info("read address '{}' has URISyntaxException.", line);
                }
            }
            logger.info("bucket read list -> {}", strBuffer.toString());
            this.newAddress(staticNewHostSet, Static);
            this.newAddress(dynamicNewHostSet, Dynamic);
        }
    }
    //
    public String getServiceID() {
        return serviceID;
    }
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
    public void newAddress(Collection<InterAddress> newHostSet, String type) {
        if (newHostSet == null || newHostSet.isEmpty()) {
            logger.error("{} - newHostList is empty.", serviceID);
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
                if (newHost.equals(hasAddress) == true) {
                    doAdd = false;
                    break;
                }
            }
            //2.确定是否需要再次激活。
            for (InterAddress hasAddress : this.invalidAddresses.keySet()) {
                if (newHost.equals(hasAddress) == true) {
                    toAvailable.add(newHost);
                }
            }
            //
            if (doAdd) {
                if (StringUtils.equals(type, Static)) {
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
     * 将地址置为失效的。
     * @param address 失效的地址。
     * @param timeout 失效时长
     */
    public void invalidAddress(InterAddress newInvalid, long timeout) {
        if (this.staticAddressList.contains(newInvalid) == true) {
            return;
        }
        if (this.allAddressList.contains(newInvalid) == false) {
            return;
        }
        InnerInvalidInfo invalidInfo = this.invalidAddresses.get(newInvalid);
        if ((invalidInfo = this.invalidAddresses.putIfAbsent(newInvalid, new InnerInvalidInfo(timeout))) != null) {
            invalidInfo.invalid(timeout);
        } else {
            try {
                synchronized (this) {
                    refreshAvailableAddress();
                }
            } catch (Exception e) {
                logger.error("invalid Address error -> {}.", e);
            }
        }
    }
    /**
     * 将地址从地址本中删除。
     * @param address 要被删除的地址。
     */
    public void removeAddress(InterAddress address) {
        if (this.allAddressList.contains(address) == false) {
            return;
        }
        this.allAddressList.remove(address);
        this.staticAddressList.remove(address);
        this.invalidAddresses.remove(address);
        synchronized (this) {
            refreshAvailableAddress();
        }
    }
    /**
     * 刷新地址计算结果。
     * @return
     */
    public void refreshAddress() {
        synchronized (this) {
            refreshAvailableAddress();
        }
    }
    //
    /**刷新地址*/
    private void refreshAvailableAddress() {
        logger.debug("bucket {} refreshAvailableAddress.", this.getServiceID());
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
            if (unitFlowControl.isLocalUnit(availableList.size(), unitList.size()) == false) {
                unitList = availableList;
            }
        }
        //
        this.availableAddresses = availableList;
        this.localUnitAddresses = unitList;
    }
    //
    /**获取流控规则*/
    public FlowControlRef getFlowControlRef() {
        return this.flowControlRef;
    }
    /**设置流控规则*/
    public void setFlowControlRef(FlowControlRef flowControlRef) {
        this.flowControlRef = flowControlRef;
    }
    public RuleRef getRuleRef() {
        return this.ruleRef;
    }
    public void setRuleRef(RuleRef ruleRef) {
        this.ruleRef = ruleRef;
    }
    @Override
    public String toString() {
        return "AddressBucket - " + this.getServiceID() + ",unit = " + this.unitName + " ,allAddress size = " + this.allAddressList.size();
    }
    /** 从Zip压缩文件中恢复配置 */
    protected static void recoveryConfig(ZipFile zipFile, String serviceID, AddressPool pool) {
        AddressBucket bucker = pool.getBucket(serviceID);
        //1.恢复地址数据
        try {
            bucker.readAddressFromZip(zipFile);
        } catch (Throwable e) {
            logger.error("recoveryConfig address,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //2.恢复流控规则
        try {
            String fclName = serviceID + RsfConstants.FlowControlRef_ZipEntry;
            String flowControl = ZipUtils.readToString(zipFile, fclName);
            if (StringUtils.isNotBlank(flowControl)) {
                pool.updateFlowControl(serviceID, flowControl);
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig flowControl,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //3.恢复服务级路由脚本策略
        try {
            String slsName = serviceID + RsfConstants.ServiceLevelScript_ZipEntry;//服务级路由脚本
            String scriptBody = ZipUtils.readToString(zipFile, slsName);
            if (StringUtils.isNotBlank(scriptBody)) {
                pool.updateServiceRoute(serviceID, scriptBody);
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig serviceRoute,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //4.恢复方法级路由脚本策略
        try {
            String mlsName = serviceID + RsfConstants.MethodLevelScript_ZipEntry;//方法级路由脚本
            String scriptBody = ZipUtils.readToString(zipFile, mlsName);
            if (StringUtils.isNotBlank(scriptBody)) {
                pool.updateMethodRoute(serviceID, scriptBody);
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig methodRoute,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
        //4.恢复参数级路由脚本策略
        try {
            String mlsName = serviceID + RsfConstants.MethodLevelScript_ZipEntry;//方法级路由脚本
            String scriptBody = ZipUtils.readToString(zipFile, mlsName);
            if (StringUtils.isNotBlank(scriptBody)) {
                pool.updateArgsRoute(serviceID, scriptBody);
            }
        } catch (Throwable e) {
            logger.error("recoveryConfig argsRoute,failed-> serviceID ={} message={}.", serviceID, e.getMessage(), e);
        }
    }
}