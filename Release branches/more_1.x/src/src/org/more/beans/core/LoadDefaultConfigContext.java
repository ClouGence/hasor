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
import java.net.URL;
import org.more.beans.BeanResource;
import org.more.beans.resource.ResourceFactory;
/**
 * 默认配置文件下的BeanContext接口实现。使用该类只能装载more jar包中携带的默认配置文件。
 * @version 2010-2-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class LoadDefaultConfigContext extends AbstractBeanContext {
    //==================================================================================Constructor
    public LoadDefaultConfigContext() throws Exception {
        this.init();
    }
    //=============================================================================Impl BeanContext
    @Override
    public void init() throws Exception {
        URL configURL = LoadDefaultConfigContext.class.getResource("/META-INF/default-more-config.xml");
        BeanResource resource = ResourceFactory.create(configURL.toURI());
        resource.reload();
        defaultBeanFactory = new ResourceBeanFactory(resource);
        super.init();
    }
}