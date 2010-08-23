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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import javax.servlet.ServletContext;
import org.more.FormatException;
import org.more.submit.ActionContext;
import org.more.submit.ActionContextBuild;
import org.more.util.Config;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * SpringBuilder提供了spring2.5为容器的submit支撑环境，并且提供了一些常见创建方法。<br/>
 * 如果不指定配置文件名称SpringBuilder会自动在当前类路径下寻找名称为applicationContext.xml的配置文件。<br/>
 * SpringBuilder类的构造方法会自动检测参数是否为空如果为空会使用默认参数。如果使用setConfig方法设置配置文件位置则可以设置一个null进去但是init会引发异常。
 * init：参数configFile，String类型对象表示配置文件位置。这里的配置文件设置可以替换创建SpringBuilder对象时构造方法传入的配置文件位置。
 * @version : 2010-8-2
 * @author 赵永春(zyc@byshell.org)
 */
public class SpringBuilder implements ActionContextBuild {
    //========================================================================================Field
    public static final String           DefaultConfig = "applicationContext.xml";
    protected AbstractApplicationContext springContext = null;
    protected String                     config        = null;
    private File                         baseDir       = null;
    //==================================================================================Constructor
    /**创建ClientMoreBuilder，同时初始化ClientMoreBuilder对象使用默认配置文件名为more-config.xml其文件保存在当前路径下。*/
    public SpringBuilder() throws Exception {
        this.config = SpringBuilder.DefaultConfig;
    };
    /**创建ClientMoreBuilder，通过configFile参数来决定使用那个配置文件初始化ClientMoreBuilder对象。*/
    public SpringBuilder(String configFile) throws Exception {
        if (configFile == null || configFile.equals(""))
            this.config = SpringBuilder.DefaultConfig;
        else
            this.config = configFile;
    };
    /**创建ClientMoreBuilder，通过configFile参数来决定使用那个配置文件初始化ClientMoreBuilder对象。*/
    public SpringBuilder(File configFile) throws Exception {
        if (configFile == null)
            this.config = SpringBuilder.DefaultConfig;
        else
            this.config = configFile.getAbsolutePath();
    };
    //==========================================================================================Job
    /**获取配置文件位置。*/
    public String getConfig() {
        return config;
    };
    /**设置配置文件位置，此处可以设置null作为参数，但是在init时会引发空指针异常。*/
    public void setConfig(String config) {
        this.config = config;
    };
    /**获取Spring上下文对象，如果设置该属性则init方法将不会起作用。*/
    public AbstractApplicationContext getSpringContext() {
        return springContext;
    };
    /**获取Spring上下文对象，如果设置该属性则init方法将不会起作用。*/
    public void setSpringContext(AbstractApplicationContext springContext) {
        this.springContext = springContext;
    };
    /**初始化springContext对象。*/
    public void init(Config config) throws Exception {
        if (config != null)
            //考虑下WEB的情况
            if (config.getContext() instanceof ServletContext) {
                ServletContext sc = (ServletContext) config.getContext();
                Object context = sc.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");//获取spring上下文
                if (context instanceof AbstractApplicationContext == true)
                    this.springContext = (AbstractApplicationContext) context;
            };
        //
        if (this.springContext != null)
            return;
        //
        if (config != null) {
            Object configParam = config.getInitParameter("configFile");
            if (configParam != null)
                this.setConfig(configParam.toString());
        };
        //
        File configFile = new File(this.baseDir, this.config);
        if (configFile.isAbsolute() == false) {
            URL url = ClassLoader.getSystemResource(this.config);
            if (url != null)
                configFile = new File(URLDecoder.decode(url.getFile(), "utf-8"));
        }
        //
        if (configFile.exists() == false || configFile.canRead() == false)
            throw new IOException("配置文件[" + configFile.getAbsolutePath() + "]不存在，或者无法读取。");
        this.springContext = new FileSystemXmlApplicationContext(configFile.getAbsolutePath());
        this.springContext.refresh();
    };
    public ActionContext getActionContext() {
        return new StringContext(this.springContext);
    }
    public void setBaseDir(File baseDir) {
        if (baseDir.isAbsolute() == false)
            throw new FormatException("baseDir必须是一个绝对路径。");
        this.baseDir = baseDir;
    }
}