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
package org.platform.runtime.config;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.more.core.global.Global;
import org.platform.api.context.Config;
/**
 * ServletContextµΩContextConfigµƒ«≈
 * @version : 2013-4-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class PlatformConfig implements Config {
    private ServletContext servletContext = null;
    public PlatformConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    @Override
    public String getInitParameter(String name) {
        return this.servletContext.getInitParameter(name);
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        return this.servletContext.getInitParameterNames();
    }
    @Override
    public Global getSettings() {
        a
        // TODO Auto-generated method stub
        return null;
    }
}