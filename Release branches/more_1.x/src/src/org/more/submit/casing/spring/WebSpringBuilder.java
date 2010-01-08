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
import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.more.InvokeException;
import org.more.submit.Config;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * WebSpringBuilder类扩展了ClientSpringBuilder提供了web的支持，该类所使用的configFile参数是一个相对于站点的web相对路径。<br/>
 * 如果没有指定configFile参数，则configFile默认将表示为“/WEB-INF/applicationContext.xml”配置文件。<br/>
 * 首先，如果配置了spring的监听器WebSpringBuilder则会自动到ServletContext中查找AbstractApplicationContext类型对象<br/>
 * 其次，如果传递了beanFactory参数则使用beanFactory参数所指定的AbstractApplicationContext类型对象。
 * 然后，会查找configFile参数配置来装载Spring配置文件。
 * @version 2010-1-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebSpringBuilder extends ClientSpringBuilder implements Config {
    public static final String Default_ConfigXML = "/WEB-INF/applicationContext.xml";
    //==================================================================================Constructor
    /** WebSpringBuilder类扩展了ClientSpringBuilder提供了web的支持，该类将configFile参数所表示的路径从web相对路径转换为绝对路径。*/
    public WebSpringBuilder() throws IOException {
        super(false);
    }
    //==========================================================================================Abs
    /**总体初始化入口*/
    @Override
    public void init(Config config) throws Exception {
        //优先级：spring监听器->beanFactory参数->configFile参数
        System.out.println("init WebSpringBuilder...");
        ServletContext sc = (ServletContext) config.getContext();
        this.springContext = this.getSpringContext(sc);
        if (this.springContext != null) {
            this.config = config;
            return;
        } else
            super.init(this.getWebSpringBuilderConfig(config));
        System.out.println("init WebSpringBuilder OK");
    }
    /**创建XmlWebApplicationContext对象。*/
    @Override
    protected void init(File configFile) throws IOException {
        if (configFile.exists() == false || configFile.canRead() == false)
            throw new IOException("配置文件[" + configFile.getAbsolutePath() + "]不存在，或者无法读取。");
        FileSystemXmlApplicationContext webApp = new FileSystemXmlApplicationContext();
        //webApp.setServletContext((ServletContext) this.config.getContext());
        webApp.setConfigLocation(configFile.getAbsolutePath());
        webApp.refresh();
        this.springContext = webApp;
    }
    /**负责从ServletContext中获取Spring工厂对象。 */
    private AbstractApplicationContext getSpringContext(ServletContext sc) {
        //获取spring上下文
        Object context = sc.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
        if (context == null)
            return null;
        else if (context instanceof AbstractApplicationContext == false)
            throw new InvokeException("无法读取org.springframework.web.context.WebApplicationContext.ROOT属性对象，该属性对象没有继承spring的AbstractApplicationContext抽象类");
        return (AbstractApplicationContext) context;
    }
    //=========================================================================================Impl
    private Config propxyConfig;
    protected Config getWebSpringBuilderConfig(Config config) {
        this.propxyConfig = config;
        return this;
    }
    /**负责处理beanFactory和configFile参数*/
    @Override
    public Object getInitParameter(String name) {
        if ("configFile".equals(name) == true) {
            ServletContext sc = (ServletContext) this.propxyConfig.getContext();
            Object obj = this.propxyConfig.getInitParameter(name);
            if (obj == null)
                return sc.getRealPath(WebSpringBuilder.Default_ConfigXML);
            else
                return sc.getRealPath(obj.toString());
        } else
            return this.propxyConfig.getInitParameter(name);
    }
    @Override
    public Object getContext() {
        return this.propxyConfig.getContext();
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        return this.propxyConfig.getInitParameterNames();
    }
}