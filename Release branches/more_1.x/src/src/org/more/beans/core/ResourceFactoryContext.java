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
package org.more.beans.core;
import java.io.File;
import java.net.URI;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.beans.resource.ResourceFactory;
/**
 * 该BeanContext可以提供以BeanResource为基础的BeanContext接口实现。
 * ResourceFactoryContext对象的父级Context是LoadDefaultConfigContext。
 * @version 2010-2-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class ResourceFactoryContext extends AbstractBeanContext {
    //==================================================================================Constructor
    /**创建ResourceFactoryContext对象。*/
    public ResourceFactoryContext(BeanFactory beanFactory) throws Exception {
        defaultBeanFactory = beanFactory;
        this.init();
    }
    /**创建ResourceFactoryContext对象。*/
    public ResourceFactoryContext(BeanResource beanResource) throws Exception {
        beanResource.reload();
        defaultBeanFactory = new ResourceBeanFactory(beanResource);
        this.init();
    }
    /**创建ResourceFactoryContext对象。*/
    public ResourceFactoryContext(File configFile) throws Exception {
        BeanResource beanResource = ResourceFactory.create(configFile);
        beanResource.reload();
        defaultBeanFactory = new ResourceBeanFactory(beanResource);
        this.init();
    }
    /**创建ResourceFactoryContext对象。*/
    public ResourceFactoryContext(URI configURI) throws Exception {
        BeanResource beanResource = ResourceFactory.create(configURI);
        beanResource.reload();
        defaultBeanFactory = new ResourceBeanFactory(beanResource);
        this.init();
    }
    @Override
    public void init() throws Exception {
        this.setParent(new LoadDefaultConfigContext());
        super.init();
    }
}