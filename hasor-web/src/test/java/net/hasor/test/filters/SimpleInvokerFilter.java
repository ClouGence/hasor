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
package net.hasor.test.filters;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

public class SimpleInvokerFilter implements InvokerFilter {
    private boolean       init;
    private boolean       destroy;
    private boolean       doCall;
    private InvokerConfig config;

    public boolean isInit() {
        return init;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public boolean isDoCall() {
        return doCall;
    }

    public InvokerConfig getConfig() {
        return config;
    }

    public void init(InvokerConfig config) {
        this.init = true;
        this.config = config;
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        doCall = true;
        return chain.doNext(invoker);
    }

    @Override
    public void destroy() {
        this.destroy = true;
    }
}
