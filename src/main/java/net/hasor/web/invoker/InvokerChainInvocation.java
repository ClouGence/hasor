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
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;
import net.hasor.web.MappingData;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
class InvokerChainInvocation implements InvokerChain, FilterChain {
    private final MappingData     mapping;
    private final InvokerFilter[] filters;
    private final InvokerChain    chain;
    private int index = -1;
    //
    public InvokerChainInvocation(final MappingData mapping, final InvokerFilter[] filters, final InvokerChain chain) {
        this.mapping = mapping;
        this.filters = filters;
        this.chain = chain;
    }
    @Override
    public void doNext(Invoker invoker) throws Throwable {
        this.index++;
        if (this.index < this.filters.length) {
            if (!this.mapping.matchingMapping(invoker)) {
                this.doNext(invoker);
                return;
            }
            this.filters[this.index].doInvoke(invoker, this);
        } else {
            this.chain.doNext(invoker);
        }
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        //TODO
    }
    @Override
    public Method targetMethod() {
        return this.chain.targetMethod();
    }
    @Override
    public Object[] getParameters() {
        return this.chain.getParameters();
    }
    @Override
    public MappingData getMappingTo() {
        return this.chain.getMappingTo();
    }
}