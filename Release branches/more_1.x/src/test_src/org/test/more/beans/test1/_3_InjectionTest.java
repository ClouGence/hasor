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
package org.test.more.beans.test1;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.core.injection.InjectionFactory;
import org.more.beans.core.propparser.MainPropertyParser;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.CreateTypeEnum;
import org.more.beans.info.IocTypeEnum;
import org.more.beans.info.PropArray;
import org.more.beans.info.PropList;
import org.more.beans.info.PropVarValue;
import org.more.beans.resource.ArrayResource;
import org.test.more.beans.testBeans.PropBean;
/**
 * 
 * Date : 2009-11-20
 * @author Administrator
 */
public class _3_InjectionTest {
    public static BeanProperty[] getProps() {
        BeanProperty[] bp = new BeanProperty[5];
        bp[0] = new BeanProperty();
        bp[0].setName("a");
        bp[0].setPropType("int");
        bp[0].setRefValue(new PropVarValue("123"));
        bp[1] = new BeanProperty();
        bp[1].setName("b");
        bp[1].setPropType("float");
        bp[1].setRefValue(new PropVarValue("123.5"));
        bp[2] = new BeanProperty();
        bp[2].setName("c");
        bp[2].setPropType("String");
        bp[2].setRefValue(new PropVarValue("这个是字符串"));
        bp[3] = new BeanProperty();
        bp[3].setName("objectArray");
        bp[3].setPropType("java.lang.Object");
        bp[3].setRefValue(new PropArray(new BeanProp[] {//
                new PropVarValue("12", "int"),//
                        new PropVarValue("123.5", "double"),//
                        new PropVarValue("这个是字符串", "String") //
                }));
        //==================
        bp[4] = new BeanProperty();
        bp[4].setName("objectList");
        bp[4].setPropType("java.util.List");
        bp[4].setRefValue(new PropList(new BeanProp[] {//
                new PropVarValue("12", "int"),//
                        new PropVarValue("123.5", "double"),//
                        new PropVarValue("这个是字符串", "String") //
                }));
        return bp;
    }
    public static BeanDefinition getBean_CreateTypeNew(String name) {
        BeanDefinition bean = new BeanDefinition();
        bean.setName(name);
        bean.setPropType("org.test.more.beans.testBeans.PropBean");
        bean.setCreateType(CreateTypeEnum.New);
        bean.setConstructorParams(getProps());
        return bean;
    }
    public static BeanDefinition getBean_CreateTypeFactory(String name, String factoryBeanName) {
        BeanDefinition bean = new BeanDefinition();
        bean.setName(name);
        bean.setPropType("org.test.more.beans.testBeans.PropBean");
        bean.setCreateType(CreateTypeEnum.Factory);
        bean.setFactoryRefBean(factoryBeanName);
        bean.setFactoryIsStaticMethod(false);
        bean.setFactoryMethodName("create");
        bean.setFactoryMethodParams(getProps());
        return bean;
    }
    /***/
    public static BeanDefinition iocInfo(BeanDefinition bean, IocTypeEnum iocType, String exportIocName) {
        bean.setIocType(iocType);
        bean.setExportRefBean(exportIocName);
        bean.setPropertys(getProps());
        return bean;
    }
    public static BeanDefinition getCreateFactoryBean(String name) {
        BeanDefinition bean = new BeanDefinition();
        bean.setName(name);
        bean.setPropType("org.test.more.beans.testBeans.BeanFactory");
        bean.setCreateType(CreateTypeEnum.New);
        bean.setIocType(IocTypeEnum.Ioc);
        return bean;
    }
    public static BeanDefinition getExportInjectionBean(String name) {
        BeanDefinition bean = new BeanDefinition();
        bean.setCreateType(CreateTypeEnum.New);
        bean.setName(name);
        bean.setIocType(IocTypeEnum.Ioc);
        bean.setPropType("org.test.more.beans.testBeans.TestExportInjectionProperty");
        //bean.setSingleton(true);
        return bean;
    }
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        BeanDefinition newBean = _3_InjectionTest.getBean_CreateTypeNew("newTest");
        BeanDefinition factoryBean = _3_InjectionTest.getBean_CreateTypeFactory("factoryTest", "factory");
        //
        BeanDefinition factory = _3_InjectionTest.getCreateFactoryBean("factory");
        BeanDefinition exportIoc = _3_InjectionTest.getExportInjectionBean("exportIocBean");
        _3_InjectionTest.iocInfo(factoryBean, IocTypeEnum.Fact, "exportIocBean");
        _3_InjectionTest.iocInfo(newBean, IocTypeEnum.Fact, "exportIocBean");
        //
        BeanDefinition[] definition = new BeanDefinition[] { exportIoc, factory, factoryBean, newBean };
        ResourceBeanFactory moreBeanFactory = new ResourceBeanFactory(new ArrayResource("testResource", definition), null);
        InjectionFactory iFactory = new InjectionFactory(new MainPropertyParser(moreBeanFactory));
        //
        PropBean pb = new PropBean();
        iFactory.ioc(pb, null, newBean, moreBeanFactory);
        System.out.println(pb.getObjectArray());
    }
}