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
package net.hasor.rsf.register;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfFilter;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
abstract class InnerRsfBinderBuilder implements RsfBinder {
    public abstract AbstractRegisterCenter getRegisterCenter();
    //
    public void addRsfFilter(RsfFilter instance) {
        getRegisterCenter().addRsfFilter(new InstanceProvider<RsfFilter>(instance));
    }
    public void addRsfFilter(Provider<RsfFilter> provider) {
        getRegisterCenter().addRsfFilter(provider);
    }
    public <T> LinkedBuilder<T> bindService(Class<T> type) {
        return new MetaDataBuilder<T>(type, getRegisterCenter());
    }
    public <T> NamedBuilder<T> bindService(Class<T> type, T instance) {
        return this.bindService(type).toInstance(instance);
    }
    public <T> NamedBuilder<T> bindService(Class<T> type, Class<? extends T> implementation) {
        return this.bindService(type).to(implementation);
    }
    public <T> NamedBuilder<T> bindService(Class<T> type, Provider<T> provider) {
        return this.bindService(type).toProvider(provider);
    }
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type) {
        return this.bindService(type).nameWith(withName);
    }
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type, T instance) {
        return this.bindService(type).toInstance(instance).nameWith(withName);
    }
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type, Class<? extends T> implementation) {
        return this.bindService(type).to(implementation).nameWith(withName);
    }
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type, Provider<T> provider) {
        return this.bindService(type).toProvider(provider).nameWith(withName);
    }
    //
    private class MetaDataBuilder<T> implements LinkedBuilder<T> {
        private ServiceMetaData           serviceMetaData = null;
        private List<Provider<RsfFilter>> rsfFilterList   = null;
        private Provider<T>               rsfProvider     = null;
        private AbstractRegisterCenter    registerCenter  = null;
        //
        private MetaDataBuilder(Class<T> serviceType, AbstractRegisterCenter registerCenter) {
            this.registerCenter = registerCenter;
            Settings settings = registerCenter.getSettings();
            this.rsfFilterList = new ArrayList<Provider<RsfFilter>>();
            //
            this.serviceMetaData = new ServiceMetaData(null, serviceType);
            this.serviceMetaData.setServiceName(serviceType.getName());
            this.serviceMetaData.setServiceGroup(settings.getString("hasor.rsfConfig.defaultServiceValue.group", "RSF"));
            this.serviceMetaData.setServiceVersion(settings.getString("hasor.rsfConfig.defaultServiceValue.version", "1.0.0"));
            this.serviceMetaData.setClientTimeout(settings.getInteger("hasor.rsfConfig.defaultServiceValue.timeout", RSFConstants.ClientTimeout));
            this.serviceMetaData.setSerializeType(settings.getString("hasor.rsfConfig.serializeType.default", "Hessian"));
            //this.serviceMetaData.setServiceDesc(serviceDesc);
            this.to(serviceType);
        }
        public ConfigurationBuilder<T> nameWith(String name) {
            this.serviceMetaData.setServiceName(name);
            return this;
        }
        public ConfigurationBuilder<T> groupWith(String group) {
            this.serviceMetaData.setServiceGroup(group);
            return this;
        }
        public ConfigurationBuilder<T> versionWith(String version) {
            this.serviceMetaData.setServiceVersion(version);
            return this;
        }
        public ConfigurationBuilder<T> timeout(int clientTimeout) {
            this.serviceMetaData.setClientTimeout(clientTimeout);
            return this;
        }
        public ConfigurationBuilder<T> serialize(String serializeType) {
            this.serviceMetaData.setSerializeType(serializeType);
            return this;
        }
        public ConfigurationBuilder<T> addRsfFilter(RsfFilter instance) {
            return this.addRsfFilter(new InstanceProvider<RsfFilter>(instance));
        }
        public ConfigurationBuilder<T> addRsfFilter(Provider<RsfFilter> provider) {
            if (provider != null)
                this.rsfFilterList.add(provider);
            return this;
        }
        public NamedBuilder<T> to(final Class<? extends T> implementation) {
            return this.toProvider(new Provider<T>() {
                public T get() {
                    try {
                        return implementation.newInstance();
                    } catch (Exception e) {
                        throw new RsfException((short) 0, e);
                    }
                }
            });
        }
        public NamedBuilder<T> toInstance(T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        public NamedBuilder<T> toProvider(Provider<T> provider) {
            this.rsfProvider = provider;
            return this;
        }
        public ServiceMetaData getMetaData() {
            return this.serviceMetaData;
        }
        public void register() {
            Provider<RsfFilter>[] rsfFilterArray = this.rsfFilterList.toArray(new Provider[this.rsfFilterList.size()]);
            this.registerCenter.publishService(this.serviceMetaData, this.rsfProvider, rsfFilterArray);
        }
        public void unRegister() {
            this.registerCenter.recoverService(this.serviceMetaData);
        }
    }
}