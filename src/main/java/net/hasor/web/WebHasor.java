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
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.web.context.WebTemplateAppContext;
import net.hasor.web.env.WebStandardEnvironment;
import org.more.util.ExceptionUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URI;
import java.util.Map;
/**
 * Hasor 基础工具包。
 * @version : 2016-11-03
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class WebHasor extends Hasor {
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext) {
        return WebHasor.createWebAppContext(servletContext, TemplateAppContext.DefaultSettings, null, null, new Module[0]);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, Module... modules) {
        return WebHasor.createWebAppContext(servletContext, TemplateAppContext.DefaultSettings, null, null, modules);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, File mainSettings) {
        return WebHasor.createWebAppContext(servletContext, mainSettings, null, null, new Module[0]);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, String mainSettings) {
        return WebHasor.createWebAppContext(servletContext, mainSettings, null, null, new Module[0]);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, URI mainSettings) {
        return WebHasor.createWebAppContext(servletContext, mainSettings, null, null, new Module[0]);
    }
    //
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, File mainSettings, Module... modules) {
        return WebHasor.createWebAppContext(servletContext, mainSettings, null, null, modules);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, String mainSettings, Module... modules) {
        return WebHasor.createWebAppContext(servletContext, mainSettings, null, null, modules);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, URI mainSettings, Module... modules) {
        return WebHasor.createWebAppContext(servletContext, mainSettings, null, null, modules);
    }
    //
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, File mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader, Module... modules) {
        return WebHasor.createWebAppContext(servletContext, mainSettings.toURI(), loadEnvConfig, loader, modules);
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, String mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader, Module... modules) {
        logger.info("create WebAppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        try {
            WebStandardEnvironment env = new WebStandardEnvironment(servletContext, mainSettings, loadEnvConfig, loader);
            WebTemplateAppContext appContext = new WebTemplateAppContext<BeanContainer>(env, new BeanContainer());
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    /**用简易的方式创建{@link WebAppContext}容器。*/
    public static WebAppContext createWebAppContext(ServletContext servletContext, URI mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader, Module... modules) {
        logger.info("create WebAppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        try {
            WebStandardEnvironment env = new WebStandardEnvironment(servletContext, mainSettings, loadEnvConfig, loader);
            WebTemplateAppContext appContext = new WebTemplateAppContext<BeanContainer>(env, new BeanContainer());
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}