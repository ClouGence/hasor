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
package net.hasor.web.wrap;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.web.*;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
/**
 *
 * @version : 2015年10月26日
 * @author 赵永春(zyc@hasor.net)
 */
public class WebApiBinderWrap extends ApiBinderWrap implements WebApiBinder {
    private WebApiBinder webApiBinder;
    public WebApiBinderWrap(WebApiBinder apiBinder) {
        super(apiBinder);
        this.webApiBinder = Hasor.assertIsNotNull(apiBinder);
    }
    public ServletContext getServletContext() {
        return this.webApiBinder.getServletContext();
    }
    @Override
    public String getMimeType(String suffix) {
        return this.webApiBinder.getMimeType(suffix);
    }
    @Override
    public WebApiBinder setRequestCharacter(String encoding) {
        return this.webApiBinder.setRequestCharacter(encoding);
    }
    @Override
    public WebApiBinder setResponseCharacter(String encoding) {
        return this.webApiBinder.setResponseCharacter(encoding);
    }
    @Override
    public WebApiBinder setEncodingCharacter(String requestEncoding, String responseEncoding) {
        return this.webApiBinder.setEncodingCharacter(requestEncoding, responseEncoding);
    }
    @Override
    public ServletVersion getServletVersion() {
        return webApiBinder.getServletVersion();
    }
    @Override
    public FilterBindingBuilder<Filter> filter(String urlPattern, String... morePatterns) {
        return this.webApiBinder.filter(urlPattern, morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> filter(String[] morePatterns) {
        return this.webApiBinder.filter(morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> filterRegex(String regex, String... regexes) {
        return this.webApiBinder.filterRegex(regex, regexes);
    }
    @Override
    public FilterBindingBuilder<Filter> filterRegex(String[] regexes) {
        return this.webApiBinder.filterRegex(regexes);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilter(String urlPattern, String... morePatterns) {
        return this.webApiBinder.invFilter(urlPattern, morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilter(String[] morePatterns) {
        return this.webApiBinder.invFilter(morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilterRegex(String regex, String... regexes) {
        return this.webApiBinder.invFilterRegex(regex, regexes);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilterRegex(String[] regexes) {
        return this.webApiBinder.invFilterRegex(regexes);
    }
    @Override
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return this.webApiBinder.serve(urlPattern, morePatterns);
    }
    @Override
    public ServletBindingBuilder serve(String[] morePatterns) {
        return this.webApiBinder.serve(morePatterns);
    }
    @Override
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return this.webApiBinder.serveRegex(regex, regexes);
    }
    @Override
    public ServletBindingBuilder serveRegex(String[] regexes) {
        return this.webApiBinder.serveRegex(regexes);
    }
    @Override
    public void addServletListener(Class<? extends ServletContextListener> targetKey) {
        this.webApiBinder.addServletListener(targetKey);
    }
    @Override
    public void addServletListener(ServletContextListener sessionListener) {
        this.webApiBinder.addServletListener(sessionListener);
    }
    @Override
    public void addServletListener(Provider<? extends ServletContextListener> targetProvider) {
        this.webApiBinder.addServletListener(targetProvider);
    }
    @Override
    public void addServletListener(BindInfo<? extends ServletContextListener> targetRegister) {
        this.webApiBinder.addServletListener(targetRegister);
    }
    @Override
    public void addSessionListener(Class<? extends HttpSessionListener> targetKey) {
        this.webApiBinder.addSessionListener(targetKey);
    }
    @Override
    public void addSessionListener(HttpSessionListener sessionListener) {
        this.webApiBinder.addSessionListener(sessionListener);
    }
    @Override
    public void addSessionListener(Provider<? extends HttpSessionListener> targetProvider) {
        this.webApiBinder.addSessionListener(targetProvider);
    }
    @Override
    public void addSessionListener(BindInfo<? extends HttpSessionListener> targetRegister) {
        this.webApiBinder.addSessionListener(targetRegister);
    }
    @Override
    public WebApiBinder addPlugin(Class<? extends WebPlugin> webPlugin) {
        return this.webApiBinder.addPlugin(webPlugin);
    }
    @Override
    public WebApiBinder addPlugin(WebPlugin webPlugin) {
        return this.webApiBinder.addPlugin(webPlugin);
    }
    @Override
    public WebApiBinder addPlugin(Provider<? extends WebPlugin> webPlugin) {
        return this.webApiBinder.addPlugin(webPlugin);
    }
    @Override
    public WebApiBinder addPlugin(BindInfo<? extends WebPlugin> webPlugin) {
        return this.webApiBinder.addPlugin(webPlugin);
    }
    @Override
    public WebApiBinder addSetup(Class<? extends MappingSetup> setup) {
        return this.webApiBinder.addSetup(setup);
    }
    @Override
    public WebApiBinder addSetup(MappingSetup setup) {
        return this.webApiBinder.addSetup(setup);
    }
    @Override
    public WebApiBinder addSetup(Provider<? extends MappingSetup> setup) {
        return this.webApiBinder.addSetup(setup);
    }
    @Override
    public WebApiBinder addSetup(BindInfo<? extends MappingSetup> setup) {
        return this.webApiBinder.addSetup(setup);
    }
}