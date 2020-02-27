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
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collections;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnSingleCandidate(AppContext.class)
public class WebHasorConfiguration extends AbstractTypeSupplierTools //
        implements ImportAware {
    private String filterPath  = "/*";
    private int    filterOrder = 0;

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
    }

    @Bean
    public FilterRegistrationBean<RuntimeFilter> hasorRuntimeFilter() {
        FilterRegistrationBean<RuntimeFilter> filterBean = //
                new FilterRegistrationBean<>(new RuntimeFilter());
        filterBean.setUrlPatterns(Collections.singletonList(this.filterPath));
        filterBean.setOrder(this.filterOrder);
        filterBean.setName(RuntimeFilter.class.getName());
        //filterBean.setAsyncSupported(true);
        return filterBean;
    }

    @Bean
    public ServletListenerRegistrationBean<RuntimeListener> hasorRuntimeListener(AppContext appContext) {
        ServletListenerRegistrationBean<RuntimeListener> listenerBean = //
                new ServletListenerRegistrationBean<>(new RuntimeListener(appContext));
        return listenerBean;
    }
}
