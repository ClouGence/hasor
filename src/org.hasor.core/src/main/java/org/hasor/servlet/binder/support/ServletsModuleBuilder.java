/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.hasor.servlet.binder.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import org.hasor.servlet.WebApiBinder.ServletBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 用于处理ServletBindingBuilder接口对象的创建
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
class ServletsModuleBuilder implements Module {
    /*Filter 定义*/
    private final List<ServletDefinition> servletDefinitions = new ArrayList<ServletDefinition>();
    //
    public ServletBindingBuilder filterPattern(List<String> servletPattern) {
        return new ServletBindingBuilderImpl(UriPatternType.SERVLET, servletPattern);
    }
    public ServletBindingBuilder filterRegex(List<String> regexPattern) {
        return new ServletBindingBuilderImpl(UriPatternType.REGEX, regexPattern);
    }
    public void configure(Binder binder) {
        /*将ServletDefinition绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
        for (ServletDefinition define : servletDefinitions)
            binder.bind(ServletDefinition.class).annotatedWith(UniqueAnnotations.create()).toProvider(define);
    }
    /*-----------------------------------------------------------------------------------------*/
    class ServletBindingBuilderImpl implements ServletBindingBuilder {
        private final List<String>   uriPatterns;
        private final UriPatternType uriPatternType;
        public ServletBindingBuilderImpl(UriPatternType uriPatternType, List<String> uriPatterns) {
            this.uriPatterns = uriPatterns;
            this.uriPatternType = uriPatternType;
        }
        public void with(Class<? extends HttpServlet> servletKey) {
            with(Key.get(servletKey));
        }
        public void with(Key<? extends HttpServlet> servletKey) {
            with(servletKey, new HashMap<String, String>());
        }
        public void with(HttpServlet servlet) {
            with(servlet, new HashMap<String, String>());
        }
        public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams) {
            with(Key.get(servletKey), initParams);
        }
        public void with(Key<? extends HttpServlet> servletKey, Map<String, String> initParams) {
            with(servletKey, initParams, null);
        }
        public void with(HttpServlet servlet, Map<String, String> initParams) {
            Key<HttpServlet> servletKey = Key.get(HttpServlet.class, UniqueAnnotations.create());
            with(servletKey, initParams, servlet);
        }
        private void with(Key<? extends HttpServlet> servletKey, Map<String, String> initParams, HttpServlet servletInstance) {
            for (String pattern : uriPatterns)
                servletDefinitions.add(new ServletDefinition(pattern, servletKey, UriPatternType.get(uriPatternType, pattern), initParams, servletInstance));
        }
    }
    /*--*/
}