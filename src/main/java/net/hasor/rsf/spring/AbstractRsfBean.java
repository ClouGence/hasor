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
package net.hasor.rsf.spring;
import net.hasor.core.AppContext;
import net.hasor.plugins.spring.SpringModule;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfPublisher;
import org.more.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
/**
 * 包装来自 Spring 的 Bean。
 * @version : 2016-11-08
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractRsfBean implements FactoryBean, ApplicationContextAware {
    private String                 factoryID     = null;
    private String                 bindGroup     = null;
    private String                 bindName      = null;
    private String                 bindVersion   = null;
    private Class<?>               bindType      = null;
    private String                 bindDesc      = "";
    private int                    clientTimeout = 600000;
    private String                 serializeType = null;
    private boolean                onShadow      = false;
    private Map<String, RsfFilter> filters       = null;
    //
    private ApplicationContext springContext;
    private AppContext         hasorContext;
    private RsfClient          rsfClient;
    //
    @Override
    public Class getObjectType() {
        return this.getBindType();
    }
    @Override
    public boolean isSingleton() {
        return true;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }
    public String getFactoryID() {
        return factoryID;
    }
    public void setFactoryID(String factoryID) {
        this.factoryID = factoryID;
    }
    public String getBindGroup() {
        return this.bindGroup;
    }
    public void setBindGroup(String bindGroup) {
        this.bindGroup = bindGroup;
    }
    public String getBindName() {
        return this.bindName;
    }
    public void setBindName(String bindName) {
        this.bindName = bindName;
    }
    public String getBindVersion() {
        return this.bindVersion;
    }
    public void setBindVersion(String bindVersion) {
        this.bindVersion = bindVersion;
    }
    public Class<?> getBindType() {
        return this.bindType;
    }
    public void setBindType(Class<?> bindType) {
        this.bindType = bindType;
    }
    public String getBindDesc() {
        return this.bindDesc;
    }
    public void setBindDesc(String bindDesc) {
        this.bindDesc = bindDesc;
    }
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    public String getSerializeType() {
        return this.serializeType;
    }
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    public boolean isOnShadow() {
        return this.onShadow;
    }
    public void setOnShadow(boolean onShadow) {
        this.onShadow = onShadow;
    }
    public Map<String, RsfFilter> getFilters() {
        return this.filters;
    }
    public void setFilters(Map<String, RsfFilter> filters) {
        this.filters = filters;
    }
    //
    protected ApplicationContext getSpringContext() {
        return this.springContext;
    }
    protected AppContext getHasorContext() {
        return this.hasorContext;
    }
    protected RsfClient getRsfClient() {
        return this.rsfClient;
    }
    //
    public void init() throws Exception {
        if (StringUtils.isBlank(this.getFactoryID())) {
            this.setFactoryID(SpringModule.DefaultHasorBeanName);
        }
        //
        String factoryID = this.getFactoryID();
        this.hasorContext = (AppContext) this.springContext.getBean(factoryID);
        if (this.hasorContext == null) {
            throw new NullPointerException("AppContext is null. of beanID '" + factoryID + "'");
        }
        RsfContext rsfContext = this.hasorContext.getInstance(RsfContext.class);
        if (rsfContext == null) {
            throw new NullPointerException("RsfContext is null.");
        }
        //
        // .注册服务
        RsfPublisher publisher = rsfContext.publisher();
        RsfPublisher.FilterBindBuilder<?> configBuilder = publisher.rsfService(this.getBindType())//
                .group(this.getBindGroup())//
                .name(this.getBindName())//
                .version(this.getBindVersion())//
                .timeout(this.getClientTimeout())//
                .serialize(this.getSerializeType());//
        // .服务过滤器
        Map<String, RsfFilter> filters = this.getFilters();
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, RsfFilter> rsfFilter : filters.entrySet()) {
                String key = rsfFilter.getKey();
                RsfFilter filter = rsfFilter.getValue();
                configBuilder = configBuilder.bindFilter(key, filter);
            }
        }
        //
        RsfPublisher.RegisterBuilder<?> builder = configBuilder;
        if (this.isOnShadow()) {
            builder = builder.asShadow();
        }
        //
        this.rsfClient = rsfContext.getRsfClient();
        RsfPublisher.RegisterBuilder<?> registerBuilder = this.registerService(builder);
        registerBuilder.register();
    }
    /** 注册服务 */
    protected abstract RsfPublisher.RegisterBuilder<?> registerService(RsfPublisher.RegisterBuilder<?> builder);
}