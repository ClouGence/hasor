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
package net.hasor.rsf.binder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.address.InterServiceAddress;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.domain.FilterDefine;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.more.FormatException;
import org.more.RepeateException;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBindBuilder implements RsfBinder {
    private final AbstractRsfContext      rsfContext;
    private final ArrayList<FilterDefine> filterList;
    private final Set<String>             filterIDs;
    private final Object                  filterLock;
    //
    protected RsfBindBuilder(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.filterList = new ArrayList<FilterDefine>();
        this.filterIDs = new HashSet<String>();
        this.filterLock = new Object();
    }
    protected AbstractRsfContext getContext() {
        return this.rsfContext;
    }
    //
    public void bindFilter(String filterID, RsfFilter instance) {
        this.bindFilter(filterID, new InstanceProvider<RsfFilter>(Hasor.assertIsNotNull(instance)));
    }
    //
    public void bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
        synchronized (this.filterLock) {
            if (this.filterIDs.contains(filterID)) {
                throw new RepeateException("repeate filterID :" + filterID);
            }
            this.filterList.add(new FilterDefine(filterID, provider));
        }
    }
    //
    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new LinkedBuilderImpl<T>(type);
    }
    //
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, T instance) {
        return this.rsfService(type).toInstance(instance);
    }
    //
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
        return this.rsfService(type).to(implementation);
    }
    //
    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Provider<T> provider) {
        return this.rsfService(type).toProvider(provider);
    }
    //
    //
    //
    public class LinkedBuilderImpl<T> implements LinkedBuilder<T> {
        private final BindServiceDefine<T> serviceDefine;
        private final Set<URI>             hostAddressSet;
        //
        protected LinkedBuilderImpl(Class<T> serviceType) {
            this.serviceDefine = new BindServiceDefine<T>(serviceType, getContext());
            RsfSettings settings = rsfContext.getSettings();
            //
            RsfService serviceInfo = new AnnoRsfServiceValue(settings, serviceType);
            ServiceDomain<T> domain = this.serviceDefine.getDomain();
            domain.setBindGroup(serviceInfo.group());
            domain.setBindName(serviceInfo.name());
            domain.setBindVersion(serviceInfo.version());
            domain.setSerializeType(serviceInfo.serializeType());
            domain.setClientTimeout(serviceInfo.clientTimeout());
            //
            this.hostAddressSet = new HashSet<URI>();
        }
        //
        @Override
        public ConfigurationBuilder<T> group(String group) {
            Hasor.assertIsNotNull(group, "group is null.");
            if (group.contains("/") == true) {
                throw new FormatException(group + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindGroup(group);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> name(String name) {
            Hasor.assertIsNotNull(name, "name is null.");
            if (name.contains("/") == true) {
                throw new FormatException(name + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindName(name);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> version(String version) {
            Hasor.assertIsNotNull(version, "version is null.");
            if (version.contains("/") == true) {
                throw new FormatException(version + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindVersion(version);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> timeout(int clientTimeout) {
            if (clientTimeout < 1) {
                throw new FormatException("clientTimeout must be greater than 0");
            }
            this.serviceDefine.getDomain().setClientTimeout(clientTimeout);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> serialize(String serializeType) {
            Hasor.assertIsNotNull(serializeType, "serializeType is null.");
            if (serializeType.contains("/") == true) {
                throw new FormatException(serializeType + " contain '/'");
            }
            this.serviceDefine.getDomain().setSerializeType(serializeType);
            return this;
        }
        //
        public ConfigurationBuilder<T> bindFilter(String filterID, RsfFilter instance) {
            this.serviceDefine.addRsfFilter(filterID, instance);
            return this;
        }
        //
        public ConfigurationBuilder<T> bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
            this.serviceDefine.addRsfFilter(filterID, provider);
            return this;
        }
        //
        @Override
        public ConfigurationBuilder<T> to(final Class<? extends T> implementation) {
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
        //
        @Override
        public ConfigurationBuilder<T> toInstance(T instance) {
            return this.toProvider(new InstanceProvider<T>(instance));
        }
        //
        @Override
        public ConfigurationBuilder<T> toProvider(Provider<T> provider) {
            this.serviceDefine.setCustomerProvider(provider);
            return this;
        }
        //
        @Override
        public RegisterBuilder<T> bindAddress(String rsfURI) throws URISyntaxException {
            return this.bindAddress(new URI(rsfURI));
        }
        //
        @Override
        public RegisterBuilder<T> bindAddress(URI rsfURI) {
            if (InterServiceAddress.checkFormat(rsfURI) || InterAddress.checkFormat(rsfURI)) {
                this.hostAddressSet.add(rsfURI);
                return this;
            }
            throw new FormatException(rsfURI + " check fail.");
        }
        //
        public RegisterReference<T> register() {
            getContext().getBindCenter().publishService(this.serviceDefine);
            getContext().getAddressPool().newAddress(this.serviceDefine, this.hostAddressSet);
            return this.serviceDefine;
        }
    }
}