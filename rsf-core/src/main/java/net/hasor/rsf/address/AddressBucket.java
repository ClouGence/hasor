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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * 服务地址管理，所有服务地址的进一步处理都需要通过地址路由处理。
 * 用于接收地址更新同时也用来计算有效和无效地址。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressBucket {
    //原始数据
    private final String                      serviceID;         //服务ID
    private final String                      unitName;          //服务所属单元
    private final List<AddressInfo>           allAddressList;    //所有备选地址
    private CopyOnWriteArrayList<AddressInfo> invalidAddresses;  //不可用地址（可能包含本机房及其它机房的地址）
    //
    //计算的可用地址
    private List<AddressInfo>                 localAddresses;    //本地单元地址
    private List<AddressInfo>                 availableAddresses; //所有可用地址（包括本地单元）
    //
    public AddressBucket(String serviceID, String unitName) {
        this.serviceID = serviceID;
        this.unitName = unitName;
        this.allAddressList = new ArrayList<AddressInfo>();
        this.localAddresses = new ArrayList<AddressInfo>();
        this.invalidAddresses = new CopyOnWriteArrayList<AddressInfo>();
        this.availableAddresses = new ArrayList<AddressInfo>();
    }
    //
    /**获取计算之后可用的地址。*/
    public synchronized List<AddressInfo> getAvailableAddresses() {
        return new ArrayList<AddressInfo>(availableAddresses);
    }
    /**失效地址。*/
    public synchronized List<AddressInfo> getInvalidAddresses() {
        return new ArrayList<AddressInfo>(invalidAddresses);
    }
    /**获取计算之后本地地址。*/
    public synchronized List<AddressInfo> getLocalAddresses() {
        return new ArrayList<AddressInfo>(localAddresses);
    }
    //
    /**新增地址支持动态新增*/
    public void newAddress(List<URL> hostAddress) {
        if (hostAddress == null || hostAddress.isEmpty()) {
            return;
        }
        //
        ArrayList<AddressInfo> newAddress = new ArrayList<AddressInfo>();
        for (URL newURLs : hostAddress) {
            for (AddressInfo hasAddress : this.allAddressList) {
                if (hasAddress.equals(newURLs) == true) {
                    continue;
                }
            }
            AddressInfo address = new AddressInfo(newURLs.getHost(), newURLs.getPort());
            newAddress.add(address);
        }
        //
        this.allAddressList.addAll(newAddress);
        this.refreshAvailableAddress();
    }
    //
    /**将地址置为失效的。*/
    public void invalidAddress(AddressInfo hostAddress) {
        if (this.invalidAddresses.addIfAbsent(hostAddress)) {
            synchronized (this) {
                refreshAvailableAddress();
            }
        }
    }
    //
    /**刷新地址*/
    private void refreshAvailableAddress() {
        //
        //从全部地址中筛选本地地址，同时剔除掉无效的地址。
        List<AddressInfo>   newLocalAddresses=new ArrayList<AddressInfo>();
        for (AddressInfo addressInfo :this.allAddressList){
            boolean doAdd=false;
            for (AddressInfo invalid :this.invalidAddresses){
                if (addressInfo.equals(invalid)){
                    doAdd=true;
                    break;
                }
            }
            if (doAdd){
                newLocalAddresses.add(addressInfo);
            }
        }
        
        
        //
        //从全部地址中剔除无效的地址。
        
        
        try {
            int index = activeHostAddressList.indexOf(hostAddress);
            if (index<0){
                return;
            }
            ArrayList<AddressInfo> activeSnapshot = new ArrayList<AddressInfo>(this.activeHostAddressList);
            for (AddressInfo active: activeSnapshot ){
                if (active.equals(hostAddress)){
                    
                }
                refreshAvailableAddress
                activeSnapshot.
                activeHostAddressList.remove(index);

            }
            
            
            //invalidHostAddressList
            int index = this.hostAddressList.indexOf(hostAddress);
            if (index > -1) {
                AddressInfo add = this.hostAddressList.get(index);
                add.removeListener(this);
                this.hostAddressList.remove(index);
            }
        } finally {
            lock.unlock();
        }
    }
    /** 从列表中去掉invalid address */
    private List<String> getAvailableAddresses(final List<String> addresses) {
        List<String> result = new ArrayList<String>();
        if (this.invalidAddresses.isEmpty()) {
            result.addAll(addresses);
            return result;
        }
        for (String address : addresses) {
            if (this.invalidAddresses.contains(address) == false) {
                result.add(address);
            }
        }
        return result;
    }
}