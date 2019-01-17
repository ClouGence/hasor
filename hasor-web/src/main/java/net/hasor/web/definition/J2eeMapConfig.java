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
package net.hasor.web.definition;
import net.hasor.core.Provider;
import net.hasor.utils.Iterators;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
/**
 * Abstract implementation for all servlet module bindings
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class J2eeMapConfig extends HashMap<String, String> implements FilterConfig, ServletConfig {
    private String                   resourceName;
    private Provider<ServletContext> servletContext;
    //
    public J2eeMapConfig(String resourceName, Map<String, String> initParams, Provider<ServletContext> servletContext) {
        this.resourceName = resourceName;
        this.servletContext = servletContext;
        if (initParams != null) {
            this.putAll(initParams);
        }
    }
    //
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
        if (this.servletContext != null) {
            return this.servletContext.get();
        }
        return null;
    }
    @Override
    public String getInitParameter(String name) {
        return this.get(name);
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        return Iterators.asEnumeration(J2eeMapConfig.this.keySet().iterator());
    }
}
