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
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * 提供地址轮转
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressPool {
    private final List<URL> addressList        = new ArrayList<URL>();
    private final Random    addressRandom;
    //
    private final List<URL> invalidAddresses   = new CopyOnWriteArrayList<URL>(); /*不可用地址*/
    private final List<URL> availableAddresses = new CopyOnWriteArrayList<URL>(); /*最终可用地址*/
    //
    public AddressPool() {
        this.addressRandom = new Random(System.currentTimeMillis());
    }
    //
    /**轮转获取地址*/
    public URL nextAddress() {
        if (this.availableAddresses.isEmpty() == true)
            return null;
        return this.availableAddresses.get(this.addressRandom.nextInt(this.availableAddresses.size()));
    }
    /**更新地址集*/
    public void updateAddress(List<URL> address) {
        // TODO Auto-generated method stub
    }
}