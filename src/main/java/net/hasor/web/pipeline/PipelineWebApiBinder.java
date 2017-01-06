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
package net.hasor.web.pipeline;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.web.WebApiBinder;
import net.hasor.web.definition.UriPatternMatcher;
import net.hasor.web.definition.UriPatternType;
import org.more.util.ArrayUtils;

import javax.servlet.http.HttpServlet;
import java.util.*;
/**
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class PipelineWebApiBinder extends ApiBinderWrap implements WebApiBinder {
    //
    protected PipelineWebApiBinder(ApiBinder apiBinder) {
        super(apiBinder);
    }
    /*--------------------------------------------------------------------------------------Utils*/
    /***/
    protected static List<String> newArrayList(final String[] arr, final String object) {
        ArrayList<String> list = new ArrayList<String>();
        if (arr != null) {
            Collections.addAll(list, arr);
        }
        if (object != null) {
            list.add(object);
        }
        return list;
    }
    //
    /*------------------------------------------------------------------------------------Servlet*/
    @Override
    public ServletBindingBuilder serve(final String urlPattern, final String... morePatterns) {
        return new ServletsModuleBuilder(UriPatternType.SERVLET, PipelineWebApiBinder.newArrayList(morePatterns, urlPattern));
    }
    @Override
    public ServletBindingBuilder serve(final String[] morePatterns) {
        if (ArrayUtils.isEmpty(morePatterns)) {
            throw new NullPointerException("Servlet patterns is empty.");
        }
        return this.serve(null, morePatterns);
    }
    @Override
    public ServletBindingBuilder serveRegex(final String regex, final String... regexes) {
        return new ServletsModuleBuilder(UriPatternType.REGEX, PipelineWebApiBinder.newArrayList(regexes, regex));
    }
    @Override
    public ServletBindingBuilder serveRegex(final String[] regexes) {
        if (ArrayUtils.isEmpty(regexes)) {
            throw new NullPointerException("Servlet regexes is empty.");
        }
        return this.serveRegex(null, regexes);
    }
    //
    private class ServletsModuleBuilder implements ServletBindingBuilder {
        private final List<String>   uriPatterns;
        private final UriPatternType uriPatternType;
        ServletsModuleBuilder(final UriPatternType uriPatternType, final List<String> uriPatterns) {
            this.uriPatterns = uriPatterns;
            this.uriPatternType = uriPatternType;
        }
        @Override
        public void with(final Class<? extends HttpServlet> servletKey) {
            this.with(0, servletKey, null);
        }
        @Override
        public void with(final HttpServlet servlet) {
            this.with(0, servlet, null);
        }
        @Override
        public void with(final Provider<? extends HttpServlet> servletProvider) {
            this.with(0, servletProvider, null);
        }
        @Override
        public void with(BindInfo<? extends HttpServlet> servletRegister) {
            this.with(0, servletRegister, null);
        }
        @Override
        public void with(final Class<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            this.with(0, servletKey, initParams);
        }
        @Override
        public void with(final HttpServlet servlet, final Map<String, String> initParams) {
            this.with(0, servlet, initParams);
        }
        @Override
        public void with(final Provider<? extends HttpServlet> servletProvider, final Map<String, String> initParams) {
            this.with(0, servletProvider, initParams);
        }
        @Override
        public void with(BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
            this.with(0, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final Class<? extends HttpServlet> servletKey) {
            this.with(index, servletKey, null);
        }
        @Override
        public void with(final int index, final HttpServlet servlet) {
            this.with(index, servlet, null);
        }
        @Override
        public void with(final int index, final Provider<? extends HttpServlet> servletProvider) {
            this.with(index, servletProvider, null);
        }
        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister) {
            this.with(index, servletRegister, null);
        }
        //
        @Override
        public void with(final int index, final Class<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().to(servletKey).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final HttpServlet servlet, final Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toInstance(servlet).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final Provider<? extends HttpServlet> servletProvider, Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toProvider((Provider<HttpServlet>) servletProvider).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<String, String>();
            }
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                ServletDefinition define = new ServletDefinition(index, pattern, matcher, (BindInfo<HttpServlet>) servletRegister, initParams);
                bindType(ServletDefinition.class).uniqueName().toInstance(define);/*单列*/
            }
        }
    }
}