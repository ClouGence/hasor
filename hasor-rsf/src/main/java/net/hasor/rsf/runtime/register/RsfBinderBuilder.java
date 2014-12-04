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
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.metadata.ServiceMetaData.Mode;
import net.hasor.rsf.runtime.RsfBinder;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfSettings;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBinderBuilder implements RsfBinder {
    private AbstractRegisterCenter registerCenter = null;
    public RsfBinderBuilder(AbstractRegisterCenter registerCenter) {
        this.registerCenter = registerCenter;
    }
    protected AbstractRegisterCenter getRegisterCenter() {
        return this.registerCenter;
    };
    //
    public void bindFilter(RsfFilter instance) {
        getRegisterCenter().addRsfFilter(new InstanceProvider<RsfFilter>(instance));
    }
    public void bindFilter(Provider<RsfFilter> provider) {
        getRegisterCenter().addRsfFilter(provider);
    }
    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new LinkedBuilderImpl<T>(type, getRegisterCenter());
    }
    public <T> NamedBuilder<T> rsfService(Class<T> type, T instance) {
        return this.rsfService(type).toInstance(instance);
    }
    public <T> NamedBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
        return this.rsfService(type).to(implementation);
    }
    public <T> NamedBuilder<T> rsfService(Class<T> type, Provider<T> provider) {
        return this.rsfService(type).toProvider(provider);
    }
    //
    public static class LinkedBuilderImpl<T> implements LinkedBuilder<T> {
        protected String                    serviceName    = null;   //服务名
        protected String                    serviceGroup   = "RSF";  //服务分组
        protected String                    serviceVersion = "1.0.0"; //服务版本
        protected int                       clientTimeout  = 6000;   //调用超时（毫秒）
        protected String                    serializeType  = null;   //传输序列化类型
        //
        protected Class<T>                  serviceType    = null;   //服务接口类型
        protected List<Provider<RsfFilter>> rsfFilterList  = null;
        protected Provider<T>               rsfProvider    = null;
        protected AbstractRegisterCenter    registerCenter = null;
        //
        //
        protected LinkedBuilderImpl(Class<T> serviceType, AbstractRegisterCenter registerCenter) {
            this.registerCenter = registerCenter;
            RsfSettings settings = registerCenter.getSettings();
            this.rsfFilterList = new ArrayList<Provider<RsfFilter>>();
            this.serviceName = serviceType.getName();
            this.serviceGroup = settings.getDefaultGroup();
            this.serviceVersion = settings.getDefaultVersion();
            this.clientTimeout = settings.getDefaultTimeout();
            this.serializeType = settings.getDefaultSerializeType();
            //this.serviceMetaData.setServiceDesc(serviceDesc);
        }
        public ConfigurationBuilder<T> ngv(String name, String group, String version) {
            Hasor.assertIsNotNull(name, "name is null.");
            Hasor.assertIsNotNull(group, "group is null.");
            Hasor.assertIsNotNull(version, "version is null.");
            this.serviceName = name;
            this.serviceGroup = group;
            this.serviceVersion = version;
            return this;
        }
        public ConfigurationBuilder<T> timeout(int clientTimeout) {
            this.clientTimeout = clientTimeout;
            return this;
        }
        public ConfigurationBuilder<T> serialize(String serializeType) {
            Hasor.assertIsNotNull(serializeType, "serializeType is null.");
            this.serializeType = serializeType;
            return this;
        }
        public ConfigurationBuilder<T> bindFilter(RsfFilter instance) {
            return this.bindFilter(new InstanceProvider<RsfFilter>(instance));
        }
        public ConfigurationBuilder<T> bindFilter(Provider<RsfFilter> provider) {
            if (provider != null) {
                this.rsfFilterList.add(provider);
            }
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
        public RegisterReference<T> register() {
            Mode mode = (this.rsfProvider == null) ? Mode.Consumer : Mode.Provider;
            ServiceMetaData serviceMetaData = new ServiceMetaData(mode, this.serviceType);
            serviceMetaData.setServiceName(this.serviceName);
            serviceMetaData.setServiceGroup(this.serviceGroup);
            serviceMetaData.setServiceVersion(this.serviceVersion);
            serviceMetaData.setClientTimeout(this.clientTimeout);
            serviceMetaData.setSerializeType(this.serializeType);
            //
            Provider<RsfFilter>[] rsfFilterArray = this.rsfFilterList.toArray(new Provider[this.rsfFilterList.size()]);
            ServiceDefine<T> define = new ServiceDefine<T>(serviceMetaData, this.registerCenter, rsfFilterArray, this.rsfProvider);
            //
            this.registerCenter.publishService(define);
            return define;
        }
    }
}