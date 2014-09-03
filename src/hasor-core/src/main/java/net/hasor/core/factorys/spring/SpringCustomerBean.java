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
package net.hasor.core.factorys.spring;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.core.Provider;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringCustomerBean implements FactoryBean, ApplicationContextAware, BeanNameAware, InitializingBean {
    private String                             springBeanName     = null;
    private AbstractApplicationContext         applicationContext = null;
    private AppContext                         appContext         = null;
    private AbstractBindInfoProviderAdapter<?> regObject          = null;
    private Object                             target             = null;
    //
    @Override
    public void setBeanName(String name) {
        this.springBeanName = name;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (AbstractApplicationContext) applicationContext;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        BeanDefinition define = this.applicationContext.getBeanFactory().getBeanDefinition(this.springBeanName);
        this.regObject = (AbstractBindInfoProviderAdapter<?>) define.getAttribute("RegObject");
        this.appContext = (AppContext) define.getAttribute("RegAppContext");
    }
    @Override
    public Object getObject() throws Exception {
        if (this.target != null) {
            return this.target;
        }
        //
        Object returnData = null;
        Provider<?> customerProvider = this.regObject.getCustomerProvider();
        if (customerProvider != null) {
            returnData = customerProvider.get();
        } else {
            //
            Class<?> finalClass = this.regObject.getBindType();
            if (this.regObject.getSourceType() != null) {
                finalClass = this.regObject.getSourceType();
            }
            returnData = finalClass.newInstance();
            returnData = this.applicationContext.getBeanFactory().configureBean(returnData, this.springBeanName);
        }
        //
        if (returnData instanceof InjectMembers) {
            ((InjectMembers) returnData).doInject(this.appContext);
        }
        if (this.isSingleton()) {
            this.target = returnData;
        }
        return returnData;
    }
    @Override
    public Class<?> getObjectType() {
        return this.regObject.getBindType();
    }
    @Override
    public boolean isSingleton() {
        return this.regObject.isSingleton();
    }
}