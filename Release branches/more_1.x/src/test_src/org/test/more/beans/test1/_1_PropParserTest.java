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
import org.more.beans.core.propparser.MainPropertyParser;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.CreateTypeEnum;
import org.more.beans.info.PropList;
import org.more.beans.info.PropVarValue;
import org.more.beans.resource.ArrayResource;
/**
 * 
 * <br/>Date : 2009-11-20
 * @author Administrator
 */
public class _1_PropParserTest {
    public static BeanDefinition getBean_CreateTypeNew(String name) {
        BeanDefinition bean = new BeanDefinition();
        bean.setName(name);
        bean.setPropType("org.test.more.beans.testBeans.PropBean");
        bean.setCreateType(CreateTypeEnum.New);
        bean.setPropertys(new BeanProperty[4]);
        bean.getPropertys()[0] = new BeanProperty();
        bean.getPropertys()[0].setName("a");
        bean.getPropertys()[0].setPropType("int");
        bean.getPropertys()[0].setRefValue(new PropVarValue("123"));
        bean.getPropertys()[1] = new BeanProperty();
        bean.getPropertys()[1].setName("b");
        bean.getPropertys()[1].setPropType("float");
        bean.getPropertys()[1].setRefValue(new PropVarValue("123.5"));
        bean.getPropertys()[2] = new BeanProperty();
        bean.getPropertys()[2].setName("c");
        bean.getPropertys()[2].setPropType("String");
        bean.getPropertys()[2].setRefValue(new PropVarValue("这个是字符串"));
        //下面是一个虚拟属性，该例子是用于演示属性解析器。
        bean.getPropertys()[3] = new BeanProperty();
        bean.getPropertys()[3].setName("d");
        bean.getPropertys()[3].setPropType("java.util.List");
        PropList pa = new PropList(new BeanProp[] {//
                new PropVarValue("12", "int"),//
                        new PropVarValue("123.5", "float"),//
                        new PropVarValue("45678", "double") //
                });
        //pa.setLength(3);
        bean.getPropertys()[3].setRefValue(pa);
        return bean;
    }
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        //
        BeanDefinition[] definition = new BeanDefinition[] {};
        ResourceBeanFactory moreBeanFactory = new ResourceBeanFactory(new ArrayResource(null, definition), null);
        //
        MainPropertyParser m = new MainPropertyParser(moreBeanFactory);
        BeanDefinition bd = _1_PropParserTest.getBean_CreateTypeNew("testBean");
        BeanProperty bp = bd.getPropertys()[3];
        Object obj = m.parser(null, null, bp.getRefValue(), bp, bd);
        System.out.println(obj + "   Type:   " + obj.getClass());
    }
}