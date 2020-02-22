/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.beans;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.utils.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 在 Spring 中创建 Hasor Bean 使用。
 * @version : 2016年2月15日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TargetFactoryBean<T> implements FactoryBean<T>, InitializingBean {
    private AppContext            factory      = null;
    private Supplier<? extends T> beanProvider = null;
    private BindInfo<? extends T> beanBindInfo = null;
    private String                refID        = null;
    private Class<? extends T>    refType      = null;
    private String                refName      = null;

    public void setFactory(AppContext factory) {
        this.factory = factory;
    }

    public void setRefType(Class<? extends T> refType) {
        this.refType = refType;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.factory == null) {
            throw new NullPointerException("AppContext is null.");
        }
        //
        if (StringUtils.isNotBlank(this.refID)) {
            this.beanBindInfo = this.factory.getBindInfo(this.refID);
        } else {
            if (this.refType != null) {
                if (StringUtils.isBlank(this.refName)) {
                    this.refName = "";
                }
                this.beanBindInfo = this.factory.findBindingRegister(this.refName, this.refType);
            } else {
                throw new NullPointerException("refType is null.");
            }
        }
        //
        if (this.beanBindInfo == null) {
            this.beanProvider = Objects.requireNonNull(this.factory.getProvider(this.refType));
        } else {
            this.beanProvider = Objects.requireNonNull(this.factory.getProvider(this.beanBindInfo));
        }
    }

    @Override
    public final T getObject() {
        return this.beanProvider.get();
    }

    @Override
    public final Class<?> getObjectType() {
        return this.beanBindInfo.getBindType();
    }

    @Override
    public final boolean isSingleton() {
        return this.factory.isSingleton(this.beanBindInfo);
    }
}
