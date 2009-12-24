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
package org.more.submit.casing.spring;
import org.more.InitializationException;
import org.more.InvokeException;
import org.more.submit.ActionContext;
import org.more.submit.CasingBuild;
import org.more.submit.Config;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * 提供以spring为容器的submit3.0支撑环境，并且提供了一些常见创建方法。
 * 当使用默认构造方法创建ClientSpringBuilder之后可以使用init方法传递参数configFile来指定配置文件位置从而初始化ClientSpringBuilder。
 * <br/>Date : 2009-11-21
 * @author 赵永春
 */
public class ClientSpringBuilder extends CasingBuild {
    //========================================================================================Field
    protected AbstractApplicationContext springContext = null;
    //==================================================================================Constructor
    public ClientSpringBuilder() {}
    /**通过一个SpringContext对象创建SpringCasingBuilder。*/
    public ClientSpringBuilder(AbstractApplicationContext springContext) {
        if (springContext == null)
            throw new NullPointerException("springContext参数不能为空。");
        this.springContext = springContext;
    }
    /**通过一个SpringContext对象创建SpringCasingBuilder。*/
    public ClientSpringBuilder(String configLocation) {
        this.springContext = new FileSystemXmlApplicationContext(configLocation);
        if (this.springContext == null)
            throw new InvokeException("无法创建FileSystemXmlApplicationContext对象，请检查配置文件位置。");
    }
    //==========================================================================================Job
    /**该方法紧当使用ClientMoreBuilder构造方法创建对象时有效。*/
    @Override
    public void init(Config config) {
        super.init(config);
        if (this.springContext == null)
            this.springContext = new FileSystemXmlApplicationContext(config.getInitParameter("configFile"));
    }
    @Override
    public ActionContext getActionFactory() {
        if (springContext == null)
            throw new InitializationException("没有执行初始化操作。");
        return new SpringActionContext(this.springContext);
    }
}