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
import net.hasor.core.Hasor;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebApiBinder;

import javax.servlet.ServletContext;
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
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return this.webApiBinder.filter(urlPattern, morePatterns);
    }
    @Override
    public FilterBindingBuilder filter(String[] morePatterns) {
        return this.webApiBinder.filter(morePatterns);
    }
    @Override
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return this.webApiBinder.filterRegex(regex, regexes);
    }
    @Override
    public FilterBindingBuilder filterRegex(String[] regexes) {
        return this.webApiBinder.filterRegex(regexes);
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
    public SessionListenerBindingBuilder sessionListener() {
        return this.webApiBinder.sessionListener();
    }
    @Override
    public ServletContextListenerBindingBuilder contextListener() {
        return this.webApiBinder.contextListener();
    }
}