/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.plugins.beans;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.hasor.core.AppContext;
import net.hasor.core.plugin.Plugin;
import net.hasor.web.AbstractWebHasorPlugin;
import net.hasor.web.WebApiBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 
 * @version : 2013-12-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Plugin
public class BeansPlugin extends AbstractWebHasorPlugin {
    public void loadPlugin(WebApiBinder apiBinder) {
        apiBinder.filter("/*").through(BeansFilter.class);
    }
    @Singleton
    public static class BeansFilter implements Filter {
        @Inject
        private AppContext  appContext;
        private WebBeansMap webBeansMap = null;
        public void init(FilterConfig filterConfig) throws ServletException {
            this.webBeansMap = new WebBeansMap(this.appContext);
        }
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            request.setAttribute("beans", this.webBeansMap);
            chain.doFilter(request, response);
        }
        public void destroy() {}
    }
}