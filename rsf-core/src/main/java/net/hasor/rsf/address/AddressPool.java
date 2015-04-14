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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.route.flowcontrol.network.NetworkFlowControl;
import net.hasor.rsf.address.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.address.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.address.route.rule.Rule;
import net.hasor.rsf.address.route.rule.RuleParser;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * 服务地址池
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPool {
    private final RsfSettings                          rsfSettings;
    private final ConcurrentMap<String, AddressBucket> addressPool;               //服务地址池Map.
    private final String                               unitName;                  //本机所处单元.
    //
    private final AddressCacheResult                   rulerCache;
    private TreeSet<String>                            flowControlSequence = null; //规则应用顺序.
    private RuleParser                                 ruleParser          = null;
    private volatile FlowControlRef                    flowControlRef      = null; //流控规则引用
    private final Object                               poolLock;
    //
    //
    public AddressPool(String unitName, RsfSettings rsfSettings) {
        this.rsfSettings = rsfSettings;
        this.addressPool = new ConcurrentHashMap<String, AddressBucket>();
        this.unitName = unitName;
        this.rulerCache = new AddressCacheResult(this);
        this.flowControlSequence = new TreeSet<String>();
        this.ruleParser = new RuleParser(rsfSettings);
        this.poolLock = new Object();
        this.flowControlRef = FlowControlRef.defaultRef(rsfSettings);
        this.rulerCache.reset();
    }
    //
    /**获取本机所属单元*/
    public String getUnitName() {
        return this.unitName;
    }
    /**获取网络流控规则*/
    public NetworkFlowControl getNetworkFlowControl() {
        return this.flowControlRef.networkFlowControl;
    }
    /**获取单元化流控规则*/
    public UnitFlowControl getUnitFlowControl() {
        return this.flowControlRef.unitFlowControl;
    }
    /**获取地址选取规则*/
    public RandomFlowControl getRandomFlowControl() {
        return this.flowControlRef.randomFlowControl;
    }
    /**获取QoS速率规则*/
    public SpeedFlowControl getSpeedFlowControl() {
        return this.flowControlRef.speedFlowControl;
    }
    //
    //
    /**新增地址支持动态新增*/
    public void newAddress(RsfBindInfo<?> bindInfo, List<URI> newHostList) throws MalformedURLException {
        //1.AddressBucket
        String serviceID = bindInfo.getBindID();
        AddressBucket bucket = this.addressPool.get(serviceID);
        if (bucket == null) {
            /*在并发情况下,invalidAddress可能正打算读取AddressBucket,因此要锁住poolLock*/
            synchronized (this.poolLock) {
                AddressBucket newBucket = new AddressBucket(serviceID, this);
                bucket = this.addressPool.putIfAbsent(serviceID, newBucket);
                if (bucket == null) {
                    bucket = newBucket;
                }
            }
        }
        //2.新增服务
        bucket.newAddress(newHostList);
        bucket.refreshAddress();//局部更新
        this.rulerCache.reset();
    }
    /**将地址置为失效的。*/
    public void invalidAddress(RsfBindInfo<?> bindInfo, URI newInvalid) {
        /*在并发情况下,newAddress可能正在创建AddressBucket,因此要锁住poolLock*/
        synchronized (this.poolLock) {
            String serviceID = bindInfo.getBindID();
            AddressBucket bucket = this.addressPool.get(serviceID);
            if (bucket == null) {
                return;
            }
        }
        this.rulerCache.reset();
    }
    //
    /**用新的路由规则刷新地址池*/
    public void refreshFlowControl(String flowControl) throws IOException {
        if (StringUtils.isBlank(flowControl) || !flowControl.startsWith("<controlSet") || !flowControl.endsWith("</controlSet>")) {
            LoggerHelper.logSevere("flowControl body format error.");
            return;
        }
        //
        FlowControlRef flowControlRef = FlowControlRef.defaultRef(rsfSettings);
        //
        //1.提取路由配置
        List<String> ruleBodyList = new ArrayList<String>();
        final String tagNameBegin = "<flowControl";
        final String tagNameEnd = "</flowControl>";
        int beginIndex = 0;
        int endIndex = 0;
        while (true) {
            beginIndex = flowControl.indexOf(tagNameBegin, endIndex);
            endIndex = flowControl.indexOf(tagNameEnd, endIndex + tagNameEnd.length());
            if (beginIndex < 0 || endIndex < 0) {
                break;
            }
            String flowControlBody = flowControl.substring(beginIndex, endIndex + tagNameEnd.length());
            ruleBodyList.add(flowControlBody);
        }
        if (ruleBodyList.isEmpty()) {
            LoggerHelper.logWarn("flowControl is empty -> use default settings.");
        }
        //2.解析路由配置
        for (int i = 0; i < ruleBodyList.size(); i++) {
            String controlBody = ruleBodyList.get(i);
            Rule rule = this.ruleParser.ruleSettings(controlBody);
            if (rule == null) {
                continue;
            }
            String simpleName = rule.getClass().getSimpleName();
            LoggerHelper.logConfig("setup flowControl -> %s.", simpleName);
            /*  */if (rule instanceof NetworkFlowControl) {
                flowControlRef.networkFlowControl = (NetworkFlowControl) rule;/*网络规则*/
            } else if (rule instanceof UnitFlowControl) {
                flowControlRef.unitFlowControl = (UnitFlowControl) rule; /*单元规则*/
            } else if (rule instanceof RandomFlowControl) {
                flowControlRef.randomFlowControl = (RandomFlowControl) rule;/*选址规则*/
            } else if (rule instanceof SpeedFlowControl) {
                flowControlRef.speedFlowControl = (SpeedFlowControl) rule; /*速率规则*/
            }
            //
            this.flowControlSequence.add(simpleName);
        }
        //3.引用切换
        this.flowControlRef = flowControlRef;
        //4.刷新缓存
        this.refreshCache();
    }
    //
    /**刷新缓存*/
    public void refreshCache() {
        /*在并发情况下,newAddress和invalidAddress可能正在执行,因此要锁住poolLock*/
        synchronized (this.poolLock) {
            Set<String> keySet = this.addressPool.keySet();
            for (String bucketKey : keySet) {
                this.addressPool.get(bucketKey).refreshAddress();//刷新地址计算结果
            }
            this.rulerCache.reset();
        }
    }
    //
    /**轮转获取地址(如果{@link #refreshFlowControl(String)}或{@link #refreshCache()}处在执行期,则该方法会被挂起等待操作完毕.)*/
    public InterAddress nextAddress(RsfBindInfo<?> info, String methodSign, Object[] args) {
        String serviceID = info.getBindID();
        //
        /*并发下不需要保证瞬时的一致性,只要保证最终一致性就好.*/
        AddressBucket bucket = addressPool.get(serviceID);
        if (bucket == null) {
            return null;
        }
        //
        List<InterAddress> addresses = this.rulerCache.getAddressList(info, methodSign, args);
        InterAddress doCallAddress = null;
        //
        doCallAddress = this.flowControlRef.randomFlowControl.getServiceAddress(addresses);
        while (true) {
            boolean check = this.flowControlRef.speedFlowControl.callCheck(info, methodSign, doCallAddress);//QoS
            if (check) {
                break;
            }
        }
        //
        return doCallAddress;
    }
    //
    @Override
    public String toString() {
        return "AddressPool[" + this.unitName + "]";
    }
}
class FlowControlRef {
    public NetworkFlowControl networkFlowControl = null; //网络规则
    public UnitFlowControl    unitFlowControl    = null; //单元规则
    public RandomFlowControl  randomFlowControl  = null; //地址选取规则
    public SpeedFlowControl   speedFlowControl   = null; //QoS速率规则
    //
    private FlowControlRef() {}
    //
    public static final FlowControlRef defaultRef(RsfSettings rsfSettings) {
        FlowControlRef flowControlRef = new FlowControlRef();
        flowControlRef.randomFlowControl = new RandomFlowControl();
        flowControlRef.speedFlowControl = SpeedFlowControl.defaultControl(rsfSettings);
        return flowControlRef;
    }
}