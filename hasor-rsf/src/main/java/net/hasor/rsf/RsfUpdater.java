/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.rsf;
import java.util.Collection;
import java.util.List;
/**
 * 服务配置更新器。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfUpdater {
    /**更新服务地址本计算规则（服务级）*/
    public boolean updateServiceRoute(String serviceID, String scriptBody);

    /**更新服务地址本计算规则（服务级）*/
    public String serviceRoute(String serviceID);

    /**更新本地方法级地址计算脚本。*/
    public boolean updateMethodRoute(String serviceID, String scriptBody);

    /**更新本地方法级地址计算脚本。*/
    public String methodRoute(String serviceID);

    /**更新本地参数级地址计算脚本。*/
    public boolean updateArgsRoute(String serviceID, String scriptBody);

    /**更新本地参数级地址计算脚本。*/
    public String argsRoute(String serviceID);

    /**更新服务路由策略*/
    public boolean updateFlowControl(String serviceID, String flowControl);

    /**更新服务路由策略*/
    public String flowControl(String serviceID);

    /**获取所有地址（包括本地的和无效的）。*/
    public List<InterAddress> queryAllAddresses(String serviceID);

    /**获取计算之后可用的地址。*/
    public List<InterAddress> queryAvailableAddresses(String serviceID);

    /**失效地址。*/
    public List<InterAddress> queryInvalidAddresses(String serviceID);

    /**获取计算之后同一单元地址。*/
    public List<InterAddress> queryLocalUnitAddresses(String serviceID);

    /**
     * 新增或追加更新服务地址信息。<p>
     * 如果追加的地址是已存在的失效地址，那么updateAddress方法将重新激活这些失效地址。
     * @param serviceID 服务ID。
     * @param newHostSet 追加更新的地址。
     */
    public void appendAddress(String serviceID, Collection<InterAddress> newHostSet);

    /**
     * 新增或追加更新服务静态本信息,静态服务地址是永久'有效'地址。<p>
     * 如果追加的地址是已存在的失效地址，那么updateAddress方法将重新激活这些失效地址。
     * @param serviceID 服务ID。
     * @param newHostSet 追加更新的地址。
     */
    public void appendStaticAddress(String serviceID, Collection<InterAddress> newHostSet);

    /**刷新服务的地址本，使其使用全新的地址本。*/
    public void refreshAddress(String serviceID, List<InterAddress> addressList);

    /**使用新的地址本替换已有的地址本。*/
    public void refreshAddressCache();

    /**
     * 将服务的地址设置成临时失效状态，把地址从服务的地址本中彻底删除。
     * @param serviceID 服务ID。
     * @param invalidAddress 将要删除的地址。
     */
    public void removeAddress(String serviceID, InterAddress invalidAddress);

    /**
     * 将服务的地址设置成临时失效状态，把地址从服务的地址本中彻底删除。
     * @param serviceID 服务ID。
     * @param invalidAddressSet 将要删除的地址。
     */
    public void removeAddress(String serviceID, Collection<InterAddress> invalidAddressSet);

    /**某一个地址不可用了，从所有服务中删除这个地址*/
    public void removeAddress(InterAddress address);
}