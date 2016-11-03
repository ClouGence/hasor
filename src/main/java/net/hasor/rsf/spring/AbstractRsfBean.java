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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/**
 * 包装来自 Spring 的 Bean。
 *
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractRsfBean implements FactoryBean, InitializingBean, ApplicationContextAware {
    private String   bindGroup        = null;
    private String   bindName         = null;
    private String   bindVersion      = null;
    private Class<?> bindType         = null;
    private String   bindDesc         = "";
    private int      clientTimeout    = 600000;
    private String   serializeType    = null;
    private boolean  sharedThreadPool = true;
    private boolean  onShadow         = false;
    //
    private ApplicationContext springContext;
    private AppContext         hasorContext;
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
        this.springContext = springContext;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        //        if (this.factory == null) {
        //            throw new NullPointerException("AppContext is null.");
        //        }
        //        //
        //        if (StringUtils.isNotBlank(this.refID)) {
        //            this.beanBindInfo = this.factory.getBindInfo(this.refID);
        //        } else {
        //            if (this.refName != null && this.refType != null) {
        //                this.beanBindInfo = this.factory.findBindingRegister(this.refName, this.refType);
        //            }
        //        }
        //        if (this.beanBindInfo == null && this.refType == null) {
        //            throw new NullPointerException("HasorBean class is null.");
        //        }
        //        //
        //        if (this.beanBindInfo != null) {
        //            this.beanProvider = this.factory.getProvider(this.beanBindInfo);
        //        }
    }
    //
    protected ApplicationContext getSpringContext() {
        return this.springContext;
    }
    protected AppContext getHasorContext() {
        return this.hasorContext;
    }
    //
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
    public boolean isSharedThreadPool() {
        return this.sharedThreadPool;
    }
    public void setSharedThreadPool(boolean sharedThreadPool) {
        this.sharedThreadPool = sharedThreadPool;
    }
    public boolean isOnShadow() {
        return this.onShadow;
    }
    public void setOnShadow(boolean onShadow) {
        this.onShadow = onShadow;
    }
}