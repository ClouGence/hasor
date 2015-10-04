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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
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
public class AddressBucket {
    protected Logger                                 logger = LoggerFactory.getLogger(getClass());
    //流控规则
    private volatile FlowControlRef                  flowControlRef;                              //默认流控规则引用
    private int                                      invalidTryCount;                             //失效连接重试最大允许次数
    //原始数据
    private final String                             serviceID;                                   //服务ID
    private final String                             unitName;                                    //服务所属单元
    private final List<InterAddress>                 allAddressList;                              //所有备选地址
    private ConcurrentMap<InterAddress, InvalidInfo> invalidAddresses;                            //失效状态统计信息
    //
    //下面时计算出来的数据
    private List<InterAddress>                       localUnitAddresses;                          //本单元地址
    private List<InterAddress>                       availableAddresses;                          //所有可用地址（包括本地单元）
    //
    //
    public AddressBucket(String serviceID, String unitName, int invalidTryCount) {
        this.serviceID = serviceID;
        this.unitName = unitName;
        this.invalidTryCount = invalidTryCount;
        this.allAddressList = new ArrayList<InterAddress>();
        this.invalidAddresses = new ConcurrentHashMap<InterAddress, InvalidInfo>();
        this.localUnitAddresses = new ArrayList<InterAddress>();
        this.availableAddresses = new ArrayList<InterAddress>();
        this.refreshAddress();
    }
    /**保存地址列表到zip流中。*/
    public void saveTo(ZipOutputStream outStream, String charsetName) throws IOException {
        ZipEntry entry = new ZipEntry(this.serviceID);
        entry.setComment("the address List of [" + this.serviceID + "] service.");
        outStream.putNextEntry(entry);
        logger.info("bucket save to entry -> {}", this.serviceID);
        {
            StringBuffer strBuffer = new StringBuffer("");
            OutputStreamWriter writer = new OutputStreamWriter(outStream, charsetName);
            BufferedWriter bfwriter = new BufferedWriter(writer);
            for (InterAddress inter : this.allAddressList) {
                strBuffer.append(inter.toString() + " , ");
                bfwriter.write(inter.toString());
                bfwriter.newLine();
            }
            logger.info("bucket save list -> {}", strBuffer.toString());
            bfwriter.flush();
            writer.flush();
            outStream.flush();
        }
        logger.info("bucket save to entry -> {} , finish.", this.serviceID);
        outStream.closeEntry();
    }
    /**保存地址列表到zip流中。*/
    public void readFrom(ZipFile zipFile, String charsetName) throws IOException {
        ZipEntry entry = zipFile.getEntry(this.serviceID);
        if (entry != null) {
            logger.info("bucket read form {}", zipFile.getName());
            InputStream inStream = zipFile.getInputStream(entry);
            InputStreamReader reader = new InputStreamReader(inStream, charsetName);
            BufferedReader bfreader = new BufferedReader(reader);
            String line = null;
            StringBuffer strBuffer = new StringBuffer("");
            ArrayList<URI> newHostList = new ArrayList<URI>();
            while ((line = bfreader.readLine()) != null) {
                try {
                    newHostList.add(new URI(line));
                    strBuffer.append(line + " , ");
                } catch (URISyntaxException e) {
                    logger.info("read address '{}' has URISyntaxException.", line);
                }
            }
            this.newAddress(newHostList);
            logger.info("bucket read list -> {}", strBuffer.toString());
        } else {
            logger.info("bucket read empty , not match record");
        }
    }
    //
    //
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
    public void newAddress(Collection<URI> newHostList) {
        if (newHostList == null || newHostList.isEmpty()) {
            logger.error("{} - newHostList is empty.", serviceID);
            return;
        }
        //
        List<InterAddress> newAddress = new ArrayList<InterAddress>();
        for (URI hostURI : newHostList) {
            boolean doAdd = true;
            InterAddress newHost = null;
            try {
                newHost = new InterAddress(hostURI);
                for (InterAddress hasAddress : this.allAddressList) {
                    if (newHost.equals(hasAddress) == true) {
                        doAdd = false;
                        break;
                    }
                }
            } catch (Throwable e) {
                logger.error("{} append new host '{}' format error.", serviceID, hostURI);
            }
            //
            if (doAdd) {
                newAddress.add(newHost);
            }
        }
        //
        this.allAddressList.addAll(newAddress);
        this.refreshAvailableAddress();
    }
    //
    /**将地址置为失效的。*/
    public void invalidAddress(InterAddress newInvalid, long timeout) {
        for (InterAddress invalid : this.invalidAddresses.keySet()) {
            String strInvalid = invalid.toString();
            String strInvalidNew = newInvalid.toString();
            if (StringUtils.equalsBlankIgnoreCase(strInvalid, strInvalidNew)) {
                return;
            }
        }
        InvalidInfo invalidInfo = null;
        if ((invalidInfo = this.invalidAddresses.putIfAbsent(newInvalid, new InvalidInfo(timeout, this.invalidTryCount))) != null) {
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
    /**强制刷新地址计算结果*/
    public void refreshAddress() {
        synchronized (this) {
            refreshAvailableAddress();
        }
    }
    //
    /**刷新地址*/
    private void refreshAvailableAddress() {
        logger.info("refreshAvailableAddress.");
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
            InvalidInfo info = this.invalidAddresses.get(addressInfo);
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
}