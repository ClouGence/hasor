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
import org.more.beans.resource.XmlFileResource;
import a.beanContextTest.testBeans.Face;
import a.beanContextTest.testBeans.SimpleBean;
/**
 * 
 * Date : 2009-11-23
 * @author Administrator
 */
public class _5_FileResourceTest {
    public static void main(String[] args) throws Throwable {
        System.out.println("Start...");
        ResourceBeanFactory factory = new ResourceBeanFactory(new XmlFileResource("beansContext.xml"), null);
        System.out.println("Start End\n");
        //
        //
        //
        //
        Object obj = factory.getBean("test_aop", "asdf");
        System.out.println(obj);
        //
        SimpleBean sb = (SimpleBean) obj;
        Face f = (Face) obj;
        f.println("adfsd");
        System.out.println(sb + "" + f);
        //System.out.println(obj + "\t\t" + obj.getClass());
        //
        //
        //
        //
        //
        //
        //
        //        ResourceBeanFactory factory = new ResourceBeanFactory(new XmlFileResource("beansContext.xml"), null);
        //        factory.isFactory("test1");
        //        long start1 = new Date().getTime();
        //        for (int i = 0; i < 200000; i++)
        //            factory.getBean("test1");
        //        long end1 = new Date().getTime();
        //        System.out.println("time:" + (end1 - start1) + "\tMoreIoc");
        //
        //
        //
        //
        //
        //        FileSystemXmlApplicationContext spring = new FileSystemXmlApplicationContext();
        //        spring.setConfigLocation("applicationContext.xml");
        //        spring.refresh();
        //        long start4 = new Date().getTime();
        //        for (int i = 0; i < 200000; i++)
        //            spring.getBean("test1");
        //        long end4 = new Date().getTime();
        //        System.out.println("time:" + (end4 - start4) + "\tSpringIoc");
    }
}