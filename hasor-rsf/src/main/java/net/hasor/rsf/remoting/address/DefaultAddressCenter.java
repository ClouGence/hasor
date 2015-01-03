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
package net.hasor.rsf.remoting.address;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.adapter.AbstracAddressCenter;
import net.hasor.rsf.adapter.Address;
/**
 * 地址管理中心，负责维护服务的远程服务提供者列表。
 * （线程安全）
 * @version : 2014年12月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultAddressCenter extends AbstracAddressCenter {
    private final Object                   lock;
    private final Map<String, AddressPool> addressMap; //维护服务和服务地址的映射
    private final List<Address>            addressPool; //维护所有服务地址
    public DefaultAddressCenter() {
        this.lock = new Object();
        this.addressMap = new ConcurrentHashMap<String, AddressPool>();
        this.addressPool = new CopyOnWriteArrayList<Address>();
    }
    //
    public Address findHostAddress(RsfBindInfo<?> bindInfo) {
        if (bindInfo == null)
            return null;
        synchronized (this.lock) {
            AddressPool pool = this.addressMap.get(bindInfo.getBindID());
            if (pool != null) {
                return pool.nextAddress();
            }
        }
        return null;
    }
    public void invalidAddress(Address refereeAddress) {
        if (refereeAddress == null)
            return;
        if (this.addressPool.contains(refereeAddress) == false)
            return;
        refereeAddress.setInvalid();
    }
    public void updateAddress(RsfBindInfo<?> bindInfo, List<URL> hostAddress) {
        if (bindInfo == null || hostAddress == null || hostAddress.isEmpty() == true)
            return;
        //
        List<Address> hostAddressList = new ArrayList<Address>();
        for (URL url : hostAddress) {
            hostAddressList.add(new AddressInfo(url));
        }
        //
        synchronized (this.lock) {
            AddressPool pool = this.addressMap.get(bindInfo.getBindID());
            if (pool == null) {
                pool = new AddressPool();
                this.addressMap.put(bindInfo.getBindID(), pool);
            }
            pool.updateAddress(hostAddressList);
            hostAddressList.removeAll(this.addressPool);
            this.addressPool.addAll(hostAddressList);
        }
    }
}