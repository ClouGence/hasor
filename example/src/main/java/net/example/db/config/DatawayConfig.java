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
package net.example.db.config;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
@Configuration(proxyBeanMethods = false)
public class DatawayConfig {
    // 把 spring 的配置文件导入到 hasor.
    public Hasor importConfig(Hasor loaderWith, ApplicationContext applicationContext) {
        for (PropertySource<?> propertySource : ((StandardEnvironment) applicationContext.getEnvironment()).getPropertySources()) {
            if ("systemProperties".equalsIgnoreCase(propertySource.getName())) {
                continue;// this propertySource in Hasor has same one
            }
            if ("systemEnvironment".equalsIgnoreCase(propertySource.getName())) {
                continue;// this propertySource in Hasor has same one
            }
            Object source = propertySource.getSource();
            if (source instanceof Map) {
                ((Map<?, ?>) source).forEach((BiConsumer<Object, Object>) (key, value) -> {
                    if (key != null && value != null) {
                        loaderWith.addVariable(key.toString(), value.toString());
                    }
                });
            }
        }
        return loaderWith;
    }

    @Bean(destroyMethod = "shutdown")
    public AppContext appContext(ApplicationContext applicationContext) {
        if (!(applicationContext instanceof WebApplicationContext)) {
            throw new IllegalStateException("miss ServletContext.");
        }
        //
        ServletContext parent = ((WebApplicationContext) applicationContext).getServletContext();
        ClassLoader classLoader = applicationContext.getClassLoader();
        //
        DatawayModule initModule = applicationContext.getBean(DatawayModule.class);
        Hasor loaderWith = Hasor.create(parent).parentClassLoaderWith(classLoader);
        return importConfig(loaderWith, applicationContext).build(initModule);
    }

    @Bean()
    public ServletListenerRegistrationBean<RuntimeListener> registrationListener(AppContext appContext) {
        Objects.requireNonNull(appContext, "appContext is null.");
        RuntimeListener listener = new RuntimeListener(appContext);
        return new ServletListenerRegistrationBean<>(listener);
    }

    @Bean
    public FilterRegistrationBean<Filter> registrationFilter(AppContext appContext) {
        Objects.requireNonNull(appContext, "appContext is null.");
        RuntimeFilter filter = new RuntimeFilter(appContext);
        FilterRegistrationBean<Filter> filterBean = new FilterRegistrationBean<>(filter);
        filterBean.setUrlPatterns(Collections.singletonList("/*"));
        filterBean.setName(RuntimeFilter.class.getName());
        return filterBean;
    }
}