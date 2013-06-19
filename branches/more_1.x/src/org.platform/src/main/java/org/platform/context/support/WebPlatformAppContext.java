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
package org.platform.context.support;
import javax.servlet.ServletContext;
import org.more.util.ArrayUtils;
import org.platform.context.AppContext;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
/**
 * {@link AppContext}接口的抽象实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebPlatformAppContext extends PlatformAppContext {
    public WebPlatformAppContext(ServletContext context) {
        super(context);
        System.setProperty("MORE_WEB_HOME", context.getRealPath("/"));
    }
    @Override
    public synchronized void start(Module... modules) {
        final Module webModule = new Module() {
            @Override
            public void configure(Binder binder) {
                /*绑定BeanContext对象的Provider*/
                binder.bind(ServletContext.class).toProvider(new Provider<ServletContext>() {
                    @Override
                    public ServletContext get() {
                        return (ServletContext) WebPlatformAppContext.this.getContext();
                    }
                });
            }
        };
        modules = ArrayUtils.addToArray(modules, webModule);
        super.start(modules);
    }
}