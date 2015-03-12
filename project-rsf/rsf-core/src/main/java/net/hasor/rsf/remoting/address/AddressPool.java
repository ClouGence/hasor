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
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.hasor.core.EventListener;
import net.hasor.rsf.adapter.Address;
/**
 * 某一个服务的地址池，提供地址轮转
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressPool implements EventListener {
    private final Object        LOCK_OBJECT = new Object();
    private final List<Address> hostAddressList;
    private final Random        addressRandom;
    //
    public AddressPool() {
        this.addressRandom = new Random(System.currentTimeMillis());
        this.hostAddressList = new CopyOnWriteArrayList<Address>();
    }
    /**轮转获取地址*/
    public Address nextAddress() {
        synchronized (this.LOCK_OBJECT) {
            return this.hostAddressList.get(this.addressRandom.nextInt(this.hostAddressList.size()));
        }
    }
    /**追加地址集*/
    public void updateAddress(List<Address> hostAddress) {
        if (hostAddress == null || hostAddress.isEmpty())
            return;
        synchronized (this.LOCK_OBJECT) {
            //1.排除重复
            for (Address newInfo : hostAddress) {
                if (this.hostAddressList.contains(newInfo) == true)
                    continue;
                newInfo.addListener(this);
                this.hostAddressList.add(newInfo);
            }
        }
    }
    /**从地址列表中移除。有失效的连接要及时通知以减少nextAddress的压力。*/
    public void removeAddress(Address hostAddress) {
        synchronized (this.LOCK_OBJECT) {
            int index = this.hostAddressList.indexOf(hostAddress);
            if (index > -1) {
                Address add = this.hostAddressList.get(index);
                add.removeListener(this);
                this.hostAddressList.remove(index);
            }
        }
    }
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        if ("Invalid".equalsIgnoreCase(event) == false || params.length < 1)
            return;
        Address address = (Address) params[0];
        if (address.invalidCount() > 100 && address.isStatic() == false)
            this.removeAddress(address);
    }
}