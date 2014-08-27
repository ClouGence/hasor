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
package net.hasor.web;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
/**
 * 
 * @version : 2013-11-4
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class WebModule implements Module {
    @Override
    public final void loadModule(final ApiBinder apiBinder) throws Throwable {
        if (apiBinder instanceof WebApiBinder == false) {
            Hasor.logWarn("does not support ‘%s’ Web plug-in.", this.getClass());
            return;
        }
        this.loadModule((WebApiBinder) apiBinder);
        Hasor.logInfo("‘%s’ Plug-in loaded successfully", this.getClass());
    }
    public abstract void loadModule(WebApiBinder apiBinder) throws Throwable;
}