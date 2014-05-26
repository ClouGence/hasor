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
package net.hasor.web.module;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebAppContext;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractWebModule implements Module {
    public final void init(ApiBinder apiBinder) {
        if (apiBinder instanceof WebApiBinder)
            this.init((WebApiBinder) apiBinder);
        else
            throw new UnsupportedOperationException("Hasor context does not support the web module.");
    }
    public final void start(AppContext appContext) {
        if (appContext instanceof WebAppContext)
            this.start((WebAppContext) appContext);
        else
            throw new UnsupportedOperationException("Hasor context does not support the web module.");
    }
    //
    //
    /**初始化过程。*/
    public abstract void init(WebApiBinder apiBinder);
    /**启动信号*/
    public abstract void start(WebAppContext appContext);
}