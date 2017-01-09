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
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import org.more.util.Iterators;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.Map;
/**
 *
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ServletDefinition extends AbstractDefinition {
    private BindInfo<? extends HttpServlet> bindInfo = null;
    private HttpServlet                     instance = null;
    //
    public ServletDefinition(long index, String pattern, UriPatternMatcher uriPatternMatcher,//
            BindInfo<? extends HttpServlet> bindInfo, Map<String, String> initParams) {
        super(index, pattern, uriPatternMatcher, initParams);
        this.bindInfo = bindInfo;
    }
    //
    protected final HttpServlet getTarget() throws ServletException {
        if (this.instance != null) {
            return this.instance;
        }
        //
        final Map<String, String> initParams = this.getInitParams();
        final AppContext appContext = this.getAppContext();
        this.instance = appContext.getInstance(this.bindInfo);
        this.instance.init(new ServletConfig() {
            @Override
            public String getServletName() {
                return bindInfo.getBindID();
            }
            @Override
            public ServletContext getServletContext() {
                return appContext.getInstance(ServletContext.class);
            }
            @Override
            public String getInitParameter(String name) {
                return initParams.get(name);
            }
            @Override
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
        });
        return this.instance;
    }
    //
    /*--------------------------------------------------------------------------------------------------------*/
    public void doInvoke(final Invoker invoker, final InvokerChain chain) throws Throwable {
        HttpServlet servlet = this.getTarget();
        if (servlet != null) {
            servlet.service(invoker.getHttpRequest(), invoker.getHttpResponse());
        } else {
            chain.doNext(invoker);
        }
    }
    public void destroy() {
        if (this.instance == null) {
            return;
        }
        this.instance.destroy();
    }
}