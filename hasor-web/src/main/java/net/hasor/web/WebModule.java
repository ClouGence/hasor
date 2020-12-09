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
package net.hasor.web;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;

/**
 * WebModule
 * @version : 2013-11-4
 * @author 赵永春 (zyc@hasor.net)
 */
@FunctionalInterface
public interface WebModule extends Module {
    @Override
    public default void loadModule(final ApiBinder apiBinder) throws Throwable {
        WebApiBinder webApiBinder = apiBinder.tryCast(WebApiBinder.class);
        if (webApiBinder == null) {
            throw new Module.IgnoreModuleException();
        }
        this.loadModule(webApiBinder);
    }

    public void loadModule(WebApiBinder apiBinder) throws Throwable;
}