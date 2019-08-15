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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.utils.Iterators;
import net.hasor.web.InvokerConfig;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Abstract implementation for all servlet module bindings
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class OneConfig extends HashMap<String, String> implements FilterConfig, ServletConfig, InvokerConfig {
    private String               resourceName;
    private Supplier<AppContext> appContext;

    public OneConfig(String resourceName, Supplier<AppContext> appContext) {
        this.resourceName = resourceName;
        this.appContext = appContext;
    }

    public OneConfig(String resourceName, Map<String, String> initParams, Supplier<AppContext> appContext) {
        this.resourceName = resourceName;
        this.appContext = appContext;
        if (initParams != null) {
            this.putAll(initParams);
        }
    }

    public OneConfig(FilterConfig config, Supplier<AppContext> appContext) {
        this.resourceName = config.getFilterName();
        this.appContext = appContext;
        this.putConfig(config, true);
    }

    public OneConfig(ServletConfig config, Supplier<AppContext> appContext) {
        this.resourceName = config.getServletName();
        this.appContext = appContext;
        this.putConfig(config, true);
    }

    public OneConfig(String resourceName, InvokerConfig config, Supplier<AppContext> appContext) {
        this.resourceName = resourceName;
        this.appContext = appContext;
        this.putConfig(config, true);
    }

    public void putConfig(FilterConfig config, boolean overwrite) {
        Enumeration<?> names = config.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                this.computeIfAbsent(name, s -> overwrite ? config.getInitParameter(name) : s);
            }
        }
    }

    public void putConfig(ServletConfig config, boolean overwrite) {
        Enumeration<?> names = config.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                this.computeIfAbsent(name, s -> overwrite ? config.getInitParameter(name) : s);
            }
        }
    }

    public void putConfig(InvokerConfig config, boolean overwrite) {
        Enumeration<?> names = config.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                this.computeIfAbsent(name, s -> overwrite ? config.getInitParameter(name) : s);
            }
        }
    }

    @Override
    public String getFilterName() {
        return this.resourceName;
    }

    @Override
    public String getServletName() {
        return this.resourceName;
    }

    @Override
    public ServletContext getServletContext() {
        if (this.appContext != null) {
            return this.appContext.get().getInstance(ServletContext.class);
        }
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return this.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Iterators.asEnumeration(OneConfig.this.keySet().iterator());
    }

    @Override
    public AppContext getAppContext() {
        return this.appContext.get();
    }
}
