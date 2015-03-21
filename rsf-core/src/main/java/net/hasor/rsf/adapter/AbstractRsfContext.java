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
package net.hasor.rsf.adapter;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.serialize.SerializeFactory;
import org.more.util.StringUtils;
/**
 * 服务上下文，负责提供 RSF 运行环境的支持。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    //
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     * @return 服务对象
     */
    public <T> T getBean(RsfBindInfo<T> bindInfo) {
        //根据bindInfo 的 id 从 BindCenter 中心取得本地  RsfBindInfo
        //   （该操作的目的是为了排除传入参数的干扰，确保可以根据BindInfo id 取得本地的BindInfo。因为外部传入进来的RsfBindInfo极有可能是包装过后的）
        bindInfo = this.getBindCenter().getServiceByID(bindInfo.getBindID());
        if (bindInfo != null && bindInfo instanceof RsfBindDefine == true) {
            Provider<T> provider = ((RsfBindDefine<T>) bindInfo).getCustomerProvider();
            if (provider != null)
                return provider.get();
        }
        return null;
    }
    /**
     * 查找一个{@link RsfFilter}
     * @param filterID filter ID
     * @return 返回{@link RsfFilter}
     */
    public <T extends RsfFilter> T findFilter(String filterID) {
        return this.getBindCenter().findFilter(filterID);
    }
    /**
     * 获取服务上的{@link RsfFilter}
     * @param serviceID 服务ID
     * @param filterID filter ID
     * @return 返回{@link RsfFilter}
     */
    public <T extends RsfFilter> T findFilter(String serviceID, String filterID) {
        RsfBindInfo<?> bindInfo = this.getBindCenter().getServiceByID(serviceID);
        if (bindInfo != null && bindInfo instanceof RsfBindDefine == true) {
            RsfBindDefine<?> rsfDefine = (RsfBindDefine<?>) bindInfo;
            return (T) rsfDefine.getFilter(filterID);
        }
        return null;
    }
    /**
     * 查找一个{@link RsfFilter}<br>
     *  如果在Binder阶段注册的服务通过{@link RsfBinder}指定过Group、Name、Version任意一个值则该方法不确定会成功返回。
     * @param servicetType 服务类型
     * @param filterID filter ID
     * @return 返回{@link RsfFilter}
     */
    public <T extends RsfFilter> T findFilter(Class<?> servicetType, String filterID) {
        RsfSettings settings = getSettings();
        String serviceName = servicetType.getName();
        String serviceGroup = settings.getDefaultGroup();
        String serviceVersion = settings.getDefaultVersion();
        //覆盖
        RsfService serviceInfo = servicetType.getAnnotation(RsfService.class);
        if (serviceInfo != null) {
            if (StringUtils.isBlank(serviceInfo.group()) == false)
                serviceGroup = serviceInfo.group();
            if (StringUtils.isBlank(serviceInfo.name()) == false)
                serviceName = serviceInfo.name();
            if (StringUtils.isBlank(serviceInfo.version()) == false)
                serviceVersion = serviceInfo.version();
        }
        String serviceID = String.format("[%s]%s-%s", serviceGroup, serviceName, serviceVersion);
        return this.findFilter(serviceID, filterID);
    }
    /**
     * 获取服务上配置有效的过滤器
     * @param bindInfo 元信息所描述对象
     * @return 返回{@link RsfFilter}
     */
    public <T> Provider<RsfFilter>[] getFilters(RsfBindInfo<T> bindInfo) {
        //根据bindInfo 的 id 从 BindCenter 中心取得本地  RsfBindInfo
        //   （该操作的目的是为了排除传入参数的干扰，确保可以根据BindInfo id 取得本地的BindInfo。因为外部传入进来的RsfBindInfo极有可能是包装过后的）
        bindInfo = this.getBindCenter().getServiceByID(bindInfo.getBindID());
        if (bindInfo != null && bindInfo instanceof RsfBindDefine == true) {
            Provider<RsfFilter>[] pubFilters = this.getBindCenter().publicFilters();
            Provider<RsfFilter>[] subFilters = ((RsfBindDefine<T>) bindInfo).getFilterProvider();
            Provider<RsfFilter>[] mergeFilters = new Provider[pubFilters.length + subFilters.length];
            System.arraycopy(pubFilters, 0, mergeFilters, 0, pubFilters.length);
            System.arraycopy(subFilters, 0, mergeFilters, pubFilters.length, subFilters.length);
            //
            return mergeFilters;
        }
        return null;
    }
    //
    /**
     * 获取{@link Executor}用于安排执行任务。
     * @param serviceName 服务名
     * @return 返回Executor
     */
    public abstract Executor getCallExecute(String serviceName);
    /** @return 获取序列化管理器。*/
    public abstract SerializeFactory getSerializeFactory();
    /** @return 获取Netty事件处理工具*/
    public abstract EventLoopGroup getLoopGroup();
    /** @return 获取地址管理中心*/
    public abstract AbstracAddressCenter getAddressCenter();
    /** @return 获取服务注册中心*/
    public abstract AbstractBindCenter getBindCenter();
    /** @return 获取请求管理中心*/
    public abstract AbstractRequestManager getRequestManager();
}