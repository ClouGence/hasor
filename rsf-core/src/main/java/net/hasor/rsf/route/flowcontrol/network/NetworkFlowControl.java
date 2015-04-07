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
package net.hasor.rsf.route.flowcontrol.network;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import net.hasor.core.Settings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.route.rule.AbstractRule;
import net.hasor.rsf.utils.NetworkUtils;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * 本地网络流量控制规则，用来控制本地网络调用。<p>
 * <pre>例：
 *  本机IP为192.168.1.125，子网掩码为255.255.255.0 =&gt; 网络地址为:192.168.1.0/24
 *      机房IP范围:192.168.1.1 ~ 192.168.1.254
 *  
 *  本机IP为172.128.100.3，子网掩码为255.255.240.0 =&gt; 网络地址为:172.168.96.0/20
 *      机房IP范围:172.168.96.1 ~ 172.168.111.254
 * 
 * 配置实例：
 * &lt;flowControl enable="true|false" type="network"&gt;
 *   &lt;threshold&gt;0.3&lt;/threshold&gt;
 *   &lt;exclusions&gt;172.23.*,172.19.*&lt;/exclusions&gt;
 * &lt;/flowControl&gt;
 * </pre>
 * 解释： 对某一服务，开启本机房优先调用策略
 * 但当本机房内的可用机器的数量占服务地址全部数量的比例小于0.3时，本机房优先调用策略失效，启用跨机房调用。
 * 该规则对以下网段的服务消费者不生效：172.23.*,172.19.*
 * 
 * <p>参考文献 : [无类别域间路由，Classless Inter-Domain Routing]
 * <p>需要JDK1.8以上,原因低版本JDK在获取子网掩码时存在缺陷.
 */
public class NetworkFlowControl extends AbstractRule {
    private float        threshold;
    private List<String> exclusions;
    //
    public void paserControl(Settings settings) {
        this.enable(settings.getBoolean("flowControl.enable"));
        this.threshold = settings.getFloat("flowControl.threshold");
        String exclusions = settings.getString("flowControl.exclusions");
        this.exclusions = Arrays.asList(exclusions.split(","));
        //
        String version = System.getProperty("java.version");
        if (StringUtils.isBlank(version) || version.matches("1\\.8\\..*") == false) {
            if (this.enable()) {
                this.enable(false);
                LoggerHelper.logWarn("please replace the JDK 1.8+");
            }
        }
    }
    public float getThreshold() {
        return this.threshold;
    }
    public List<String> getExclusions() {
        return this.exclusions;
    }
    //
    /**
     * 是否启用本地网络优先规则
     * @param allAmount 所有可用地址数量
     * @param localAmount 本地网络地址数量
     */
    public boolean isLocalNetwork(int allAmount, int localAmount) {
        if (localAmount == 0 || !this.enable()) {
            return false;
        }
        float value = (localAmount + 0.0F) / allAmount;
        if (value >= this.getThreshold()) {
            return true;
        }
        return false;
    }
    //
    /**筛选本机房地址(按照本机网卡所属网段划分)*/
    public List<InterAddress> siftNetworkAddress(List<InterAddress> address) {
        List<Integer> networkIDs = getNetworkIDs();
        if (networkIDs == null || networkIDs.isEmpty() || address == null || address.isEmpty())
            return null;
        //
        List<InterAddress> local = new ArrayList<InterAddress>();
        for (InterAddress inter : address) {
            for (Integer netID : networkIDs) {
                int ipData = inter.getHostAddressData();
                if (ipData == (ipData | netID)) {
                    local.add(inter);
                }
            }
        }
        return local;
    }
    /**获取本机所处网络的网络ID（包括多网卡,排除回环地址）*/
    private static List<Integer> getNetworkIDs() {
        try {
            List<Integer> list = new ArrayList<Integer>();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                List<InterfaceAddress> faceAddresses = ni.getInterfaceAddresses();
                if (faceAddresses == null) {
                    faceAddresses = Collections.EMPTY_LIST;
                }
                //
                for (InterfaceAddress faceAddress : faceAddresses) {
                    InetAddress address = faceAddress.getAddress();
                    if (address.isLoopbackAddress() == true || address.getHostAddress().contains(":")) {
                        continue;
                    }
                    //
                    byte[] ipBytes = address.getAddress();
                    int ipData = NetworkUtils.ipDataByBytes(ipBytes);
                    int ipMask = NetworkUtils.maskByPrefixLength(faceAddress.getNetworkPrefixLength());
                    int networkID = ipData & ipMask;
                    //
                    if (LoggerHelper.isEnableInfoLoggable()) {
                        String ipStr = StringUtils.rightPad(NetworkUtils.ipDataToString(ipData), 15);
                        String maskStr = StringUtils.rightPad(NetworkUtils.ipDataToString(ipMask), 15);
                        String netIDStr = StringUtils.rightPad(NetworkUtils.ipDataToString(networkID), 15);
                        LoggerHelper.logInfo("IP:%s  Mask:%s  NetID:%s", ipStr, maskStr, netIDStr);
                    }
                    //
                    list.add(networkID);
                }
            }
            if (list.isEmpty()) {
                LoggerHelper.logWarn("[RoomFlowControl] Can not get the server IP address.");
            }
            return list;
        } catch (Throwable t) {
            LoggerHelper.logWarn("[RoomFlowControl] Get the server IP address failed.", t);
            return null;
        }
    }
}