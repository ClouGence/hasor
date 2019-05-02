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
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.web.*;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.io.Reader;
/**
 * {@link WebApiBinder} 接口包装器
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
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
    public ServletVersion getServletVersion() {
        return webApiBinder.getServletVersion();
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns) {
        return this.webApiBinder.filter(morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes) {
        return this.webApiBinder.filterRegex(regexes);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(String[] morePatterns) {
        return this.webApiBinder.jeeFilter(morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(String[] regexes) {
        return this.webApiBinder.jeeFilterRegex(regexes);
    }
    @Override
    public ServletBindingBuilder jeeServlet(String[] moreMappingTo) {
        return this.webApiBinder.jeeServlet(moreMappingTo);
    }
    @Override
    public <T> MappingToBindingBuilder<T> mappingTo(String[] morePatterns) {
        return this.webApiBinder.mappingTo(morePatterns);
    }
    @Override
    public void loadMappingTo(Class<?> clazz) {
        this.webApiBinder.loadMappingTo(clazz);
    }
    @Override
    public void addServletListener(BindInfo<? extends ServletContextListener> targetRegister) {
        this.webApiBinder.addServletListener(targetRegister);
    }
    @Override
    public void addSessionListener(BindInfo<? extends HttpSessionListener> targetRegister) {
        this.webApiBinder.addSessionListener(targetRegister);
    }
    @Override
    public void addPlugin(BindInfo<? extends WebPlugin> webPlugin) {
        this.webApiBinder.addPlugin(webPlugin);
    }
    @Override
    public void addDiscoverer(BindInfo<? extends MappingDiscoverer> discoverer) {
        this.webApiBinder.addDiscoverer(discoverer);
    }
    @Override
    public void addMimeType(String type, String mimeType) {
        this.webApiBinder.addMimeType(type, mimeType);
    }
    @Override
    public void loadMimeType(Reader reader) throws IOException {
        this.webApiBinder.loadMimeType(reader);
    }
    @Override
    public RenderEngineBindingBuilder suffix(String[] morePatterns) {
        return this.webApiBinder.suffix(morePatterns);
    }
    @Override
    public void loadRender(Class<?> renderClass) {
        this.webApiBinder.loadRender(renderClass);
    }
}