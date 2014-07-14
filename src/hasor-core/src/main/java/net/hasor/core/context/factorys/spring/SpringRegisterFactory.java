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
import net.hasor.core.RegisterInfo;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.factorys.AbstractRegisterFactory;
import net.hasor.core.context.factorys.AbstractRegisterInfoAdapter;
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
    protected AbstractApplicationContext createSpring() {
        return new ClassPathXmlApplicationContext();
    }
    protected <T> AbstractRegisterInfoAdapter<T> createRegisterInfoAdapter(Class<T> bindingType) {
        SpringRegisterInfoAdapter<T> adapter = new SpringRegisterInfoAdapter<T>();
        adapter.setBindType(bindingType);
        return adapter;
    }
    public void doInitializeCompleted(AbstractAppContext appContext) {
        //1.检查
        super.doInitializeCompleted(appContext);
        //2.绑定
        //TODO
        //      AbstractApplicationContext spring = new ClassPathXmlApplicationContext();
        //      spring.refresh();
        //      BeanDefinitionRegistry reg = (BeanDefinitionRegistry) spring.getBeanFactory();
        //      //
        //      //
        //      Set<Class<?>> anonymityTypes = new HashSet<Class<?>>();
        //      for (SpringTypeRegister<?> tempItem : tempRegisterList) {
        //          SpringTypeRegister<Object> register = (SpringTypeRegister<Object>) tempItem;
        //          if (ifAnonymity(register)) {
        //              /*多一层判断防止相同的类型的匿名注册重复注册*/
        //              Class<?> bindType = register.getType();
        //              if (anonymityTypes.contains(bindType) == true)
        //                  continue;
        //              anonymityTypes.add(bindType);
        //          }
        //          //
        //          String name = register.getName();
        //          name = !StringUtils.isBlank(name) ? name : register.getType().getName();
        //          BeanDefinition define = this.paserBeanDefinition(name, register);
        //          reg.registerBeanDefinition(name, define);
    }
    protected <T> T newInstance(RegisterInfo<T> oriType) {
        String name = oriType.getBindName();
        Class<T> type = oriType.getBindType();
        if (name == null) {
            name = type.getName();
        }
        return (T) this.spring.getBean(name, type);
    }
}