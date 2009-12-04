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
package org.more.submit.casing.more;
import java.io.File;
import org.more.InitializationException;
import org.more.beans.BeanFactory;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.resource.XmlFileResource;
import org.more.submit.ActionContext;
import org.more.submit.CasingBuild;
import org.more.submit.Config;
/**
 * 提供以beans软件包为容器的submit3.0支撑环境，并且提供了一些常见创建方法。
 * 当使用默认构造方法创建ClientMoreBuilder之后可以使用init方法传递参数configFile来指定配置文件位置从而初始化ClientMoreBuilder。
 * <br/>Date : 2009-11-21
 * @author 赵永春
 */
public class ClientMoreBuilder extends CasingBuild {
    //========================================================================================Field
    protected BeanFactory factory = null;
    //==================================================================================Constructor
    public ClientMoreBuilder() {}
    /**创建submit3.0支撑环境，使用指定的more.beans容器来创建它。*/
    public ClientMoreBuilder(BeanFactory factory) {
        if (factory == null)
            throw new NullPointerException("factory参数不能为空。");
        this.factory = factory;
    }
    /**创建submit3.0支撑环境，使用指定的more.beans配置文件初始化more.beans容器。*/
    public ClientMoreBuilder(String configFile) {
        this.factory = new ResourceBeanFactory(new XmlFileResource(configFile), null);
    }
    /**创建submit3.0支撑环境，使用指定的more.beans配置文件初始化more.beans容器。*/
    public ClientMoreBuilder(File configFile) {
        this.factory = new ResourceBeanFactory(new XmlFileResource(configFile), null);
    }
    //==========================================================================================Job
    /**该方法紧当使用ClientMoreBuilder构造方法创建对象时有效。*/
    @Override
    public void init(Config config) {
        super.init(config);
        if (this.factory == null)
            this.factory = new ResourceBeanFactory(new XmlFileResource(config.getInitParameter("configFile")), null);
    }
    @Override
    public ActionContext getActionFactory() {
        if (factory == null)
            throw new InitializationException("没有执行初始化操作。");
        return new MoreActionContext(this.factory);
    }
}