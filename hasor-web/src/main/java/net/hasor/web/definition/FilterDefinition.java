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
import net.hasor.core.BeanCreaterListener;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.utils.ExceptionUtils;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;
/**
 * Filter 定义
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class FilterDefinition extends AbstractDefinition implements BeanCreaterListener<Filter> {
    private BindInfo<? extends Filter> bindInfo = null;
    //
    public FilterDefinition(long index, String pattern, UriPatternMatcher uriPatternMatcher,//
            BindInfo<? extends Filter> bindInfo, Map<String, String> initParams) {
        super(index, pattern, uriPatternMatcher, initParams);
        this.bindInfo = bindInfo;
    }
    //
    protected final Filter getTarget() throws ServletException {
        return this.getAppContext().getInstance(this.bindInfo);
    }
    @Override
    public void beanCreated(Filter newObject, BindInfo<? extends Filter> bindInfo) throws Throwable {
        final ServletContext servletContext = this.getAppContext().getInstance(ServletContext.class);
        newObject.init(new J2eeMapConfig(bindInfo.getBindID(), this.getInitParams(), new Provider<ServletContext>() {
            @Override
            public ServletContext get() {
                return servletContext;
            }
        }));
    }
    //
    /*--------------------------------------------------------------------------------------------------------*/
    public Object doInvoke(final Invoker invoker, final InvokerChain chain) throws Throwable {
        Filter filter = this.getTarget();
        if (filter == null) {
            throw new NullPointerException("target Filter instance is null.");
        }
        //
        filter.doFilter(invoker.getHttpRequest(), invoker.getHttpResponse(), new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                try {
                    chain.doNext(invoker);
                } catch (IOException e) {
                    throw (IOException) e;
                } catch (ServletException e) {
                    throw (ServletException) e;
                } catch (Throwable e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        });
        return invoker.get(Invoker.RETURN_DATA_KEY);
    }
    public void destroy() {
        //        if (this.instance == null) {
        //            return;
        //        }
        //        this.instance.destroy();
        //        this.instance = null;
    }
}