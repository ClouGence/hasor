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
import javax.servlet.ServletContext;
import org.more.InvokeException;
import org.more.submit.Config;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * WebSpringBuilder类扩展了ClientSpringBuilder提供了web的支持，init方法传入configFile参数WebSpringBuilder类
 * 首先从ServletContext中查找Spring使用监听器装载的Spring上下文，如果找不到则使用FileSystemXmlApplicationContext创建它，
 * 同时参数configFile回被转换为相对站点目录。
 * <br/>Date : 2009-12-2
 * @author 赵永春
 */
public class WebSpringBuilder extends ClientSpringBuilder {
    //==================================================================================Constructor
    /** 使用Web形式构建More的ActionManager，如果在构造方法中没有指定任何参数则当ActionManager在初始化时候回用过init方法获取，SpringContext */
    public WebSpringBuilder() {}
    /** 该构造方法可以使开发人员通过指定ServletContext来获取SpringContext。如果在该构造方法中获取到了SpringContext则在init方法时将忽略获取SpringContext的过程。如果该方法中没有获取到SpringContext则回引发InvokeException异常*/
    public WebSpringBuilder(ServletContext sc) {
        this.springContext = this.getSpringContext(sc);//获取spring上下文,该方法会确保获取到对象，如果获取不到或者类型错误将会引发异常。
    }
    //==========================================================================================Job
    /**首先从ServletContext中查找Spring使用监听器装载的Spring上下文，如果找不到则使用FileSystemXmlApplicationContext创建它，同时参数configFile回被转换为相对站点目录。*/
    @Override
    public void init(Config config) throws InvokeException {
        this.config = config;
        System.out.println("init WebMoreBuilder...");
        ServletContext sc = (ServletContext) this.getConfig().getContext();
        if (this.springContext == null)
            this.springContext = this.getSpringContext(sc);//该方法会确保获取到对象，如果获取不到或者类型错误将会引发异常。
        if (this.springContext == null) {
            String configFile = sc.getRealPath(config.getInitParameter("configFile"));
            this.springContext = new FileSystemXmlApplicationContext(configFile);
        }
        System.out.println("init WebMoreBuilder OK");
    }
    /** 该方法负责从ServletContext中获取Spring工厂对象。 */
    private AbstractApplicationContext getSpringContext(ServletContext sc) {
        //获取spring上下文
        Object context = sc.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
        if (context == null)
            return null;
        else if (context instanceof AbstractApplicationContext == false)
            throw new InvokeException("无法读取org.springframework.web.context.WebApplicationContext.ROOT属性对象，该属性对象没有继承spring的AbstractApplicationContext抽象类");
        return (AbstractApplicationContext) context;
    }
}