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
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.route.flowcontrol.network.NetworkFlowControl;
import net.hasor.rsf.address.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.address.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.address.route.rule.RuleParser;
import net.hasor.rsf.address.route.rule.RulerCacheResult;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
import org.more.util.io.input.ReaderInputStream;
/**
 * 地址池
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPool {
    private final ConcurrentMap<String, AddressBucket> addressPool;               //服务地址池Map.
    private final String                               unitName;                  //本机所处单元.
    private final Object                               poolLock;                  //addressPool的锁.
    //
    private final RulerCacheResult                     rulerCache;
    private TreeSet<String>                            flowControlSequence = null; //规则应用顺序.
    private RuleParser                                 ruleParser          = null;
    private NetworkFlowControl                         networkFlowControl  = null; //网络规则
    private UnitFlowControl                            unitFlowControl     = null; //单元规则
    private RandomFlowControl                          randomFlowControl   = null; //地址选取规则
    private SpeedFlowControl                           speedFlowControl    = null; //QoS速率规则
    //
    //
    public AddressPool(String unitName, RsfSettings rsfSettings) {
        this.addressPool = new ConcurrentHashMap<String, AddressBucket>();
        this.poolLock = new Object();
        this.unitName = unitName;
        this.rulerCache = null;
        this.ruleParser = new RuleParser(rsfSettings);
        this.flowControlSequence = new TreeSet<String>();
    }
    //
    /**获取本机所属单元*/
    public String getUnitName() {
        return this.unitName;
    }
    //
    /**用新的路由规则刷新地址池*/
    public void refreshFlowControl(String flowControl) throws IOException {
        if (StringUtils.isBlank(flowControl)) {
            LoggerHelper.logWarn("flowControl nothing.");
            return;
        }
        ReaderInputStream ris = new ReaderInputStream(new StringReader(flowControl));
        InputStreamSettings ruleSettings = new InputStreamSettings(ris);
        ruleSettings.loadSettings();
        //
        XmlNode[] flowControlArrays = ruleSettings.getXmlNodeArray("flowControl");
        if (flowControlArrays != null) {
            for (XmlNode control : flowControlArrays) {
                String controlType = control.getAttribute("type");
                if (StringUtils.isBlank(controlType)) {
                    LoggerHelper.logConfig("flowControl type Body :" + control.getXmlText());
                    continue;
                }
                /*  */if (StringUtils.equalsBlankIgnoreCase(NetworkFlowControl.TYPE, controlType)) {
                    /*网络规则*/
                    this.networkFlowControl = (NetworkFlowControl) this.ruleParser.ruleSettings(control.getXmlText());
                    LoggerHelper.logConfig("setup flowControl -> NetworkFlowControl.");
                } else if (StringUtils.equalsBlankIgnoreCase(UnitFlowControl.TYPE, controlType)) {
                    /*单元规则*/
                    this.unitFlowControl = (UnitFlowControl) this.ruleParser.ruleSettings(control.getXmlText());
                    LoggerHelper.logConfig("setup flowControl -> UnitFlowControl.");
                } else if (StringUtils.equalsBlankIgnoreCase(RandomFlowControl.TYPE, controlType)) {
                    /*选址规则*/
                    this.randomFlowControl = (RandomFlowControl) this.ruleParser.ruleSettings(control.getXmlText());
                    LoggerHelper.logConfig("setup flowControl -> RandomFlowControl.");
                } else if (StringUtils.equalsBlankIgnoreCase(SpeedFlowControl.TYPE, controlType)) {
                    /*速率规则*/
                    this.speedFlowControl = (SpeedFlowControl) this.ruleParser.ruleSettings(control.getXmlText());
                    LoggerHelper.logConfig("setup flowControl -> SpeedFlowControl.");
                }
                //
                this.flowControlSequence.add(controlType);
            }
        }
        //
        this.refreshCache();
    }
    //
    /**刷新缓存*/
    public void refreshCache() {
        // TODO Auto-generated method stub
    }
    //
    /**轮转获取地址(如果{@link #refreshFlowControl(String)}或{@link #refreshCache()}处在执行期,则该方法会被挂起等待操作完毕.)*/
    public InterAddress nextAddress(RsfBindInfo<?> info, Method doCallMethod, Object[] args) {
        String serviceID = info.getBindID();
        AddressBucket bucket = addressPool.get(serviceID);
        if (bucket == null) {
            LoggerHelper.logConfig("the service '%s' , did not define any provider.", info);
            return null;
        }
        //
        List<InterAddress> addresses = rulerCache.getAddressList(info, doCallMethod, args);
        InterAddress doCallAddress = this.randomFlowControl.getServiceAddress(addresses);
        while (true) {
            boolean check = this.speedFlowControl.callCheck(info, doCallMethod, doCallAddress);
            if (check) {
                break;
            }
        }
        //
        return doCallAddress;
    }
}