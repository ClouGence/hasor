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
package net.hasor.plugins.spring.factory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
/**
 *
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringFactoryBean implements FactoryBean, InitializingBean, //
        ApplicationContextAware, Module {
    //
    //
    protected static Logger logger = LoggerFactory.getLogger(Hasor.class);
    private AppContext         appContext;
    private ApplicationContext applicationContext;
    private String             config;
    private String             refProperties;
    private ArrayList<Module>  modules;
    //
    public String getConfig() {
        return config;
    }
    public void setConfig(String config) {
        this.config = config;
    }
    public String getRefProperties() {
        return refProperties;
    }
    public void setRefProperties(String refProperties) {
        this.refProperties = refProperties;
    }
    public ArrayList<Module> getModules() {
        return modules;
    }
    public void setModules(ArrayList<Module> modules) {
        this.modules = modules;
    }
    //
    //
    //
    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Override
    public final Object getObject() throws Exception {
        if (this.appContext == null) {
            throw new IllegalStateException("has not been initialized");
        }
        return this.appContext;
    }
    @Override
    public final Class<?> getObjectType() {
        return AppContext.class;
    }
    @Override
    public final boolean isSingleton() {
        return true;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        ArrayList<Module> moduleList = this.getModules();
        String config = this.getConfig();
        // - initData
        if (!StringUtils.isBlank(config)) {
            config = SystemPropertyUtils.resolvePlaceholders(config);
        }
        if (moduleList == null) {
            moduleList = new ArrayList<Module>();
        }
        // - AppContext
        moduleList.add(this);
        Module[] moduleArrays = moduleList.toArray(new Module[moduleList.size()]);
        Resource resource = null;
        if (StringUtils.isNotBlank(config)) {
            resource = this.applicationContext.getResource(config);
        }
        //
        PropertiesLoaderSupport propertiesLoaderSupport = null;
        if (this.applicationContext.containsBean(this.refProperties)) {
            Object obj = this.applicationContext.getBean(this.refProperties);
            if (obj instanceof PropertiesLoaderSupport) {
                propertiesLoaderSupport = (PropertiesLoaderSupport) obj;
            }
        }
        //
        this.appContext = createAppContext(this.applicationContext, resource, propertiesLoaderSupport, moduleArrays);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    protected AppContext createAppContext(ApplicationContext context, Resource resource, final PropertiesLoaderSupport envProperties, final Module... modules) throws Exception {
        //
        // .获取所有 Spring 的属性配置
        HashMap<String, String> envMap = new HashMap<String, String>();
        if (envProperties == null) {
            logger.info("not import any environment variables -> refProperties is null");
        } else {
            Properties props = null;
            try {
                Method mergeProperties = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties");
                Method convertProperties = PropertyResourceConfigurer.class.getDeclaredMethod("convertProperties", Properties.class);
                mergeProperties.setAccessible(true);
                convertProperties.setAccessible(true);
                //
                props = (Properties) mergeProperties.invoke(envProperties);
                convertProperties.invoke(envProperties, props);
            } catch (Exception e) {
                logger.error("import environment variables error -> " + e.getMessage(), e);
                throw ExceptionUtils.toRuntimeException(e);
            }
            //
            if (props == null) {
                logger.warn("not import any environment variables -> form Spring mergeProperties is null");
            } else {
                for (String keyStr : props.stringPropertyNames()) {
                    String keyVal = props.getProperty(keyStr);
                    envMap.put(keyStr, keyVal);
                }
            }
        }
        logger.info("import environment variables ,done. -> import size :" + envMap.size());
        //
        // .测试 Spring 的上下文是否为 WebApplicationContext
        boolean testSupportWeb = ResourcesUtils.getResourceAsStream("/org/springframework/web/context/WebApplicationContext.class") != null;
        if (testSupportWeb) {
            if (context instanceof WebApplicationContext) {
                testSupportWeb = true;
            } else {
                testSupportWeb = false;
            }
        }
        //
        // .主配置文件
        URI mainSettings = (resource == null) ? null : resource.getURI();
        ClassLoader loader = context.getClassLoader();
        //
        // .创建Context
        AppContext appContext = null;
        if (testSupportWeb) {
            ServletContext sc = ((WebApplicationContext) context).getServletContext();
            appContext = Hasor.create(sc)//
                    .setMainSettings(mainSettings)//
                    .setLoader(loader)//
                    .putAllData(envMap)//
                    .build(modules);//
        } else {
            appContext = Hasor.create()//
                    .setMainSettings(mainSettings)//
                    .setLoader(loader)//
                    .putAllData(envMap)//
                    .build(modules);//
        }
        logger.info("create AppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        return appContext;
    }
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
    }
}