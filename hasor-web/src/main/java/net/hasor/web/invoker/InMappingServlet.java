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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.Iterators;
import net.hasor.web.Invoker;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class InMappingServlet extends InMappingDef {
    private Map<String, String> initParams;
    private Object              servlet;
    public InMappingServlet(long index, BindInfo<? extends HttpServlet> targetType, String mappingTo, Map<String, String> initParams) {
        super(index, targetType, mappingTo, findMethod(), true);
        this.initParams = initParams;
    }
    private static List<Method> findMethod() {
        try {
            Method serviceMethod = HttpServlet.class.getMethod("service", new Class[] { ServletRequest.class, ServletResponse.class });
            return Arrays.asList(serviceMethod);
        } catch (NoSuchMethodException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    @Override
    public Object newInstance(Invoker invoker) throws Throwable {
        if (this.servlet != null) {
            return this.servlet;
        }
        synchronized (this) {
            if (this.servlet != null) {
                return this.servlet;
            }
            final AppContext appContext = invoker.getAppContext();
            this.servlet = super.newInstance(invoker);
            ((Servlet) this.servlet).init(new ServletConfig() {
                @Override
                public String getServletName() {
                    return getTargetType().toString();
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
        }
        return this.servlet;
    }
}