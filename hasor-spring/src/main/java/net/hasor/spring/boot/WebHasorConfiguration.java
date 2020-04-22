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
import net.hasor.spring.beans.AbstractTypeSupplierTools;
import net.hasor.utils.ExceptionUtils;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Spring Boot 下 Web 环境配置。
 * @version : 2020-02-27
 * @author 赵永春 (zyc@hasor.net)
 */
@Configuration(proxyBeanMethods = false)
public class WebHasorConfiguration extends AbstractTypeSupplierTools//
        implements ImportAware, WebMvcConfigurer {
    private static Logger     logger      = LoggerFactory.getLogger(WebHasorConfiguration.class);
    @Autowired
    private        AppContext appContext;
    private        String     filterPath  = "/*";
    private        int        filterOrder = 0;
    private        WorkAt     filterWorkAt;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Set<String> types = importMetadata.getAnnotationTypes();
        if (!types.contains(EnableHasorWeb.class.getName())) {
            return;
        }
        // 得到 EnableHasorWeb
        EnableHasorWeb enableHasor = null;
        try {
            String className = importMetadata.getClassName();
            Class<?> loadClass = this.getSpringClassLoader().loadClass(className);
            enableHasor = loadClass.getAnnotation(EnableHasorWeb.class);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        this.filterPath = enableHasor.path();
        this.filterOrder = 0;
        this.filterWorkAt = enableHasor.at();
        //
        logger.info("@EnableHasorWeb -> filterPath='" + this.filterPath + "', filterOrder='" + this.filterOrder + "', filterWorkAt='" + this.filterWorkAt + "'");
    }

    @Bean
    @ConditionalOnClass(name = "net.hasor.web.startup.RuntimeListener")
    public ServletListenerRegistrationBean<?> hasorRuntimeListener() {
        Objects.requireNonNull(this.appContext, "AppContext is not inject.");
        return new ServletListenerRegistrationBean<>(new RuntimeListener(this.appContext));
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnClass(name = "net.hasor.web.startup.RuntimeFilter")
    public FilterRegistrationBean<?> hasorRuntimeFilter() {
        Filter runtimeFilter = null;
        if (this.filterWorkAt == WorkAt.Filter) {
            runtimeFilter = new RuntimeFilter();    // 过滤器模式
        } else {
            runtimeFilter = new EmptyFilter();      // 拦截器模式
        }
        //
        FilterRegistrationBean<Filter> filterBean = //
                new FilterRegistrationBean<>(runtimeFilter);
        filterBean.setUrlPatterns(Collections.singletonList(this.filterPath));
        filterBean.setOrder(this.filterOrder);
        filterBean.setName(RuntimeFilter.class.getName());
        return filterBean;
    }

    /** 拦截器模式下，添加Spring 拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (this.filterWorkAt != WorkAt.Interceptor) {
            return;
        }
        try {
            RuntimeFilter runtimeFilter = new RuntimeFilter(appContext);
            runtimeFilter.init(new OneConfig("", () -> appContext));
            Filter2Interceptor interceptor = new Filter2Interceptor(runtimeFilter);
            //
            String filterPath = this.filterPath;
            if (filterPath.endsWith("/*")) {
                filterPath = filterPath.substring(0, filterPath.length() - 2) + "/**";
            }
            //
            registry.addInterceptor(interceptor)//
                    .addPathPatterns(filterPath)//
                    .order(this.filterOrder);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}
