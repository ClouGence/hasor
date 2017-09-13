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
import net.hasor.utils.Iterators;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import java.util.Enumeration;
import java.util.Map;
/**
 * InvokerFilter 定义
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokeFilterDefinition extends AbstractDefinition {
    private BindInfo<? extends InvokerFilter> bindInfo = null;
    private InvokerFilter                     instance = null;
    //
    public InvokeFilterDefinition(long index, String pattern, UriPatternMatcher uriPatternMatcher,//
            BindInfo<? extends InvokerFilter> bindInfo, Map<String, String> initParams) {
        super(index, pattern, uriPatternMatcher, initParams);
        this.bindInfo = bindInfo;
    }
    //
    protected final InvokerFilter getTarget() throws Throwable {
        if (this.instance != null) {
            return this.instance;
        }
        //
        final Map<String, String> initParams = this.getInitParams();
        final AppContext appContext = this.getAppContext();
        this.instance = appContext.getInstance(this.bindInfo);
        this.instance.init(new InvokerConfig() {
            @Override
            public String getInitParameter(String name) {
                return initParams.get(name);
            }
            @Override
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
            @Override
            public AppContext getAppContext() {
                return appContext;
            }
        });
        return this.instance;
    }
    //
    /*--------------------------------------------------------------------------------------------------------*/
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        InvokerFilter filter = this.getTarget();
        if (filter != null) {
            filter.doInvoke(invoker, chain);
        } else {
            chain.doNext(invoker);
        }
    }
    public void destroy() {
        if (this.instance != null) {
            this.instance.destroy();
        }
    }
}