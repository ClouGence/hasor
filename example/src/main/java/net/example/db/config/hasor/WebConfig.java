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
package net.example.db.config.hasor;
import net.hasor.core.AppContext;
import net.hasor.utils.ExceptionUtils;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.startup.RuntimeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 三种方式启动 Hasor（三选一）
 *  - Filter 模式
 *  - Interceptor 模式
 *  - Controller 模式
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private       AppContext appContext;
    private final String[]   datawayWorkPath = new String[] { "/*" };
    private final WorkAt     workAt          = WorkAt.Controller;

    // - Filter 模式
    @Bean
    public FilterRegistrationBean<Filter> registrationFilter() {
        Filter filter = null;
        if (this.workAt == WorkAt.Filter) {
            Objects.requireNonNull(this.appContext, "appContext is null.");
            filter = new RuntimeFilter(this.appContext);
        } else {
            filter = (request, response, chain) -> chain.doFilter(request, response);
        }
        //
        FilterRegistrationBean<Filter> filterBean = new FilterRegistrationBean<>(filter);
        filterBean.setUrlPatterns(Arrays.asList(datawayWorkPath));
        filterBean.setName(RuntimeFilter.class.getName());
        return filterBean;
    }

    // - Interceptor 模式
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (this.workAt == WorkAt.Interceptor) {
            Objects.requireNonNull(this.appContext, "appContext is null.");
            try {
                RuntimeFilter runtimeFilter = new RuntimeFilter(appContext);
                runtimeFilter.init(new OneConfig("", () -> appContext));
                Filter2Interceptor interceptor = new Filter2Interceptor(runtimeFilter);
                //
                registry.addInterceptor(interceptor).addPathPatterns(evalFilterPath(this.datawayWorkPath)).order(0);
            } catch (Exception e) {
                throw ExceptionUtils.toRuntime(e);
            }
        }
    }

    // - Controller 模式
    @Bean
    public Object addController(RequestMappingHandlerMapping handlerMapping) throws Exception {
        if (this.workAt == WorkAt.Controller) {
            Objects.requireNonNull(this.appContext, "appContext is null.");
            RuntimeFilter filter = new RuntimeFilter(this.appContext);
            Filter2Controller handler = new Filter2Controller(filter, appContext);
            //
            Method handlerMethod = Filter2Controller.class.getMethod("doHandler", HttpServletRequest.class, HttpServletResponse.class);
            RequestMappingInfo info = RequestMappingInfo.paths(evalFilterPath(this.datawayWorkPath)).methods(RequestMethod.values()).build();
            ((AbstractHandlerMethodMapping<RequestMappingInfo>) handlerMapping).registerMapping(info, handler, handlerMethod);
        }
        return new Object();
    }

    private static String[] evalFilterPath(String[] filterPath) {
        String[] filterPathArray = new String[filterPath.length];
        for (int i = 0; i < filterPath.length; i++) {
            String tmp = filterPath[i];
            if (tmp.endsWith("/*")) {
                tmp = tmp.substring(0, tmp.length() - 2) + "/**";
            }
            filterPathArray[i] = tmp;
        }
        return filterPathArray;
    }

    /** Hasor 的请求拦截器在 springwebmvc 中工作方式 */
    public static enum WorkAt {
        /** 过滤器模式，以 web filter 的方式进行集成 */
        Filter,
        /** 拦截器模式，以 springwebmvc 的拦截器方式进行集成 */
        Interceptor,
        /** 控制器模式，以 springwebmvc 的 Controller 方式进行集成 */
        Controller
    }

    /** Filter to Controller */
    public static class Filter2Controller {
        private final RuntimeFilter runtimeFilter;

        public Filter2Controller(RuntimeFilter runtimeFilter, AppContext appContext) throws ServletException {
            this.runtimeFilter = runtimeFilter;
            runtimeFilter.init(new OneConfig("", () -> appContext));
        }

        public void doHandler(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            this.runtimeFilter.doFilter(request, response, (req, res) -> {
                HttpServletResponse httpRes = (HttpServletResponse) res;
                if (!httpRes.isCommitted()) {
                    httpRes.sendError(404, "Not Found Resource.");
                }
            });
        }
    }

    /** Filter to Interceptor */
    public static class Filter2Interceptor implements AsyncHandlerInterceptor {
        private final RuntimeFilter runtimeFilter;

        public Filter2Interceptor(RuntimeFilter runtimeFilter) {
            this.runtimeFilter = runtimeFilter;
        }

        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            this.runtimeFilter.doFilter(request, response, (req, res) -> {
                atomicBoolean.set(true);
            });
            return atomicBoolean.get();
        }
    }
}