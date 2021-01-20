/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.beans;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.utils.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import java.util.function.Predicate;

/**
 * 在 Spring 中创建 Hasor 环境 使用。
 * @version : 2020年02月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ContextFactoryBean extends AbstractEnvironmentAware implements FactoryBean<Object>, InitializingBean, DisposableBean,//
        Module, ApplicationContextAware, EnvironmentAware {
    protected static Logger             logger             = LoggerFactory.getLogger(Hasor.class);
    private          AppContext         realAppContext     = null;
    private          ApplicationContext applicationContext = null;
    private          String[]           loadModules        = null;
    private          String[]           scanPackages       = null;
    private final    BuildConfig        buildConfig        = new BuildConfig();
    // ------------------------------------------------------------------------ getter/setter

    public void setMainConfig(String mainConfig) {
        this.buildConfig.mainConfig = mainConfig;
    }

    public void setRefProperties(Properties refProperties) {
        this.buildConfig.refProperties = refProperties;
    }

    public void setUseProperties(boolean useProperties) {
        this.buildConfig.useProperties = useProperties;
    }

    public void setCustomProperties(Map<Object, Object> customProperties) {
        if (this.buildConfig.customProperties == null) {
            this.buildConfig.customProperties = new HashMap<>();
        }
        this.buildConfig.customProperties.putAll(customProperties);
    }

    public void setLoadModules(String[] loadModules) {
        this.loadModules = loadModules;
    }

    public void setScanPackages(String[] scanPackages) {
        this.scanPackages = scanPackages;
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.buildConfig.envProperties = super.setupEnvironment(environment);
    }

    // ------------------------------------------------------------------------ promise FactoryBean.
    @Override
    public final Object getObject() throws Exception {
        return this.realAppContext;
    }

    @Override
    public final Class<?> getObjectType() {
        return AppContext.class;
    }

    @Override
    public final boolean isSingleton() {
        return true;
    }

    // ------------------------------------------------------------------------ promise InitializingBean and DisposableBean.
    @Override
    public final void afterPropertiesSet() throws Exception {
        // create Hasor, if WebApplicationContext then get ServletContext
        Object parentObject = null;
        if (ResourcesUtils.getResourceAsStream("/org/springframework/web/context/WebApplicationContext.class") != null) {
            if (this.applicationContext instanceof WebApplicationContext) {
                parentObject = ((WebApplicationContext) this.applicationContext).getServletContext();
            }
        }
        //
        Set<Class<?>> needCheckRepeat = new HashSet<>();
        if (this.loadModules != null) {
            for (String name : this.loadModules) {
                needCheckRepeat.add(this.applicationContext.getType(name));
                this.buildConfig.loadModules.add((Module) this.applicationContext.getBean(name));
            }
        }
        //
        if (this.scanPackages != null && this.scanPackages.length > 0) {
            Predicate<Class<?>> classPredicate = needCheckRepeat.isEmpty() ? Matchers.anyClass() : Matchers.anyClassExcludes(needCheckRepeat);
            AutoScanPackagesModule autoScanModule = new AutoScanPackagesModule(this.scanPackages, classPredicate);
            autoScanModule.setApplicationContext(Objects.requireNonNull(this.applicationContext));
            this.buildConfig.loadModules.add(autoScanModule);
        }
        //
        this.realAppContext = this.buildConfig.build(parentObject, this.applicationContext).build(this);
        logger.info("hasor Spring factory inited.");
    }

    @Override
    public void loadModule(ApiBinder apiBinder) {
        apiBinder.bindType(ApplicationContext.class).toInstance(this.applicationContext);
    }

    @Override
    public final void destroy() {
        if (this.realAppContext != null) {
            this.realAppContext.shutdown();
        }
    }
}
