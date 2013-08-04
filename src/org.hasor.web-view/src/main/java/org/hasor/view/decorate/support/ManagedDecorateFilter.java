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
package org.hasor.view.decorate.support;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
/**
 * 
 * @version : 2013-6-9
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
class ManagedDecorateFilter implements Filter {
    private DecorateFilterDefine[] filterDefinitions = null;
    @Inject
    private AppContext             appContext        = null;
    private boolean                enable            = false;
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterDefinitions = collectFilterDefinitions(appContext.getGuice());
        for (DecorateFilterDefine filterDefinition : filterDefinitions) {
            filterDefinition.init(appContext);
        }
        this.enable = this.appContext.getSettings().getBoolean("pageDecorate.enable", false);
    }
    private DecorateFilterDefine[] collectFilterDefinitions(Injector injector) {
        List<DecorateFilterDefine> filterDefinitions = new ArrayList<DecorateFilterDefine>();
        TypeLiteral<DecorateFilterDefine> FILTER_DEFS = TypeLiteral.get(DecorateFilterDefine.class);
        for (Binding<DecorateFilterDefine> entry : injector.findBindingsByType(FILTER_DEFS)) {
            filterDefinitions.add(entry.getProvider().get());
        }
        // Convert to a fixed size array for speed.
        return filterDefinitions.toArray(new DecorateFilterDefine[filterDefinitions.size()]);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (enable == false) {
            chain.doFilter(request, response);
            return;
        }
        //
        //1.执行过滤器获取输出的结果。
        DecHttpServletRequestPropxy decRequest = new DecHttpServletRequestPropxy((HttpServletRequest) request);
        DecHttpServletResponsePropxy decResponse = new DecHttpServletResponsePropxy((HttpServletResponse) response);
        chain.doFilter(decRequest, decResponse);
        //2.对获取结果进行装饰处理
        if (response.isCommitted() == false) {
            new FilterChainInvocation(this.filterDefinitions).doDecorate(decRequest, decResponse);
            decResponse.flushBuffer();
            decResponse.sendByteData(decResponse.getBufferData());
        }
    }
    @Override
    public void destroy() {
        if (this.filterDefinitions != null)
            for (DecorateFilterDefine filter : this.filterDefinitions)
                filter.destroy();
    }
}