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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Abstract implementation for all servlet module bindings
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class FilterDef implements InvokerFilter {
    private final int                     index;
    private final UriPatternMatcher       patternMatcher;
    private final OneConfig               initParams;
    //
    private       AtomicBoolean           inited;
    private       BindInfo<?>             targetType;
    private       Supplier<InvokerFilter> targetFilter;

    public FilterDef(int index, UriPatternMatcher patternMatcher, Map<String, String> initParams,//
            BindInfo<? extends InvokerFilter> bindInfo, Supplier<AppContext> appContext//
    ) {
        this.index = index;
        this.patternMatcher = patternMatcher;
        this.initParams = new OneConfig(bindInfo.getBindID(), initParams, appContext);
        this.inited = new AtomicBoolean(false);
        this.targetType = bindInfo;
        this.targetFilter = Provider.ofs(() -> appContext.get().getInstance(bindInfo));
    }

    /***/
    public int getIndex() {
        return this.index;
    }

    /** Returns true if the given URI will match this binding. */
    public boolean matchesInvoker(Invoker invoker) {
        String url = invoker.getRequestPath();
        return this.patternMatcher.matches(url);
    }

    public BindInfo<?> getTargetType() {
        return this.targetType;
    }

    public UriPatternMatcher getMatcher() {
        return patternMatcher;
    }

    public InvokerConfig getInitParams() {
        return initParams;
    }

    @Override
    public String toString() {
        return String.format("pattern=%s ,uriPatternType=%s ,type %s ,initParams=%s ", //
                this.patternMatcher.getPattern(), this.patternMatcher, this.getClass(), this.initParams);
    }

    @Override
    public final void init(InvokerConfig config) throws Throwable {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        if (config != null) {
            this.initParams.putConfig(config, true);
        }
        this.targetFilter().init(this.initParams);
    }

    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        if (!this.inited.get()) {
            throw new IllegalStateException("this Filter uninitialized.");
        }
        //
        return this.targetFilter().doInvoke(invoker, chain);
    }

    private InvokerFilter targetFilter() {
        InvokerFilter filter = this.targetFilter.get();
        if (filter == null) {
            throw new NullPointerException("target InvokerFilter instance is null.");
        }
        return filter;
    }

    public void destroy() {
        if (!this.inited.compareAndSet(true, false)) {
            return;
        }
        this.targetFilter.get().destroy();
    }
}