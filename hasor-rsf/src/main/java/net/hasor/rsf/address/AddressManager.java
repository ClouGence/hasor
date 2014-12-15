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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.rsf.metadata.ServiceMetaData;
/**
 * 
 * @version : 2014年12月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressManager {
    /*Name -> Group -> Version --> AddressPool*/
    private Map<String, Map<String, Map<String, AddressPool>>> addressMap;
    public AddressManager() {
        this.addressMap = new ConcurrentHashMap<String, Map<String, Map<String, AddressPool>>>();
    }
    //
    public AddressInfo findAddress(ServiceMetaData<?> metaData) {
        ServiceDefine<?> define = this.rsfDefineMap.get(metaData);
        if (define == null)
            return null;
        return this.addressManager.nextAddress(define.getBindName(), define.getBindGroup(), define.getBindVersion());
    }
    /**更新services服务提供地址。*/
    public void updateAddress(ServiceMetaData<?> metaData, List<AddressInfo> addressInfo) {
        this.addressManager.updateAddress(metaData.getServiceName(), metaData.getServiceGroup(), metaData.getServiceVersion(), addressInfo);
    }
    public void invalidAddress(AddressInfo address) {
        // TODO Auto-generated method stub
    }
}