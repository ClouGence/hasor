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
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.route.flowcontrol.network.NetworkFlowControl;
import net.hasor.rsf.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.route.flowcontrol.unit.UnitFlowControl;
import org.more.logger.LoggerHelper;
/**
 * 地址池
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPool {
    private final ConcurrentMap<String, AddressBucket> addressPool = new ConcurrentHashMap<String, AddressBucket>();
    private final Object                               poolLock    = new Object();
    private String                                     unitName;
    //
    private NetworkFlowControl                         networkFlowControl;
    private UnitFlowControl                            unitFlowControl;
    private RandomFlowControl                          randomFlowControl;
    private SpeedFlowControl                           speedFlowControl;
    //
    //
    /**轮转获取地址*/
    public InterAddress nextAddress(RsfBindInfo<?> info, Method doCallMethod) {
        String serviceID = info.getBindID();
        AddressBucket bucket = addressPool.get(serviceID);
        if (bucket == null) {
            LoggerHelper.logConfig("the service '%s' , did not define any provider.", info);
            return null;
        }
        //
        bucket.getLocalUnitAddresses();
        bucket.getLocalNetAddresses();
        bucket.getAvailableAddresses();
        return this.activeHostAddressList.get(this.addressRandom.nextInt(this.activeHostAddressList.size()));
    }
    //      this.addressRandom = new Random(System.currentTimeMillis());
    //
    //
    //
}