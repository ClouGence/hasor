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
package net.hasor.web.binder.support;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
/**
 * 
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
class FilterChainInvocation implements FilterChain {
    private final FilterDefinition[]     filterDefinitions;
    private final FilterChain            proceedingChain;
    private final ManagedServletPipeline servletPipeline;
    private int                          index = -1;
    //
    public FilterChainInvocation(final FilterDefinition[] filterDefinitions, final ManagedServletPipeline servletPipeline, final FilterChain proceedingChain) {
        this.filterDefinitions = filterDefinitions;
        this.servletPipeline = servletPipeline;
        this.proceedingChain = proceedingChain;
    }
    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        this.index++;
        //dispatch down the chain while there are more filters
        if (this.index < this.filterDefinitions.length) {
            this.filterDefinitions[this.index].doFilter(servletRequest, servletResponse, this);
        } else {
            //we've reached the end of the filterchain, let's try to dispatch to a servlet
            final boolean serviced = this.servletPipeline.service(servletRequest, servletResponse);
            //dispatch to the normal filter chain only if one of our servlets did not match
            if (!serviced) {
                this.proceedingChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}