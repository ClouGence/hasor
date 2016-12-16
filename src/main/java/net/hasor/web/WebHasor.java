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
package net.hasor.web;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.StatusAppContext;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.web.env.WebStandardEnvironment;
import org.more.util.ExceptionUtils;

import javax.servlet.ServletContext;
import java.net.URI;
import java.util.Map;
/**
 * Hasor 基础工具包。
 * @version : 2016-11-03
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class WebHasor extends Hasor {
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createWebAppContext(ServletContext servletContext, Module... modules) {
        return createWebAppContext(servletContext, TemplateAppContext.DefaultSettings, null, null, modules);
    }
    public static AppContext createWebAppContext(ServletContext servletContext, String mainSettings, Map<String, String> envMap, ClassLoader loader, Module... modules) {
        logger.info("create WebAppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        try {
            WebStandardEnvironment env = new WebStandardEnvironment(servletContext, mainSettings, envMap, loader);
            AppContext appContext = new StatusAppContext<BeanContainer>(env, new BeanContainer());
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    public static AppContext createWebAppContext(ServletContext servletContext, URI mainSettings, Map<String, String> envMap, ClassLoader loader, Module... modules) {
        logger.info("create WebAppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        try {
            WebStandardEnvironment env = new WebStandardEnvironment(servletContext, mainSettings, envMap, loader);
            AppContext appContext = new StatusAppContext<BeanContainer>(env, new BeanContainer());
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}