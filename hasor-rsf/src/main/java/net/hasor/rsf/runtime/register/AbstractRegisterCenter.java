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
package net.hasor.rsf.runtime.register;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RegisterCenter;
import net.hasor.rsf.runtime.RsfBinder;
import net.hasor.rsf.runtime.RsfFilter;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRegisterCenter implements RegisterCenter {
    public RsfBinder getRsfBinder() {
        return new RsfBinderBuilder(this);
    }
    /**获取RSF配置。*/
    public abstract Settings getSettings();
    /**回收已经发布的服务*/
    public abstract void recoverService(ServiceMetaData serviceMetaData);
    /**发布服务*/
    public abstract void publishService(ServiceMetaData serviceMetaData, Provider<?> provider, Provider<RsfFilter>[] rsfFilter);
    /**添加全局Filter*/
    public abstract void addRsfFilter(Provider<RsfFilter> provider);
    //
    public ServiceMetaData getService(String serviceName) {
        // TODO Auto-generated method stub
        return null;
    }
    public RsfFilter[] getRsfFilters(ServiceMetaData metaData) {
        // TODO Auto-generated method stub
        return null;
    }
    public String[] getServiceNames() {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getBean(ServiceMetaData metaData) {
        // TODO Auto-generated method stub
        return null;
    }
}