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
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.util.Collections;

@Configuration
public class HasorWebConfiguration extends HasorBasicConfiguration {
    @Override
    protected AppContext createAppContext(Object envObject, ApplicationContext applicationContext) {
        ServletContext sc = applicationContext.getBean(ServletContext.class);
        return super.createAppContext(sc, applicationContext);
    }

    @Bean
    public FilterRegistrationBean<RuntimeFilter> hasorRuntimeFilter(AppContext appContext) {
        FilterRegistrationBean<RuntimeFilter> filterBean = //
                new FilterRegistrationBean<>(new RuntimeFilter());
        filterBean.setUrlPatterns(Collections.singletonList("/*"));
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
