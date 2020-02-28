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
package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.core.Module;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.spring.beans.AbstractTypeSupplierTools;
import net.hasor.spring.beans.AutoScanPackagesModule;
import net.hasor.spring.beans.BuildConfig;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Spring Boot 启动入口
 * @version : 2020年02月27日
 * @author 赵永春 (zyc@hasor.net)
 */
@Configuration(proxyBeanMethods = false)
public class BasicHasorConfiguration extends AbstractTypeSupplierTools implements ImportAware {
    @Override
    public final void setImportMetadata(AnnotationMetadata importMetadata) {
        BuildConfig buildConfig = getBuildConfig();
        ApplicationContext applicationContext = getApplicationContext();
        Set<String> types = importMetadata.getAnnotationTypes();
        if (!types.contains(EnableHasor.class.getName())) {
            return;
        }
        // 得到 EnableHasor
        EnableHasor enableHasor = null;
        try {
            String className = importMetadata.getClassName();
            ClassLoader classLoader = applicationContext.getClassLoader();
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            Class<?> loadClass = classLoader.loadClass(className);
            enableHasor = loadClass.getAnnotation(EnableHasor.class);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        // 处理mainConfig
        buildConfig.mainConfig = enableHasor.mainConfig();
        // 处理useProperties
        buildConfig.useProperties = enableHasor.useProperties();
        // 处理startWith
        for (Class<? extends Module> startWith : enableHasor.startWith()) {
            if (startWith.getAnnotation(Component.class) != null) {
                buildConfig.loadModules.add(applicationContext.getBean(startWith));
            } else {
                try {
                    buildConfig.loadModules.add(startWith.newInstance());
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        }
        // 把Spring 中所有标记了 @DimModule 的 Module，捞进来。
        Set<Class<?>> needCheckRepeat = new HashSet<>(Arrays.asList(enableHasor.startWith()));
        for (String name : applicationContext.getBeanDefinitionNames()) {
            Class<?> type = applicationContext.getType(name);
            if (type == null || needCheckRepeat.contains(type)) {
                continue;
            }
            if (Module.class.isAssignableFrom(type) && type.getAnnotation(DimModule.class) != null) {
                needCheckRepeat.add(type);
                buildConfig.loadModules.add((Module) applicationContext.getBean(name));
            }
        }
        //
        // 处理scanPackages
        if (enableHasor.scanPackages().length != 0) {
            AutoScanPackagesModule autoScanModule = new AutoScanPackagesModule(enableHasor.scanPackages(), Matchers.anyClassExcludes(needCheckRepeat));
            autoScanModule.setApplicationContext(Objects.requireNonNull(applicationContext));
            buildConfig.loadModules.add(autoScanModule);
        }
        // 处理customProperties
        Property[] customProperties = enableHasor.customProperties();
        for (Property property : customProperties) {
            String name = property.name();
            if (StringUtils.isNotBlank(name)) {
                buildConfig.customProperties.put(name, property.value());
            }
        }
    }

    @Bean
    @ConditionalOnNotWebApplication
    public AppContext normalAppContext(ApplicationContext applicationContext) {
        return this.createAppContext(null, applicationContext);
    }

    @Bean
    @ConditionalOnWebApplication
    public AppContext webAppContext(ApplicationContext applicationContext) {
        ServletContext parent = null;
        if (applicationContext instanceof WebApplicationContext) {
            parent = ((WebApplicationContext) applicationContext).getServletContext();
        } else {
            throw new IllegalStateException("miss ServletContext.");
        }
        return this.createAppContext(parent, applicationContext);
    }

    protected AppContext createAppContext(Object parentObject, ApplicationContext applicationContext) {
        //
        try {
            return this.getBuildConfig().build(parentObject, applicationContext).build(apiBinder -> {
                apiBinder.bindType(ApplicationContext.class).toInstance(applicationContext);
            });
        } catch (IOException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}
