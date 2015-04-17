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
package net.hasor.rsf.remoting.binder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.domain.ServiceDefine;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.more.FormatException;
import org.more.util.StringUtils;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBindBuilder implements RsfBinder {
    private final AbstractRsfContext rsfContext;
    private final List<URL>          parentAddressList;
    //
    protected RsfBindBuilder(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.parentAddressList = new ArrayList<URL>();
    }
    protected AbstractRsfContext getContext() {
        return this.rsfContext;
    }
    public void bindFilter(String filterID, RsfFilter instance) {
        getContext().getBindCenter().bindFilter(filterID, new InstanceProvider<RsfFilter>(instance));
    }
    public void bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
        getContext().getBindCenter().bindFilter(filterID, provider);
    }
    public void bindAddress(String hostIP, int hostPort) throws MalformedURLException {
        this.parentAddressList.add(URLUtils.toURL(hostIP, hostPort));
        return;
    }
    public <T> LinkedBuilder<T> rsfService(Class<T> type) {
        return new LinkedBuilderImpl<T>(type, getContext());
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
    public class LinkedBuilderImpl<T> implements LinkedBuilder<T> {
        private String                           serviceName;    //服务名
        private String                           serviceGroup;   //服务分组
        private String                           serviceVersion; //服务版本
        private int                              clientTimeout;  //调用超时（毫秒）
        private String                           serializeType;  //传输序列化类型
        private Class<T>                         serviceType;    //服务接口类型
        private Map<String, Provider<RsfFilter>> meFilterMap;
        private Provider<T>                      rsfProvider;
        private List<URL>                        hostAddressList;
        private AbstractRsfContext               rsfContext;
        //
        protected LinkedBuilderImpl(Class<T> serviceType, AbstractRsfContext rsfContext) {
            RsfSettings settings = rsfContext.getSettings();
            this.rsfContext = rsfContext;
            //
            this.serviceName = serviceType.getName();
            this.serviceGroup = settings.getDefaultGroup();
            this.serviceVersion = settings.getDefaultVersion();
            this.clientTimeout = settings.getDefaultTimeout();
            this.serializeType = settings.getDefaultSerializeType();
            this.serviceType = serviceType;
            this.meFilterMap = new LinkedHashMap<String, Provider<RsfFilter>>();
            this.hostAddressList = new ArrayList<URL>(parentAddressList);
            //覆盖
            RsfService serviceInfo = serviceType.getAnnotation(RsfService.class);
            if (serviceInfo != null) {
                if (StringUtils.isBlank(serviceInfo.group()) == false)
                    this.serviceGroup = serviceInfo.group();
                if (StringUtils.isBlank(serviceInfo.name()) == false)
                    this.serviceName = serviceInfo.name();
                if (StringUtils.isBlank(serviceInfo.version()) == false)
                    this.serviceVersion = serviceInfo.version();
                if (StringUtils.isBlank(serviceInfo.serializeType()) == false)
                    this.serializeType = serviceInfo.serializeType();
                if (serviceInfo.clientTimeout() > 0)
                    this.clientTimeout = serviceInfo.clientTimeout();
            }
        }
        public ConfigurationBuilder<T> ngv(String group, String name, String version) {
            Hasor.assertIsNotNull(group, "group is null.");
            Hasor.assertIsNotNull(name, "name is null.");
            Hasor.assertIsNotNull(version, "version is null.");
            if (group.contains("/") == true)
                throw new FormatException(name + " contain '/'");
            if (name.contains("/") == true)
                throw new FormatException(name + " contain '/'");
            if (version.contains("/") == true)
                throw new FormatException(name + " contain '/'");
            //
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
        public ConfigurationBuilder<T> bindFilter(String filterID, RsfFilter instance) {
            this.meFilterMap.put(filterID, new InstanceProvider<RsfFilter>(instance));
            return this;
        }
        public ConfigurationBuilder<T> bindFilter(String filterID, Provider<? extends RsfFilter> provider) {
            if (provider != null) {
                this.meFilterMap.put(filterID, (Provider<RsfFilter>) provider);
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
            ServiceDomain<T> domain = new ServiceDomain<T>(this.serviceType);
            domain.setBindName(this.serviceName);
            domain.setBindGroup(this.serviceGroup);
            domain.setBindVersion(this.serviceVersion);
            domain.setClientTimeout(this.clientTimeout);
            domain.setSerializeType(this.serializeType);
            //
            ServiceDefine<T> define = new ServiceDefine<T>(domain, this.rsfContext);
            //
            this.rsfContext.getBindCenter().publishService(define);
            this.rsfContext.getAddressCenter().updateAddress(define, this.hostAddressList);;
            return define;
        }
        public RegisterBuilder<T> bindAddress(String hostIP, int hostPort) throws MalformedURLException {
            this.hostAddressList.add(URLUtils.toURL(hostIP, hostPort));
            return this;
        }
    }
}