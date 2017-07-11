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
import net.hasor.core.EventListener;
import net.hasor.rsf.*;
import net.hasor.rsf.address.route.rule.ArgsKey;
import net.hasor.rsf.address.route.rule.DefaultArgsKey;
import net.hasor.rsf.domain.RsfEvent;
import net.hasor.rsf.utils.ExceptionUtils;
import net.hasor.rsf.utils.FilenameUtils;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/**
 * 服务地址池
 * <p>路由策略：随机选址
 * <p>流控规则：服务级、方法级、参数级
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPool implements RsfUpdater {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    //
    private final RsfEnvironment                       rsfEnvironment;
    private final ConcurrentMap<String, AddressBucket> addressPool;
    private final String                               unitName;
    //
    private final AddressCacheResult                   rulerCache;
    private final ArgsKey                              argsKey;
    private final Object                               poolLock;
    //
    public AddressPool(RsfEnvironment rsfEnvironment) {
        String unitName = rsfEnvironment.getSettings().getUnitName();
        this.logger.info("AddressPool unitName at {}", unitName);
        //
        this.rsfEnvironment = rsfEnvironment;
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        this.addressPool = new ConcurrentHashMap<String, AddressBucket>();
        this.unitName = unitName;
        this.rulerCache = new AddressCacheResult(this);
        this.poolLock = new Object();
        //
        String argsKeyType = rsfSettings.getString("hasor.rsfConfig.route.argsKey", DefaultArgsKey.class.getName());
        this.logger.info("argsKey type is {}", argsKeyType);
        try {
            Class<?> type = Class.forName(argsKeyType);
            this.argsKey = (ArgsKey) type.newInstance();
        } catch (Throwable e) {
            this.logger.error("create argsKey " + argsKeyType + " , message = " + e.getMessage(), e);
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        // .接受删除事件,把对应的地址本清除掉。
        rsfEnvironment.getEventContext().addListener(RsfEvent.Rsf_DeleteService, new EventListener<RsfBindInfo<?>>() {
            @Override
            public void onEvent(String event, RsfBindInfo<?> eventData) throws Throwable {
                if (eventData == null) {
                    return;
                }
                removeBucket(eventData.getBindID());
            }
        });
    }
    //
    public AddressBucket getBucket(String serviceID) {
        if (this.addressPool.containsKey(serviceID)) {
            return this.addressPool.get(serviceID);
        } else {
            return null;
        }
    }
    //
    /**
     * 获取本机所属单元（虚机房 or 集群）
     * @return 返回单元名（虚机房 or 集群）
     */
    public String getUnitName() {
        return this.unitName;
    }
    /**
     * 获取所使用的RsfEnvironment
     */
    public RsfEnvironment getRsfEnvironment() {
        return rsfEnvironment;
    }
    /**
     * 所有服务地址快照功能，该接口获得的数据不可以进行写操作。通过这个接口可以获得到此刻地址池中所有服务的：
     * <ol>
     * <li>原始服务地址列表，以{serviceID}_ALL作为key</li>
     * <li>本单元服务地址列表，以{serviceID}_UNIT作为key</li>
     * <li>不可用服务地址列表，以{serviceID}_INVALID作为key</li>
     * <li>所有可用服务地址列表，以{serviceID}作为key</li>
     * <ol>
     * 并不是单元化的列表中是单元化规则计算的结果,规则如果失效单元化列表中讲等同于 all
     */
    public Map<String, List<InterAddress>> allServiceAddressToSnapshot() {
        Map<String, List<InterAddress>> snapshot = new HashMap<String, List<InterAddress>>();
        synchronized (this.poolLock) {
            for (String key : this.addressPool.keySet()) {
                AddressBucket bucket = this.addressPool.get(key);
                snapshot.put(key + "_ALL", bucket.getAllAddresses());
                snapshot.put(key + "_UNIT", bucket.getLocalUnitAddresses());
                snapshot.put(key + "_INVALID", bucket.getInvalidAddresses());
                snapshot.put(key, bucket.getAvailableAddresses());
            }
        }
        return snapshot;
    }
    /**
     * 获取地址池中注册的服务列表。
     * @see net.hasor.rsf.RsfBindInfo#getBindID()
     * @return 返回地址池中注册的服务列表。
     */
    public Set<String> getBucketNames() {
        Set<String> duplicate = new HashSet<String>();
        synchronized (this.poolLock) {
            duplicate.addAll(this.addressPool.keySet());
        }
        return duplicate;
    }
    /**
     * 新增或追加更新服务地址信息。<p>
     * 如果追加的地址是已存在的失效地址，那么updateAddress方法将重新激活这些失效地址。
     * @param serviceID 服务ID。
     * @param newHost 追加更新的地址。
     */
    public void appendStaticAddress(String serviceID, InterAddress newHost) {
        List<InterAddress> newHostSet = Arrays.asList(newHost);
        this.appendStaticAddress(serviceID, newHostSet);
    }
    /**
     * 新增或追加更新服务地址信息。<p>
     * 如果追加的地址是已存在的失效地址，那么updateAddress方法将重新激活这些失效地址。
     * @param serviceID 服务ID。
     * @param newHostSet 追加更新的地址。
     */
    @Override
    public void appendStaticAddress(String serviceID, Collection<InterAddress> newHostSet) {
        this._appendAddress(serviceID, newHostSet, AddressTypeEnum.Static);
    }
    /**
     * 新增或追加更新服务地址信息。<p>
     * 如果追加的地址是已存在的失效地址，那么updateAddress方法将重新激活这些失效地址。
     * @param serviceID 服务ID。
     * @param newHost 追加更新的地址。
     */
    public void appendAddress(String serviceID, InterAddress newHost) {
        List<InterAddress> newHostSet = Arrays.asList(newHost);
        this.appendAddress(serviceID, newHostSet);
    }
    /**
     * 新增或追加更新服务地址信息。<p>
     * 如果追加的地址是已存在的失效地址，那么updateAddress方法将重新激活这些失效地址。
     * @param serviceID 服务ID。
     * @param newHostSet 追加更新的地址。
     */
    @Override
    public void appendAddress(String serviceID, Collection<InterAddress> newHostSet) {
        this._appendAddress(serviceID, newHostSet, AddressTypeEnum.Dynamic);
    }
    private void _appendAddress(String serviceID, Collection<InterAddress> newHostSet, AddressTypeEnum type) {
        String hosts = StringUtils.join(newHostSet.toArray(), ", ");
        this.logger.info("updateAddress of service {} , new Address set = {} ", serviceID, hosts);
        //1.AddressBucketd
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            /*在并发情况下,invalidAddress可能正打算读取AddressBucket,因此要锁住poolLock*/
            synchronized (this.poolLock) {
                AddressBucket newBucket = new AddressBucket(serviceID, this.rsfEnvironment);
                //newBucket.addObserver(this.refreshCacheNotify);
                bucket = this.addressPool.putIfAbsent(serviceID, newBucket);
                if (bucket == null) {
                    bucket = newBucket;
                }
                this.logger.info("newBucket {}", bucket);
            }
        }
        //2.新增服务
        bucket.newAddress(newHostSet, type);
        bucket.refreshAddress();//局部更新
        this.rulerCache.reset();
    }
    /**
     * 将服务的地址设置成临时失效状态。
     * 在{@link net.hasor.rsf.RsfSettings#getInvalidWaitTime()}毫秒之后，失效的地址会重新被列入备选地址池。
     * 置为失效，失效并不意味着永久的。
     * @param address 失效的地址。
     */
    public void invalidAddress(InterAddress address) {
        long invalidWaitTime = rsfEnvironment.getSettings().getInvalidWaitTime();
        /*在并发情况下,newAddress和invalidAddress可能正在执行,因此要锁住poolLock*/
        synchronized (this.poolLock) {
            Set<String> keySet = this.addressPool.keySet();
            for (String bucketKey : keySet) {
                logger.info("serviceID ={} ,invalid address = {} ,bucket is not exist.", bucketKey, address);
                AddressBucket bucket = this.addressPool.get(bucketKey);
                bucket.invalidAddress(address, invalidWaitTime);
                bucket.refreshAddress();
            }
            this.rulerCache.reset();
        }
        this.rulerCache.reset();
    }
    /**
     * 将服务的地址设置成临时失效状态，把地址从服务的地址本中彻底删除。
     * @param serviceID 服务ID。
     * @param invalidAddress 将要删除的地址。
     */
    @Override
    public void removeAddress(String serviceID, InterAddress invalidAddress) {
        this.removeAddress(serviceID, Arrays.asList(invalidAddress));
    }
    /**
     * 将服务的地址设置成临时失效状态，把地址从服务的地址本中彻底删除。
     * @param serviceID 服务ID。
     * @param invalidAddressSet 将要删除的地址。
     */
    @Override
    public void removeAddress(String serviceID, Collection<InterAddress> invalidAddressSet) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            this.logger.info("serviceID ={} ,bucket is not exist.", serviceID);
            return;
        }
        StringBuilder strBuilder = new StringBuilder("");
        if (invalidAddressSet == null || invalidAddressSet.isEmpty()) {
            strBuilder.append("empty.");
        } else {
            for (InterAddress invalidAddress : invalidAddressSet) {
                strBuilder.append(invalidAddress.toHostSchema() + ",");
                bucket.removeAddress(invalidAddress);
                bucket.refreshAddress();
                this.rulerCache.reset();
            }
        }
        long invalidWaitTime = rsfEnvironment.getSettings().getInvalidWaitTime();
        this.logger.info("serviceID ={} ,remove invalidAddress = {} ,wait {} -> active.", serviceID, strBuilder.toString(), invalidWaitTime);
    }
    @Override
    public void removeAddress(InterAddress address) {
        /*在并发情况下,newAddress和invalidAddress可能正在执行,因此要锁住poolLock*/
        synchronized (this.poolLock) {
            Set<String> keySet = this.addressPool.keySet();
            for (String bucketKey : keySet) {
                AddressBucket bucket = this.addressPool.get(bucketKey);
                if (bucket == null) {
                    return;
                }
                this.logger.debug("service {} removeAddress.", bucketKey);
                bucket.removeAddress(address);
            }
            this.rulerCache.reset();
        }
    }
    /**
     * 从地址池中，删除指定服务的地址本。
     * @param serviceID 服务ID。
     */
    public boolean removeBucket(String serviceID) {
        if (this.addressPool.containsKey(serviceID)) {
            this.logger.info("removeAddressBucket serviceID is {}", serviceID);
            this.addressPool.remove(serviceID);
            this.rulerCache.reset();
            return true;
        }
        return false;
    }
    //
    @Override
    public void refreshAddress(String serviceID, List<InterAddress> addressList) {
        /*在并发情况下,newAddress和invalidAddress可能正在执行,因此要锁住poolLock*/
        synchronized (this.poolLock) {
            AddressBucket bucket = this.addressPool.get(serviceID);
            if (bucket == null) {
                return;
            }
            this.logger.debug("service {} refreshCache.", serviceID);
            bucket.refreshAddressToNew(addressList);//刷新地址计算结果
        }
        this.rulerCache.reset();
    }
    /**刷新地址缓存*/
    @Override
    public void refreshAddressCache() {
        /*在并发情况下,newAddress和invalidAddress可能正在执行,因此要锁住poolLock*/
        synchronized (this.poolLock) {
            Set<String> keySet = this.addressPool.keySet();
            for (String bucketKey : keySet) {
                AddressBucket bucket = this.addressPool.get(bucketKey);
                if (bucket == null) {
                    return;
                }
                this.logger.debug("service {} refreshCache.", bucketKey);
                bucket.refreshAddress();//刷新地址计算结果
            }
            this.rulerCache.reset();
        }
    }
    /**
     * 从服务地址本中获取一条可用的地址。<p>当一个服务具有多个地址的情况下，为了保证公平性地址池采取了随机选取的方式（路由策略：随机选址）
     * <ul>
     *  <li>如果地址池中没有定义这个服务的Bucket，那么将会返回一个null。</li>
     *  <li>如果地址本或者地址池上配置了流控机制，那么选择到的地址将会被限制固定的速率，进而限制nextAddress方法的整个QPS。</li>
     *  <li>默认情况下地址的获取，会受到路由规则、流控规则的影响。</li>
     * </ul>
     * 当地址获取和地址更新同时进行时候，不需要保证瞬时的一致性，只要保证最终一致性就好。
     * @param serviceID 服务id。
     * @param methodName 调用该服务的方法名。
     * @param args 方法调用时用到的参数。
     * @return 返回可以使用的地址。
     */
    public InterAddress nextAddress(String serviceID, String methodName, Object[] args) {
        AddressBucket bucket = addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        //
        List<InterAddress> addresses = this.rulerCache.getAddressList(serviceID, methodName, args);
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        //
        InterAddress doCallAddress = null;
        //
        /*并发下不需要保证瞬时的一致性,只要保证最终一致性就好.*/
        FlowControlRef flowControlRef = bucket.getFlowControlRef();
        if (flowControlRef == null) {
            throw new NullPointerException("flowControlRef is null.");
        }
        doCallAddress = flowControlRef.randomFlowControl.getServiceAddress(addresses);
        while (true) {
            boolean check = flowControlRef.speedFlowControl.callCheck(serviceID, methodName, doCallAddress);//QoS
            if (check) {
                break;
            }
        }
        //
        return doCallAddress;
    }
    //
    //
    protected ArgsKey getArgsKey() {
        return this.argsKey;
    }
    /**获取地址路由规则引用。*/
    protected RuleRef getRefRule(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        RuleRef ruleRef = null;
        if (bucket != null && bucket.getRuleRef() != null) {
            ruleRef = bucket.getRuleRef();
        }
        return ruleRef;
    }
    //
    @Override
    public String toString() {
        return "AddressPool[" + this.unitName + "]";
    }
    //
    @Override
    public boolean updateServiceRoute(String serviceID, String scriptBody) {
        return this.updateRoute(serviceID, RouteTypeEnum.ServiceLevel, scriptBody);
    }
    @Override
    public boolean updateMethodRoute(String serviceID, String scriptBody) {
        return this.updateRoute(serviceID, RouteTypeEnum.MethodLevel, scriptBody);
    }
    @Override
    public boolean updateArgsRoute(String serviceID, String scriptBody) {
        return this.updateRoute(serviceID, RouteTypeEnum.ArgsLevel, scriptBody);
    }
    //
    /**
     * 更新服务的流控规则。
     * @param serviceID 应用到的服务。
     * @param flowControl 流控规则
     */
    @Override
    public boolean updateFlowControl(String serviceID, String flowControl) {
        if (StringUtils.isBlank(serviceID)) {
            return false;
        }
        //
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            this.logger.warn("update flowControl service={} -> AddressBucket not exist.", serviceID);
            return false;
        }
        this.logger.info("update flowControl service={} -> update ok", serviceID);
        bucket.updateFlowControl(flowControl);
        this.refreshAddressCache();
        return true;
    }
    /**
     * 更新某个服务的路由规则脚本。
     * @param serviceID 要更新的服务。
     * @param routeType 更新的路由规则类型。
     * @param script 路由规则脚本内容。
     */
    public boolean updateRoute(String serviceID, RouteTypeEnum routeType, String script) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            this.logger.warn("update rules service={} -> AddressBucket not exist.", serviceID);
            return false;
        }
        //
        this.logger.info("update rules service={} -> update ok", serviceID);
        bucket.updateRoute(routeType, script);
        this.refreshAddressCache();
        return true;
    }
    //
    //
    @Override
    public String serviceRoute(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return getServiceRouteByRef(bucket.getRuleRef());
    }
    @Override
    public String methodRoute(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return getMethodRouteByRef(bucket.getRuleRef());
    }
    @Override
    public String argsRoute(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return getArgsRouteByRef(bucket.getRuleRef());
    }
    @Override
    public String flowControl(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return getFlowControlByRef(bucket.getFlowControlRef());
    }
    @Override
    public List<InterAddress> queryAllAddresses(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return Collections.unmodifiableList(bucket.getAllAddresses());
    }
    @Override
    public List<InterAddress> queryAvailableAddresses(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return Collections.unmodifiableList(bucket.getAvailableAddresses());
    }
    @Override
    public List<InterAddress> queryInvalidAddresses(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return Collections.unmodifiableList(bucket.getInvalidAddresses());
    }
    @Override
    public List<InterAddress> queryLocalUnitAddresses(String serviceID) {
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        return Collections.unmodifiableList(bucket.getLocalUnitAddresses());
    }
    private static String getFlowControlByRef(FlowControlRef ruleRef) {
        if (ruleRef == null || ruleRef.flowControlScript == null) {
            return null;
        }
        return ruleRef.flowControlScript;
    }
    private static String getArgsRouteByRef(RuleRef ruleRef) {
        if (ruleRef == null || ruleRef.getArgsLevel() == null) {
            return null;
        }
        return ruleRef.getArgsLevel().getScript();
    }
    private static String getMethodRouteByRef(RuleRef ruleRef) {
        if (ruleRef == null || ruleRef.getMethodLevel() == null) {
            return null;
        }
        return ruleRef.getMethodLevel().getScript();
    }
    private static String getServiceRouteByRef(RuleRef ruleRef) {
        if (ruleRef == null || ruleRef.getServiceLevel() == null) {
            return null;
        }
        return ruleRef.getServiceLevel().getScript();
    }
    //
    //
    // --------------------------------------------------------------------------------------------
    //
    //
    /**保存地址列表到zip流中。*/
    public synchronized void storeConfig(OutputStream outStream) throws IOException {
        this.logger.info("rsf - saveAddress to stream.");
        ZipOutputStream zipStream = null;
        try {
            zipStream = new ZipOutputStream(outStream);
            synchronized (this.poolLock) {
                for (AddressBucket bucket : this.addressPool.values()) {
                    if (bucket != null) {
                        String serviceID = bucket.getServiceID() + ".zip";
                        this.logger.debug("rsf - service saveAddress {} storage to snapshot.", serviceID);
                        ZipEntry entry = new ZipEntry(serviceID);
                        entry.setComment("service config of " + serviceID);
                        zipStream.putNextEntry(entry);
                        bucket.saveToZip(zipStream);
                        zipStream.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            this.logger.error("rsf - saveAddress " + e.getClass().getSimpleName() + " :" + e.getMessage(), e);
            throw e;
        } finally {
            /*这里进行清理。*/
            if (zipStream != null) {
                zipStream.finish();
            }
        }
    }
    /**从保存的地址本中恢复数据。*/
    public synchronized void restoreConfig(InputStream inStream) throws IOException {
        ZipInputStream zipStream = new ZipInputStream(inStream);
        //
        try {
            synchronized (this.poolLock) {
                ZipEntry zipEntry = null;
                while ((zipEntry = zipStream.getNextEntry()) != null) {
                    String serviceID = zipEntry.getName();
                    serviceID = FilenameUtils.getBaseName(serviceID);
                    AddressBucket bucket = this.addressPool.get(serviceID);
                    if (bucket == null) {
                        continue;
                    }
                    bucket.readFromZip(zipStream);
                    zipStream.closeEntry();
                }
            }
        } catch (Exception e) {
            this.logger.error("read the snapshot file error :" + e.getMessage(), e);
        }
    }
}