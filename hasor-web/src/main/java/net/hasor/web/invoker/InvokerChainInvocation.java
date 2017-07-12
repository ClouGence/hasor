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
import net.hasor.web.definition.AbstractDefinition;
/**
 *
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
class InvokerChainInvocation implements InvokerChain {
    private final AbstractDefinition[] filters;
    private final InvokerChain         chain;
    private int index = -1;
    //
    public InvokerChainInvocation(final AbstractDefinition[] filters, final InvokerChain chain) {
        this.filters = filters;
        this.chain = chain;
    }
    @Override
    public void doNext(Invoker invoker) throws Throwable {
        this.index++;
        if (this.index < this.filters.length) {
            if (this.filters[this.index].matchesInvoker(invoker)) {
                this.filters[this.index].doInvoke(invoker, this);
            } else {
                this.doNext(invoker);
            }
        } else {
            this.chain.doNext(invoker);
        }
    }
}