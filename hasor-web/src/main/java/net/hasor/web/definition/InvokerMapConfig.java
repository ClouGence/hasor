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
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.Iterators;
import net.hasor.web.InvokerConfig;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Abstract implementation for all servlet module bindings
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerMapConfig extends HashMap<String, String> implements InvokerConfig {
    private Supplier<AppContext> appContext;

    public InvokerMapConfig(Map<String, String> initParams, Supplier<AppContext> appContext) {
        this.appContext = appContext;
        if (initParams != null) {
            this.putAll(initParams);
        }
    }

    public InvokerMapConfig(Map<String, String> initParams, AppContext appContext) {
        this.appContext = InstanceProvider.wrap(appContext);
        if (initParams != null) {
            this.putAll(initParams);
        }
    }

    @Override
    public String getInitParameter(String name) {
        return this.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Iterators.asEnumeration(InvokerMapConfig.this.keySet().iterator());
    }

    @Override
    public AppContext getAppContext() {
        return this.appContext.get();
    }
}
