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
import net.hasor.rsf.RsfBindInfo;
/**
 * 路由计算结果缓存
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
class AddressCacheResult {
    private final AddressPool addressPool;
    public AddressCacheResult(AddressPool addressPool) {
        this.addressPool = addressPool;
    }
    //
    /**从全部地址中计算执行动态计算并缓存计算结果.*/
    public List<InterAddress> getAddressList(RsfBindInfo<?> info, String methodSign, Object[] args) {
        //        addressPool.
        //        
        //        bucket.getAvailableAddresses();
        //        bucket.getAvailableAddresses();
        return null;
    }
    /**重置缓存结果*/
    public void reset() {
        
        
        
        
        
        return;
    }
}