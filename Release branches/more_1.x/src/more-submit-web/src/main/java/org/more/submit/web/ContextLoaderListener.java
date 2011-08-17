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
package org.more.submit.web;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.more.core.error.InitializationException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.context.AbstractDefineResource;
import org.more.hypha.context.app.DefaultApplicationContext;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.submit.SubmitService;
import org.more.util.ResourcesUtil;
/**
 * 该类的目的是负责装载{@link ApplicationContext}接口对象，并且将该对象放置在ServletContext中。
 * hypha-configs参数会给定配置文件位置，如果没有指定则他会寻找classpath目录下的“*-hypha-config.xml”
 * @version : 2011-7-19
 * @author 赵永春 (zyc@byshell.org)
 */
public class ContextLoaderListener implements ServletContextListener {
    public static final String ContextName = "org.more.hypha.ROOT";
    private ApplicationContext context     = null;
    public void contextDestroyed(ServletContextEvent event) {
        if (this.context != null)
            this.context.destroy();//销毁context
    };
    public void contextInitialized(ServletContextEvent event) {
        try {
            AbstractDefineResource resource = this.createDefineResource(event);
            this.context = this.createContext(event, resource);
        } catch (Throwable e) {
            throw new InitializationException(e);
        }
        ServletContext sc = event.getServletContext();
        sc.setAttribute(ContextName, this.context);
        //设置参数
        Enumeration<?> enums = sc.getInitParameterNames();
        while (enums.hasMoreElements()) {
            String name = (String) enums.nextElement();
            this.context.setAttribute(name, sc.getInitParameter(name));
        }
        //2.设置必要属性
        String attName = SubmitService.class.getName();
        if (this.context.getAttribute(attName) == null)
            this.context.setAttribute(attName, WebSubmitService.class.getName());
        if (this.context.getAttribute("rootPath") == null)
            this.context.setAttribute("rootPath", sc.getRealPath(""));
        this.context.init();//初始化context
    };
    protected ApplicationContext createContext(ServletContextEvent event, AbstractDefineResource resource) throws Throwable {
        DefaultApplicationContext appContext = new DefaultApplicationContext(resource);
        appContext.setContextObject(event.getServletContext());
        return appContext;
    };
    protected AbstractDefineResource createDefineResource(ServletContextEvent event) throws Throwable {
        final XmlDefineResource xdr = new XmlDefineResource();
        //
        String configs = event.getServletContext().getInitParameter("hypha-configs"); //TODO 装载xml
        if (configs != null) {
            String[] cs = configs.split(";");
            for (String c : cs)
                xdr.addSource(c);
        } else {
            List<URL> urls = ResourcesUtil.getResources("hypha-config.xml");
            for (URL url : urls)
                xdr.addSource(url);
        }
        xdr.loadDefine();
        return xdr;
    }
}