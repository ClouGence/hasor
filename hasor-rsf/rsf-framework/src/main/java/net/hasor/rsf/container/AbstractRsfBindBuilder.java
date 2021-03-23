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
package net.hasor.rsf.container;
import net.hasor.core.BindInfo;
import net.hasor.rsf.*;
import net.hasor.rsf.address.RouteTypeEnum;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.utils.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
abstract class AbstractRsfBindBuilder implements RsfPublisher {
    protected abstract <T> Supplier<? extends T> toProvider(BindInfo<T> bindInfo);

    protected abstract <T> Supplier<? extends T> toProvider(Class<T> bindInfo);

    protected abstract <T> RsfBindInfo<T> addService(ServiceDefine<T> serviceDefine);

    protected abstract void addShareFilter(FilterDefine filterDefine);

    public RsfPublisher bindFilter(String filterID, RsfFilter instance) {
        return this.bindFilter(filterID, () -> instance);
    }

    public RsfPublisher bindFilter(String filterID, BindInfo<RsfFilter> filterBindInfo) {
        return this.bindFilter(filterID, toProvider(filterBindInfo));
    }

    public RsfPublisher bindFilter(String filterID, Class<? extends RsfFilter> rsfFilterType) {
        return this.bindFilter(filterID, toProvider(rsfFilterType));
    }

    public RsfPublisher bindFilter(String filterID, Supplier<? extends RsfFilter> provider) {
        this.addShareFilter(new FilterDefine(filterID, provider));
        return this;
    }

    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new LinkedBuilderImpl<T>(type);
    }

    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, T instance) {
        return this.rsfService(type).toInstance(instance);
    }

    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation) {
        return this.rsfService(type).to(implementation);
    }

    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, Supplier<T> provider) {
        return this.rsfService(type).toProvider(provider);
    }

    public <T> ConfigurationBuilder<T> rsfService(Class<T> type, BindInfo<T> bindInfo) {
        return this.rsfService(type).toInfo(bindInfo);
    }

    private class LinkedBuilderImpl<T> implements LinkedBuilder<T> {
        private final ServiceDefine<T> serviceDefine;

        protected LinkedBuilderImpl(Class<T> serviceType) {
            this.serviceDefine = new ServiceDefine<T>(serviceType);
            RsfSettings settings = getEnvironment().getSettings();
            //
            RsfService serviceInfo = new AnnoRsfServiceValue(settings, serviceType);
            ServiceDomain<T> domain = this.serviceDefine.getDomain();
            domain.setServiceType(RsfServiceType.Consumer);
            domain.setBindGroup(serviceInfo.group());
            domain.setBindName(serviceInfo.name());
            domain.setBindVersion(serviceInfo.version());
            domain.setSerializeType(serviceInfo.serializeType());
            domain.setClientTimeout(serviceInfo.clientTimeout());
        }

        @Override
        public ConfigurationBuilder<T> group(String group) {
            Objects.requireNonNull(group, "group is null.");
            if (group.contains("/")) {
                throw new IllegalStateException(group + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindGroup(group);
            return this;
        }

        @Override
        public ConfigurationBuilder<T> name(String name) {
            Objects.requireNonNull(name, "name is null.");
            if (name.contains("/")) {
                throw new IllegalStateException(name + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindName(name);
            return this;
        }

        //
        @Override
        public ConfigurationBuilder<T> aliasName(String aliasType, String aliasName) {
            aliasType = Objects.requireNonNull(aliasType, "aliasType is null.");
            aliasName = Objects.requireNonNull(aliasName, "aliasName is null.");
            this.serviceDefine.getDomain().putAliasName(aliasType, aliasName);
            return this;
        }

        @Override
        public ConfigurationBuilder<T> version(String version) {
            Objects.requireNonNull(version, "version is null.");
            if (version.contains("/")) {
                throw new IllegalStateException(version + " contain '/'");
            }
            this.serviceDefine.getDomain().setBindVersion(version);
            return this;
        }

        @Override
        public ConfigurationBuilder<T> timeout(int clientTimeout) {
            if (clientTimeout < 1) {
                throw new IllegalStateException("clientTimeout must be greater than 0");
            }
            this.serviceDefine.getDomain().setClientTimeout(clientTimeout);
            return this;
        }

        @Override
        public ConfigurationBuilder<T> serialize(String serializeType) {
            Objects.requireNonNull(serializeType, "serializeType is null.");
            if (serializeType.contains("/")) {
                throw new IllegalStateException(serializeType + " contain '/'");
            }
            this.serviceDefine.getDomain().setSerializeType(serializeType);
            return this;
        }

        @Override
        public ConfigurationBuilder<T> protocol(String protocol, String... protocolArrays) {
            if (StringUtils.isNotBlank(protocol)) {
                this.serviceDefine.getDomain().addBindProtocol(protocol);
            }
            for (String prot : protocolArrays) {
                if (StringUtils.isNotBlank(prot)) {
                    this.serviceDefine.getDomain().addBindProtocol(prot);
                }
            }
            return this;
        }

        public ConfigurationBuilder<T> bindFilter(String filterID, RsfFilter instance) {
            Objects.requireNonNull(instance);
            this.serviceDefine.addRsfFilter(new FilterDefine(filterID, () -> instance));
            return this;
        }

        public ConfigurationBuilder<T> bindFilter(String filterID, Supplier<? extends RsfFilter> provider) {
            this.serviceDefine.addRsfFilter(new FilterDefine(filterID, Objects.requireNonNull(provider)));
            return this;
        }

        @Override
        public FilterBindBuilder<T> bindFilter(String filterID, Class<? extends RsfFilter> rsfFilterType) {
            Supplier<? extends RsfFilter> provider = AbstractRsfBindBuilder.this.toProvider(rsfFilterType);
            this.serviceDefine.addRsfFilter(new FilterDefine(filterID, provider));
            return this;
        }

        @Override
        public FilterBindBuilder<T> bindFilter(String filterID, BindInfo<RsfFilter> rsfFilterInfo) {
            Supplier<? extends RsfFilter> provider = AbstractRsfBindBuilder.this.toProvider(rsfFilterInfo);
            this.serviceDefine.addRsfFilter(new FilterDefine(filterID, provider));
            return this;
        }

        @Override
        public ConfigurationBuilder<T> to(final Class<? extends T> implementation) {
            return this.toProvider(AbstractRsfBindBuilder.this.toProvider(implementation));
        }

        @Override
        public ConfigurationBuilder<T> toInfo(final BindInfo<? extends T> bindInfo) {
            return this.toProvider(AbstractRsfBindBuilder.this.toProvider(bindInfo));
        }

        @Override
        public ConfigurationBuilder<T> toInstance(T instance) {
            return this.toProvider(() -> instance);
        }

        @Override
        public ConfigurationBuilder<T> toProvider(Supplier<? extends T> provider) {
            this.serviceDefine.getDomain().setServiceType(RsfServiceType.Provider);
            this.serviceDefine.setCustomerProvider(provider);
            return this;
        }

        @Override
        public RegisterBuilder<T> bindAddress(String rsfHost, int port) throws UnknownHostException {
            String unitName = getEnvironment().getSettings().getUnitName();
            return this.bindAddress(new InterAddress(rsfHost, port, unitName));
        }

        @Override
        public RegisterBuilder<T> bindAddress(String rsfURI, String... array) throws URISyntaxException, UnknownHostException {
            if (!StringUtils.isBlank(rsfURI)) {
                this.bindAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (String bindItem : array) {
                    this.bindAddress(new InterAddress(bindItem));
                }
            }
            return this;
        }

        @Override
        public RegisterBuilder<T> bindAddress(URI rsfURI, URI... array) throws UnknownHostException {
            if (rsfURI != null && InterAddress.checkFormat(rsfURI)) {
                this.bindAddress(new InterAddress(rsfURI));
            }
            if (array.length > 0) {
                for (URI bindItem : array) {
                    if (rsfURI != null && InterAddress.checkFormat(bindItem)) {
                        this.bindAddress(new InterAddress(bindItem));
                    }
                    throw new IllegalStateException(bindItem + " check fail.");
                }
            }
            return this;
        }

        public RegisterBuilder<T> bindAddress(InterAddress rsfAddress, InterAddress... array) {
            if (rsfAddress != null) {
                this.serviceDefine.addAddress(rsfAddress);
            }
            if (array.length > 0) {
                for (InterAddress bindItem : array) {
                    if (bindItem == null)
                        continue;
                    this.serviceDefine.addAddress(bindItem);
                }
            }
            return this;
        }

        @Override
        public RegisterBuilder<T> asAloneThreadPool() {
            this.serviceDefine.getDomain().setSharedThreadPool(false);
            return this;
        }

        @Override
        public RegisterBuilder<T> asMessage() {
            this.serviceDefine.getDomain().setMessage(true);
            return this;
        }

        @Override
        public RegisterBuilder<T> asShadow() {
            this.serviceDefine.getDomain().setShadow(true);
            return this;
        }

        public RsfBindInfo<T> register() {
            return addService(this.serviceDefine);
        }

        @Override
        public RegisterBuilder updateFlowControl(String flowControl) {
            this.serviceDefine.setFlowControl(flowControl);
            return this;
        }

        @Override
        public RegisterBuilder updateArgsRoute(String scriptBody) {
            this.serviceDefine.setRouteScript(RouteTypeEnum.ArgsLevel, scriptBody);
            return this;
        }

        @Override
        public RegisterBuilder updateMethodRoute(String scriptBody) {
            this.serviceDefine.setRouteScript(RouteTypeEnum.MethodLevel, scriptBody);
            return this;
        }

        @Override
        public RegisterBuilder updateServiceRoute(String scriptBody) {
            this.serviceDefine.setRouteScript(RouteTypeEnum.ServiceLevel, scriptBody);
            return this;
        }
    }
}
