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
import java.net.URL;
import java.net.URLDecoder;
import org.more.beans.BeanFactory;
import org.more.beans.core.ContextFactory;
import org.more.submit.ActionContext;
import org.more.submit.ActionContextBuild;
import org.more.util.Config;
/**
 * MoreBuilder提供了beans软件包为容器的submit支撑环境，并且提供了一些常见创建方法。<br/>
 * 如果不指定配置文件名称MoreBuilder会自动在当前类路径下寻找名称为more-config.xml的配置文件。<br/>
 * MoreBuilder类的构造方法会自动检测参数是否为空如果为空会使用默认参数。如果使用setConfig方法设置配置文件位置则可以设置一个null进去但是init会引发异常。
 * init：参数configFile，String类型对象表示配置文件位置。这里的配置文件设置可以替换创建MoreBuilder对象时构造方法传入的配置文件位置。
 * @version : 2010-8-2
 * @author 赵永春(zyc@byshell.org)
 */
public class MoreBuilder implements ActionContextBuild {
    //========================================================================================Field
    public static final String DefaultConfig = "more-config.xml";
    protected BeanFactory      factory       = null;
    protected String           config        = null;
    //==================================================================================Constructor
    /**创建MoreBuilder，使用默认配置文件{@link MoreBuilder#DefaultConfig}。*/
    public MoreBuilder() throws Exception {
        this.config = MoreBuilder.DefaultConfig;
    };
    /**创建MoreBuilder，使用指定配置文件，如果指定配置文件为空则使用默认配置文件{@link MoreBuilder#DefaultConfig}。*/
    public MoreBuilder(String configFile) throws Exception {
        if (configFile == null || configFile.equals(""))
            this.config = MoreBuilder.DefaultConfig;
        else
            this.config = configFile;
    };
    /**创建ClientMoreBuilder，如果指定配置文件为空则使用默认配置文件{@link MoreBuilder#DefaultConfig}。*/
    public MoreBuilder(File configFile) throws Exception {
        if (configFile == null)
            this.config = MoreBuilder.DefaultConfig;
        else
            this.config = configFile.getAbsolutePath();
    };
    //==========================================================================================Job
    public void init(Config config) throws Exception {
        if (this.factory != null)
            return;
        if (config != null) {
            Object configParam = config.getInitParameter("configFile");
            if (configParam != null)
                this.setConfig(configParam.toString());
        };
        //
        File configFile = new File(this.config);
        if (configFile.isAbsolute() == false) {
            URL url = Thread.currentThread().getContextClassLoader().getResource(this.config);
            if (url != null)
                configFile = new File(URLDecoder.decode(url.getFile(), "utf-8"));
        }
        //
        if (configFile.exists() == false || configFile.canRead() == false)
            throw new IOException("配置文件[" + configFile.getAbsolutePath() + "]不存在，或者无法读取。");
        factory = ContextFactory.create(configFile);//使用ResourceFactory创建一个BeanResource
        factory.init();
    };
    /**获取配置文件位置。*/
    public String getConfig() {
        return config;
    };
    /**设置配置文件位置，此处可以设置null作为参数，但是在init时会引发空指针异常。*/
    public void setConfig(String config) {
        this.config = config;
    };
    @Override
    public ActionContext getActionContext() {
        return new MoreContext(this.factory);
    };
};