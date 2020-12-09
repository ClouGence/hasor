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
package net.hasor.web.render;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;

/**
 * 渲染器插件。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderWebPlugin implements WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) {
        BindInfo<RenderInvokerFilter> filterInfo = apiBinder.bindType(RenderInvokerFilter.class) //
                .idWith(RenderInvokerFilter.class.getName())//
                .toInstance(new RenderInvokerFilter()).toInfo();
        apiBinder.filter("/*").through(Integer.MIN_VALUE, filterInfo);
    }

    public void onStart(AppContext appContext) throws Throwable {
        appContext.getInstance(RenderInvokerFilter.class).doInit(appContext);
    }
}
