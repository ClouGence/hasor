///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.hasor.core.context.factorys.spring;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import net.hasor.core.Environment;
//import net.hasor.core.RegisterInfo;
//import net.hasor.core.binder.TypeRegister;
//import net.hasor.core.binder.register.AbstractTypeRegister;
//import net.hasor.core.binder.register.FreeTypeRegister;
//import net.hasor.core.builder.BeanBuilder;
//import net.hasor.core.context.AbstractAppContext;
//import net.hasor.core.context.RegisterManager;
//import net.hasor.core.context.RegisterManagerCreater;
//import net.hasor.core.context.listener.ContextInitializeListener;
//import org.more.util.Iterators;
//import org.more.util.Iterators.Converter;
//import org.more.util.StringUtils;
//import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.AbstractBeanDefinition;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.context.support.AbstractApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
///**
// * 
// * @version : 2014-5-10
// * @author 赵永春 (zyc@byshell.org)
// */
//public class SpringRegisterManagerCreater implements RegisterManagerCreater {
//    public RegisterManager create(Environment env) {
//        return new SpringRegisterManager();
//    }
//}
///*RegisterManager接口实现*/
//class SpringRegisterManager implements RegisterManager, ContextInitializeListener {
//    //
//    /*-----------------------------------------------------------------Collect GuiceTypeRegisters*/
//    private List<SpringTypeRegister<?>> tempRegisterList = new ArrayList<SpringTypeRegister<?>>();
//    public <T> TypeRegister<T> registerType(Class<T> type) {
//        SpringTypeRegister<T> register = new SpringTypeRegister<T>(type);
//        this.tempRegisterList.add(register);
//        return register;
//    }
//    public synchronized void doInitialize(AbstractAppContext appContext) {
//        // TODO Auto-generated method stub
//    }
//    //
//    /*------------------------------------------------------------------------------add to Spring*/
//    public void doInitializeCompleted(AbstractAppContext appContext) {
//        AbstractApplicationContext spring = new ClassPathXmlApplicationContext();
//        spring.refresh();
//        BeanDefinitionRegistry reg = (BeanDefinitionRegistry) spring.getBeanFactory();
//        //
//        //
//        Set<Class<?>> anonymityTypes = new HashSet<Class<?>>();
//        for (SpringTypeRegister<?> tempItem : tempRegisterList) {
//            SpringTypeRegister<Object> register = (SpringTypeRegister<Object>) tempItem;
//            if (ifAnonymity(register)) {
//                /*多一层判断防止相同的类型的匿名注册重复注册*/
//                Class<?> bindType = register.getType();
//                if (anonymityTypes.contains(bindType) == true)
//                    continue;
//                anonymityTypes.add(bindType);
//            }
//            //
//            String name = register.getName();
//            name = !StringUtils.isBlank(name) ? name : register.getType().getName();
//            BeanDefinition define = this.paserBeanDefinition(name, register);
//            reg.registerBeanDefinition(name, define);
//        }
//        tempRegisterList.clear();
//        //
//        //
//        this.springBuilder = new SpringBeanBuilder(spring);
//    }
//    /*测试register是否为匿名的*/
//    private boolean ifAnonymity(SpringTypeRegister<Object> register) {
//        return StringUtils.isBlank(register.getName());
//    }
//    private BeanDefinition paserBeanDefinition(String beanName, SpringTypeRegister<Object> register) {
//        AnnotatedGenericBeanDefinition define = new AnnotatedGenericBeanDefinition(register.getType());
//        define.set
//        //        
//        //        //1.绑定类型
//        //        //2.绑定名称
//        //        boolean haveName = false;
//        //        String name = register.getName();
//        //        if (!StringUtils.isBlank(name)) {
//        //            linkedBinding = annoBinding.annotatedWith(Names.named(name));
//        //            haveName = true;
//        //        }
//        //        //3.绑定实现
//        //        if (register.getProvider() != null)
//        //            scopeBinding = linkedBinding.toProvider(new GuiceProvider<Object>(register.getProvider()));
//        //        else if (register.getImplConstructor() != null)
//        //            scopeBinding = linkedBinding.toConstructor(register.getImplConstructor());
//        //        else if (register.getImplType() != null)
//        //            scopeBinding = linkedBinding.to(register.getImplType());
//        //        else {
//        //            if (haveName == true)
//        //                scopeBinding = linkedBinding.to(register.getType());/*自己绑定自己*/
//        //        }
//        //        //4.处理单例
//        //        if (register.isSingleton()) {
//        //            scopeBinding.asEagerSingleton();
//        //            return;/*第五步不进行处理*/
//        //        }
//        //        //5.绑定作用域
//        //        Scope scope = register.getScope();
//        //        if (scope != null)
//        //            scopeBinding.in(new GuiceScope(scope));
//        //        //
//        return define;
//    }
//    private SpringBeanBuilder springBuilder;
//    public BeanBuilder getBeanBuilder() {
//        return this.springBuilder;
//    }
//}
///**用来创建Bean、查找Bean*/
//class SpringBeanBuilder implements BeanBuilder {
//    private AbstractApplicationContext spring;
//    //
//    public SpringBeanBuilder(AbstractApplicationContext spring) {
//        this.spring = spring;
//    }
//    //
//    public <T> T getInstance(RegisterInfo<T> oriType) {
//        if (oriType == null)
//            return null;
//        //
//        if (oriType instanceof SpringTypeRegister) {
//            String beanID = oriType.getName();
//            beanID = (beanID != null) ? beanID : oriType.getType().getName();
//            return (T) this.spring.getBean(beanID, oriType.getType());
//        } else if (oriType instanceof FreeTypeRegister) {
//            Class<T> createType = ((FreeTypeRegister<T>) oriType).getType();
//            return (T) this.spring.getBean(createType.getName());
//        }
//        throw new UnsupportedOperationException(String.format("%s RegisterInfo.", oriType.getClass()));
//    }
//    //
//    public Iterator<RegisterInfo<?>> getRegisterIterator() {
//        List<String> names = new ArrayList<String>();
//        names.addAll(Arrays.asList(spring.getBeanDefinitionNames()));
//        Iterator<RegisterInfo<Object>> iterator = this.registerIterator(names.iterator());
//        return Iterators.converIterator(iterator, new Converter<RegisterInfo<Object>, RegisterInfo<?>>() {
//            public RegisterInfo<?> converter(RegisterInfo<Object> target) {
//                return target;
//            }
//        });
//    }
//    public <T> Iterator<RegisterInfo<T>> getRegisterIterator(Class<T> type) {
//        List<String> names = new ArrayList<String>();
//        names.addAll(Arrays.asList(spring.getBeanNamesForType(type)));
//        return this.registerIterator(names.iterator());
//    }
//    private <T> Iterator<RegisterInfo<T>> registerIterator(Iterator<String> defines) {
//        final ConfigurableListableBeanFactory factory = spring.getBeanFactory();
//        return Iterators.converIterator(defines, new Converter<String, RegisterInfo<T>>() {
//            public RegisterInfo<T> converter(String target) {
//                AbstractBeanDefinition define = (AbstractBeanDefinition) factory.getBeanDefinition(target);
//                return new SpringTypeRegister<T>(define);
//            }
//        });
//    }
//}
////
///*---------------------------------------------------------------------------------------Util*/
//class SpringTypeRegister<T> extends AbstractTypeRegister<T> {
//    public SpringTypeRegister(Class<T> type) {
//        super(type);
//    }
//    public SpringTypeRegister(AbstractBeanDefinition define) {
//        super(define.getBeanClass());
//        System.out.println();
//    }
//}