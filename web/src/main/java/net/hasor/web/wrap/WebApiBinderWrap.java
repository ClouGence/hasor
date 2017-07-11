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
import net.hasor.core.Matcher;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.web.*;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
/**
 * {@link WebApiBinder} 接口包装器
 * @version : 2017-01-10
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
    public FilterBindingBuilder<InvokerFilter> filter(String urlPattern, String... morePatterns) {
        return this.webApiBinder.filter(urlPattern, morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns) {
        return this.webApiBinder.filter(morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String regex, String... regexes) {
        return this.webApiBinder.filterRegex(regex, regexes);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes) {
        return this.webApiBinder.filterRegex(regexes);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(String urlPattern, String... morePatterns) {
        return this.webApiBinder.jeeFilter(urlPattern, morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(String[] morePatterns) {
        return this.webApiBinder.jeeFilter(morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(String regex, String... regexes) {
        return this.webApiBinder.jeeFilterRegex(regex, regexes);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(String[] regexes) {
        return this.webApiBinder.jeeFilterRegex(regexes);
    }
    @Override
    public ServletBindingBuilder jeeServlet(String mappingTo, String... moreMappingTo) {
        return this.webApiBinder.jeeServlet(mappingTo, moreMappingTo);
    }
    @Override
    public ServletBindingBuilder jeeServlet(String[] moreMappingTo) {
        return this.webApiBinder.jeeServlet(moreMappingTo);
    }
    @Override
    public MappingToBindingBuilder<Object> mappingTo(String urlPattern, String... morePatterns) {
        return this.webApiBinder.mappingTo(urlPattern, morePatterns);
    }
    @Override
    public MappingToBindingBuilder<Object> mappingTo(String[] morePatterns) {
        return this.webApiBinder.mappingTo(morePatterns);
    }
    @Override
    public void scanMappingTo() {
        this.webApiBinder.scanMappingTo();
    }
    @Override
    public void scanMappingTo(String... packages) {
        this.webApiBinder.scanMappingTo(packages);
    }
    @Override
    public void scanMappingTo(Matcher<Class<?>> matcher, String... packages) {
        this.webApiBinder.scanMappingTo(matcher, packages);
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
    @Override
    public RenderEngineBindingBuilder<RenderEngine> suffix(String urlPattern, String... morePatterns) {
        return this.webApiBinder.suffix(urlPattern, morePatterns);
    }
    @Override
    public RenderEngineBindingBuilder<RenderEngine> suffix(String[] morePatterns) {
        return this.webApiBinder.suffix(morePatterns);
    }
    @Override
    public void scanAnnoRender() {
        this.webApiBinder.scanAnnoRender();
    }
    @Override
    public void scanAnnoRender(String... packages) {
        this.webApiBinder.scanAnnoRender(packages);
    }
    @Override
    public void scanAnnoRender(Matcher<Class<? extends RenderEngine>> matcher, String... packages) {
        this.webApiBinder.scanAnnoRender(matcher, packages);
    }
}