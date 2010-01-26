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
package org.moretest.beans._2_singleton;
import org.more.beans.BeanFactory;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.resource.AnnoXmlFileResource;
public class Main {
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        BeanFactory factory = new ResourceBeanFactory(new AnnoXmlFileResource());
        //
        Object obj_1 = null;
        Object obj_2 = null;
        //默认配置是单态的。
        obj_1 = factory.getBean("xmlBean");
        obj_2 = factory.getBean("xmlBean");
        System.out.println(obj_1 == obj_2);
        //将单态关闭
        obj_1 = factory.getBean("annoSingletonBean");
        obj_2 = factory.getBean("annoSingletonBean");
        System.out.println(obj_1 == obj_2);
        obj_1 = factory.getBean("xmlSingletonBean");
        obj_2 = factory.getBean("xmlSingletonBean");
        System.out.println(obj_1 == obj_2);
    }
}