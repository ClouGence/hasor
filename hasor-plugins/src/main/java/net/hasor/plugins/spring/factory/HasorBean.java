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
package net.hasor.plugins.spring.factory;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
/**
 * 在Spring获取Hasor的Bean
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorBean<T> implements FactoryBean, InitializingBean {
    protected static Logger                logger       = LoggerFactory.getLogger(Hasor.class);
    private          AppContext            factory      = null;
    private          Provider<? extends T> beanProvider = null;
    private          BindInfo<? extends T> beanBindInfo = null;
    //
    private          String                refID        = null;
    private          Class<? extends T>    refType      = null;
    private          String                refName      = null;
    //
    public AppContext getFactory() {
        return factory;
    }
    public void setFactory(AppContext factory) {
        this.factory = factory;
    }
    public Class<? extends T> getRefType() {
        return refType;
    }
    public void setRefType(Class<? extends T> refType) {
        this.refType = refType;
    }
    public String getRefName() {
        return refName;
    }
    public void setRefName(String refName) {
        this.refName = refName;
    }
    public String getRefID() {
        return refID;
    }
    public void setRefID(String refID) {
        this.refID = refID;
    }
    //
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.factory == null) {
            throw new NullPointerException("AppContext is null.");
        }
        //
        if (StringUtils.isNotBlank(this.refID)) {
            this.beanBindInfo = this.factory.getBindInfo(this.refID);
        } else {
            if (this.refName != null && this.refType != null) {
                this.beanBindInfo = this.factory.findBindingRegister(this.refName, this.refType);
            }
        }
        if (this.beanBindInfo == null && this.refType == null) {
            throw new NullPointerException("HasorBean class is null.");
        }
        //
        if (this.beanBindInfo != null) {
            this.beanProvider = this.factory.getProvider(this.beanBindInfo);
        }
    }
    //
    @Override
    public final Object getObject() throws Exception {
        if (this.beanProvider != null) {
            return this.beanProvider.get();
        }
        if (this.refType != null) {
            return this.factory.getInstance(this.refType);
        }
        throw new IllegalStateException("has not been initialized");
    }
    @Override
    public final Class<?> getObjectType() {
        if (this.refType == null) {
            if (this.beanBindInfo == null) {
                throw new IllegalStateException("has not been initialized");
            } else {
                return this.beanBindInfo.getBindType();
            }
        } else {
            return this.refType;
        }
    }
    @Override
    public final boolean isSingleton() {
        return false;
    }
}