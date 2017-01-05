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
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;
import org.more.util.Iterators;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokeFilterDefinition extends AbstractDefinition implements InvokerFilter {
    private BindInfo<? extends InvokerFilter> bindInfo   = null;
    private InvokerFilter                     instance   = null;
    private AppContext                        appContext = null;
    //
    public InvokeFilterDefinition(int index, String pattern, UriPatternMatcher uriPatternMatcher,//
            BindInfo<? extends InvokerFilter> bindInfo, Map<String, String> initParams) {
        super(index, pattern, uriPatternMatcher, initParams);
        this.bindInfo = bindInfo;
    }
    //
    @Override
    public String toString() {
        return String.format("type %s pattern=%s ,initParams=%s ,uriPatternType=%s", //
                InvokeFilterDefinition.class, this.getPattern(), this.getInitParams(), this.getUriPatternType());
    }
    //
    protected final InvokerFilter getTarget() {
        if (this.instance != null) {
            return this.instance;
        }
        //
        final Map<String, String> initParams = this.getInitParams();
        this.instance = this.appContext.getInstance(this.bindInfo);
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
    @Override
    public void init(InvokerConfig config) {
        this.appContext = config.getAppContext();
        Map<String, String> initParams = new HashMap<String, String>();
        Enumeration<String> names = config.getInitParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            String value = config.getInitParameter(key);
            initParams.put(key, value);
        }
        //
        this.getInitParams().putAll(initParams);
        this.getTarget();
    }
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        String path = invoker.getRequestPath();
        boolean serve = this.matchesUri(path);
        //
        InvokerFilter filter = this.getTarget();
        if (serve && filter != null) {
            filter.doInvoke(invoker, chain);
        } else {
            chain.doNext(invoker);
        }
    }
    public void destroy() {
        InvokerFilter filter = this.getTarget();
        if (filter != null) {
            filter.destroy();
        }
    }
}