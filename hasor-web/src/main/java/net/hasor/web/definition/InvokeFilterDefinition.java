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
import net.hasor.core.BeanCreaterListener;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;

import java.util.Map;
/**
 * InvokerFilter 定义
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokeFilterDefinition extends AbstractDefinition implements BeanCreaterListener<InvokerFilter> {
    private BindInfo<? extends InvokerFilter> bindInfo = null;
    //
    public InvokeFilterDefinition(long index, String pattern, UriPatternMatcher uriPatternMatcher,//
            BindInfo<? extends InvokerFilter> bindInfo, Map<String, String> initParams) {
        super(index, pattern, uriPatternMatcher, initParams);
        this.bindInfo = bindInfo;
    }
    //
    protected final InvokerFilter getTarget() {
        return this.getAppContext().getInstance(this.bindInfo);
    }
    @Override
    public void beanCreated(InvokerFilter newObject, BindInfo<? extends InvokerFilter> bindInfo) throws Throwable {
        Map<String, String> initParams = this.getInitParams();
        AppContext appContext = this.getAppContext();
        newObject.init(new InvokerMapConfig(initParams, appContext));
    }
    //
    /*--------------------------------------------------------------------------------------------------------*/
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        InvokerFilter filter = this.getTarget();
        if (filter == null) {
            throw new NullPointerException("target InvokerFilter instance is null.");
        }
        return filter.doInvoke(invoker, chain);
    }
    public void destroy() {
//        if (this.instance == null) {
//            return;
//        }
//        this.instance.destroy();
//        this.instance = null;
    }
}