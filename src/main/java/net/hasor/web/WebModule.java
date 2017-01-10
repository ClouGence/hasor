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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * WebModule
 * @version : 2013-11-4
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class WebModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public final void loadModule(final ApiBinder apiBinder) throws Throwable {
        WebApiBinder webApiBinder = apiBinder.tryCast(WebApiBinder.class);
        if (webApiBinder == null) {
            return;
        }
        this.loadModule(webApiBinder);
    }
    public abstract void loadModule(WebApiBinder apiBinder) throws Throwable;
}