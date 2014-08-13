/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.context;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.context.adapter.RegisterFactoryCreater;
/**
 * 
 * @version : 2014年7月7日
 * @author 赵永春(zyc@hasor.net)
 */
public final class HasorFactory {
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext() {
        return HasorFactory.createAppContext(AbstractResourceAppContext.DefaultSettings, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final RegisterFactoryCreater factory) {
        return HasorFactory.createAppContext(AbstractResourceAppContext.DefaultSettings, factory, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final RegisterFactoryCreater factory, final Module... modules) {
        return HasorFactory.createAppContext(AbstractResourceAppContext.DefaultSettings, factory, modules);
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config) {
        return HasorFactory.createAppContext(config, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final RegisterFactoryCreater factory) {
        return HasorFactory.createAppContext(config, factory, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final RegisterFactoryCreater factory, final Module... modules) {
        try {
            StandardAppContext app = new StandardAppContext(config, factory);
            app.start(modules);
            return app;
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
    //
    //
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final Module... modules) {
        return HasorFactory.createAppContext(config, null, modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final Module... modules) {
        return HasorFactory.createAppContext(AbstractResourceAppContext.DefaultSettings, null, modules);
    }
}