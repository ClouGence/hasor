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
import net.hasor.spring.beans.RuntimeFilter2Controller;
import net.hasor.spring.beans.RuntimeFilter2Interceptor;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
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
    private static final Logger     logger      = LoggerFactory.getLogger(WebHasorConfiguration.class);
    @Autowired
    private              AppContext appContext;
    private              String     filterPath  = "/*";
    private              int        filterOrder = 0;
    private              WorkAt     filterWorkAt;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Objects.requireNonNull(this.appContext, "AppContext is not inject.");
        if (this.filterWorkAt != WorkAt.Interceptor) {
            return;
        }
        try {
            RuntimeFilter runtimeFilter = new RuntimeFilter(appContext);
            runtimeFilter.init(new OneConfig("", () -> appContext));
            RuntimeFilter2Interceptor interceptor = new RuntimeFilter2Interceptor(runtimeFilter);
            //
            registry.addInterceptor(interceptor)//
                    .addPathPatterns(evalFilterPath(this.filterPath))//
                    .order(this.filterOrder);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnClass(name = "net.hasor.web.startup.RuntimeListener")
    public Object addController(RequestMappingHandlerMapping requestMappingHandlerMapping) throws Exception {
        Objects.requireNonNull(this.appContext, "AppContext is not inject.");
        RuntimeFilter runtimeFilter = new RuntimeFilter(this.appContext);
        String filterPath = evalFilterPath(this.filterPath);
        //
        switch (this.filterWorkAt) {
            case Filter: {
                FilterRegistrationBean<Filter> filterBean = new FilterRegistrationBean<>(runtimeFilter);
                filterBean.setUrlPatterns(Collections.singletonList(filterPath));
                filterBean.setOrder(this.filterOrder);
                filterBean.setName(RuntimeFilter.class.getName());
                return filterBean;
            }
            case Controller: {
                RuntimeFilter2Controller handler = new RuntimeFilter2Controller(runtimeFilter, appContext);
                Method handlerMethod = RuntimeFilter2Controller.class.getMethod("doHandler", HttpServletRequest.class, HttpServletResponse.class);
                RequestMappingInfo info = RequestMappingInfo.paths(filterPath).methods(RequestMethod.values()).build();
                requestMappingHandlerMapping.registerMapping(info, handler, handlerMethod);
            }
        }
        return new Object();
    }

    private static String evalFilterPath(String filterPath) {
        if (filterPath.endsWith("/*")) {
            filterPath = filterPath.substring(0, filterPath.length() - 2) + "/**";
        }
        return filterPath;
    }
}
