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
package net.hasor.plugins.jfinal;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import net.hasor.core.*;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebHasor;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
/**
 * Jfinal 插件.
 *
 * @version : 2016-11-03
 * @author 赵永春 (zyc@byshell.org)
 */
public class HasorPlugin implements IPlugin {
    private final JFinal          jFinal;
    private       WebAppContext   webAppContext;
    private final RuntimeListener listener;
    private final RuntimeFilter   rootFilter;
    //
    //
    /***/
    public HasorPlugin(final JFinal jFinal) {
        this(jFinal, TemplateAppContext.DefaultSettings, null);
    }
    /***/
    public HasorPlugin(final JFinal jFinal, String mainSettings) {
        this(jFinal, mainSettings, null);
    }
    /***/
    public HasorPlugin(final JFinal jFinal, Module module) {
        this(jFinal, TemplateAppContext.DefaultSettings, module);
    }
    /***/
    public HasorPlugin(final JFinal jFinal, final String mainSettings, final Module module) {
        this.jFinal = Hasor.assertIsNotNull(jFinal, "jFinal Context is null.");
        this.listener = new RuntimeListener() {
            protected WebAppContext createAppContext(ServletContext sc, Module startModule) throws Throwable {
                return newAppContext(jFinal, mainSettings, new MergeModule(Arrays.asList(module, startModule)));// 创建WebAppContext
            }
        };
        this.rootFilter = new RuntimeFilter();
    }
    //
    public boolean start() {
        try {
            doStart();
            return true;
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    private void doStart() throws ServletException {
        /** 启动 Hasor 框架*/
        this.listener.contextInitialized(new ServletContextEvent(this.jFinal.getServletContext()));
        this.webAppContext = RuntimeListener.getAppContext(this.jFinal.getServletContext());
        InnerMap envProp = this.webAppContext.getInstance(InnerMap.class);
        this.rootFilter.init(new InnerFilterConfig(this.jFinal, envProp));
        //
        /** Hasor 框架停止*/
        Hasor.pushShutdownListener(this.webAppContext.getEnvironment(), new EventListener<Object>() {
            public void onEvent(String event, Object eventData) throws Throwable {
                stop();
            }
        });
    }
    public boolean stop() {
        this.rootFilter.destroy();
        this.listener.contextDestroyed(new ServletContextEvent(jFinal.getServletContext()));
        return true;
    }
    protected WebAppContext newAppContext(final JFinal jFinal, String mainSettings, final Module startModule) throws Throwable {
        //
        // .JFinal 的属性文件(如果没有则为Null)
        InnerMap envProp = null;
        try {
            Properties jfProp = PropKit.getProp().getProperties();
            envProp = new InnerMap();
            if (jfProp != null) {
                for (Map.Entry<Object, Object> ent : jfProp.entrySet()) {
                    Object entKey = ent.getKey();
                    Object entValue = ent.getValue();
                    envProp.put(entKey.toString(), entValue.toString());
                }
            }
        } catch (IllegalStateException e) { /**/ }
        //
        // .初始化 Hasor WebAppContext，并且将 JFinal 的配置作为 Hasor 的环境变量
        final InnerMap finalEnvProp = envProp;
        mainSettings = StringUtils.isBlank(mainSettings) ? TemplateAppContext.DefaultSettings : mainSettings;
        return WebHasor.createWebAppContext(jFinal.getServletContext(), mainSettings, envProp, new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                // .注册类型
                apiBinder.bindType(InnerMap.class).toInstance((finalEnvProp != null) ? finalEnvProp : new InnerMap());
                apiBinder.bindType(JFinal.class).toInstance(jFinal);
                apiBinder.bindType(RuntimeFilter.class).toProvider(new Provider<RuntimeFilter>() {
                    public RuntimeFilter get() {
                        return rootFilter;
                    }
                });
                // .容器的StartModule
                if (startModule != null) {
                    apiBinder.installModule(startModule);
                }
            }
        });
    }
}