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
package net.hasor.core.context.factorys.spring;
import java.util.Iterator;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.factorys.AbstractRegisterFactory;
import net.hasor.core.context.factorys.AbstractRegisterInfoAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringRegisterFactory extends AbstractRegisterFactory {
    private AbstractApplicationContext spring = null;
    //
    public ApplicationContext getSpring() {
        return this.spring;
    }
    /**创建Guice*/
    protected AbstractApplicationContext createSpring(Environment env) {
        ClassPathXmlApplicationContext spring = new ClassPathXmlApplicationContext();
        spring.refresh();
        return spring;
    }
    @Override
    public void doInitialize(ApiBinder apiBinder) {
        super.doInitialize(apiBinder);
        apiBinder.bindType(ApplicationContext.class).toProvider(new Provider<ApplicationContext>() {
            @Override
            public ApplicationContext get() {
                return getSpring();
            }
        });
    }
    @Override
    public void doInitializeCompleted(final AbstractAppContext appContext) {
        //1.检查
        super.doInitializeCompleted(appContext);
        //2.Spring
        this.spring = this.createSpring(appContext.getEnvironment());
        //3.注册
        Iterator<AbstractRegisterInfoAdapter<?>> registerIterator = this.getRegisterIterator();
        while (registerIterator.hasNext()) {
            AbstractRegisterInfoAdapter<?> regObject = registerIterator.next();
            if (regObject.getCustomerProvider() != null) {
                //单例Bean
                this.registerProvider(regObject);
            } else {
                //注册Bean
                this.registerBean(regObject);
            }
        }
    }
    //
    @Override
    protected <T> T newInstance(final RegisterInfo<T> oriType) {
        String name = oriType.getBindName();
        Class<T> type = oriType.getBindType();
        if (name == null) {
            name = type.getName();
        }
        return (T) this.spring.getBean(name, type);
    }
    //
    private void registerProvider(AbstractRegisterInfoAdapter<?> regObject) {
        String regName = regObject.getBindName();
        if (regName == null) {
            regName = regObject.getBindType().getName();
        }
        //
        ConfigurableListableBeanFactory factory = this.spring.getBeanFactory();
        BeanDefinitionRegistry defineRegistry = (BeanDefinitionRegistry) factory;
        BeanDefinitionBuilder defineBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringCustomerBean.class);
        if (regObject.isSingleton() == true) {
            defineBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        }
        BeanDefinition define = defineBuilder.getRawBeanDefinition();
        define.setAttribute("RegObject", regObject);
        defineRegistry.registerBeanDefinition(regName, define);
    }
    private void registerBean(AbstractRegisterInfoAdapter<?> regObject) {
        String regName = regObject.getBindName();
        if (regName == null) {
            regName = regObject.getBindType().getName();
        }
        Class<?> regType = regObject.getSourceType();
        if (regType == null) {
            regType = regObject.getBindType();
        }
        //
        ConfigurableListableBeanFactory factory = this.spring.getBeanFactory();
        BeanDefinitionRegistry defineRegistry = (BeanDefinitionRegistry) factory;
        BeanDefinitionBuilder define = BeanDefinitionBuilder.genericBeanDefinition(regType);
        if (regObject.isSingleton() == true) {
            define.setScope(BeanDefinition.SCOPE_SINGLETON);
        }
        defineRegistry.registerBeanDefinition(regName, define.getRawBeanDefinition());
    }
}