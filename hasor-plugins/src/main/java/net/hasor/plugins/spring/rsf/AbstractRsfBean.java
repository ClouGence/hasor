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
package net.hasor.plugins.spring.rsf;
import net.hasor.core.AppContext;
import net.hasor.core.Provider;
import net.hasor.plugins.spring.SpringModule;
import net.hasor.rsf.*;
import net.hasor.rsf.utils.StringUtils;
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
    private String                 bindDesc      = null;
    private int                    clientTimeout = 0;
    private String                 serializeType = null;
    private boolean                onShadow      = false;
    private Map<String, RsfFilter> filters       = null;
    //
    private ApplicationContext springContext;
    private RsfContext         rsfContext;
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
    protected ApplicationContext getSpring() {
        return this.springContext;
    }
    protected RsfContext getRsfContext() {
        return this.rsfContext;
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
        AppContext hasorContext = (AppContext) this.springContext.getBean(factoryID);
        if (hasorContext == null) {
            throw new NullPointerException("AppContext is null. of beanID '" + factoryID + "'");
        }
        this.rsfContext = hasorContext.getInstance(RsfContext.class);
        if (this.rsfContext == null) {
            throw new NullPointerException("RsfContext is null.");
        }
        //
        if (this.getBindType() == null) {
            throw new NullPointerException("bindType is null.");
        }
        //
        // .初始化默认数据
        loadDefaultValues();
        //
        // .服务类型
        RsfPublisher publisher = rsfContext.publisher();
        RsfPublisher.LinkedBuilder<?> likeBuilder = publisher.rsfService(this.getBindType());
        //
        // .提供者
        RsfPublisher.ConfigurationBuilder<?> configBuilder = likeBuilder;
        if (this instanceof Provider) {
            configBuilder = likeBuilder.toProvider((Provider) this);
        }
        //
        // .服务信息
        configBuilder = configBuilder.group(this.getBindGroup()).name(this.getBindName()).version(this.getBindVersion())//gnv 信息
                .timeout(this.getClientTimeout()).serialize(this.getSerializeType());
        //
        // .服务过滤器
        Map<String, RsfFilter> filters = this.getFilters();
        RsfPublisher.FilterBindBuilder<?> filterBuilder = configBuilder;
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, RsfFilter> rsfFilter : filters.entrySet()) {
                String key = rsfFilter.getKey();
                RsfFilter filter = rsfFilter.getValue();
                filterBuilder = filterBuilder.bindFilter(key, filter);
            }
        }
        // .服务属性
        RsfPublisher.RegisterBuilder<?> builder = filterBuilder;
        if (this.isOnShadow()) {
            builder = builder.asShadow();
        }
        // .定制化属性
        this.rsfClient = rsfContext.getRsfClient();
        RsfPublisher.RegisterBuilder<?> registerBuilder = this.configService(builder);
        registerBuilder.register();
    }
    //
    protected void loadDefaultValues() {
        RsfSettings settings = this.rsfContext.getSettings();
        if (StringUtils.isBlank(this.bindGroup)) {
            this.bindGroup = settings.getDefaultGroup();
        }
        if (StringUtils.isBlank(this.bindName)) {
            this.bindName = this.getBindType().getName();
        }
        if (StringUtils.isBlank(this.bindVersion)) {
            this.bindVersion = settings.getDefaultVersion();
        }
        if (this.clientTimeout <= 0) {
            this.clientTimeout = settings.getDefaultTimeout();
        }
        if (StringUtils.isBlank(this.serializeType)) {
            this.serializeType = settings.getDefaultSerializeType();
        }
    }
    //
    /** 注册服务 */
    protected abstract RsfPublisher.RegisterBuilder<?> configService(RsfPublisher.RegisterBuilder<?> builder);
}