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
package org.platform.api.binder;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ServletDefinition extends AbstractServletModuleBinding<HttpServlet> implements Provider<ServletDefinition> {
    public ServletDefinition(String pattern, Key<? extends HttpServlet> servletKey, UriPatternMatcher uriPatternMatcher, Map<String, String> initParams, HttpServlet servletInstance) {
        super(initParams, pattern, servletInstance, uriPatternMatcher);
        //        Key<? extends HttpServlet> servletKey,
    }
    @Override
    public ServletDefinition get() {
        // TODO Auto-generated method stub
        return null;
    }
}