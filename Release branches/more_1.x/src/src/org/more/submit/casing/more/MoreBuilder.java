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
import java.io.IOException;
import org.more.beans.BeanFactory;
import org.more.beans.core.ContextFactory;
import org.more.submit.ActionContext;
import org.more.submit.ActionContextBuild;
import org.more.util.Config;
/**
 * ClientMoreBuilder提供了beans软件包为容器的submit支撑环境，并且提供了一些常见创建方法。<br/>
 * 如果不指定配置文件名称ClientMoreBuilder会自动在当前路径下寻找名称为more-config.xml的配置文件。<br/>
 * init：<br/>
 * 参数beanFactory优先级：高，BeanFactory类型对象。<br/>
 * 参数configFile优先级：底，String类型对象。
 * @version 2010-1-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreBuilder extends ActionContextBuild {
    //========================================================================================Field
    public static final File DefaultConfig = new File("more-config.xml");
    protected BeanFactory    factory       = null;
    protected File           config        = null;
    //==================================================================================Constructor
    /**创建ClientMoreBuilder，同时初始化ClientMoreBuilder对象使用默认配置文件名为more-config.xml其文件保存在当前路径下。*/
    public MoreBuilder() throws Exception {
        this.config = MoreBuilder.DefaultConfig;
    };
    /**创建ClientMoreBuilder，通过configFile参数来决定使用那个配置文件初始化ClientMoreBuilder对象。*/
    public MoreBuilder(String configFile) throws Exception {
        this.config = new File(configFile);
    };
    /**创建ClientMoreBuilder，通过configFile参数来决定使用那个配置文件初始化ClientMoreBuilder对象。*/
    public MoreBuilder(File configFile) throws Exception {
        if (configFile == null)
            this.config = MoreBuilder.DefaultConfig;
        else
            this.config = configFile;
    };
    /**提供一个更高级的方式创建ClientMoreBuilder对象，该构造方法将使用指定的BeanFactory类型对象作为创建ActionContext而使用的数据源。*/
    public MoreBuilder(BeanFactory beanFactory) {
        this.factory = beanFactory;
    };
    //==========================================================================================Job
    public void init(Config config) throws Exception {
        if (this.factory != null)
            return;
        if (this.config.exists() == false || this.config.canRead() == false)
            throw new IOException("配置文件[" + this.config.getAbsolutePath() + "]不存在，或者无法读取。");
        factory = ContextFactory.create(this.config);//使用ResourceFactory创建一个BeanResource
        factory.init();
    };
    @Override
    public ActionContext getActionContext() {
        return new MoreContext(this.factory);
    }
}