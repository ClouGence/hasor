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
package org.moretest.beans._b_impl;
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
        factory.init();
        //目前不支持rt.jar包中的接口，但如果是标记性接口会受到支持。注：以继承方式实现rt.jar中的接口也是无效的。
        XmlImplBean xmlImplBean = (XmlImplBean) factory.getBean("xmlImplBean");
        ((XmlUserFace1) xmlImplBean).println("附加接口实现测试");
        ((XmlUserFace2) xmlImplBean).xxxx("附加接口实现测试");
        //
        AnnoImplBean annoImplBean = (AnnoImplBean) factory.getBean("annoImplBean");
        ((AnnoUserFace1) annoImplBean).println("附加接口实现测试222");
        ((AnnoUserFace2) annoImplBean).xxxx("附加接口实现测试222");
        //
        System.out.println();
    }
}