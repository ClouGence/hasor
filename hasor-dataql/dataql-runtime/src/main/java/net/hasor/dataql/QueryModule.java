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
package net.hasor.dataql;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;

/**
 * 提供 <code>DataQL</code> 初始化功能。
 * @version : 2017-6-08
 * @author 赵永春 (zyc@byshell.org)
 */
@FunctionalInterface
public interface QueryModule extends Module {
    @Override
    public default void loadModule(final ApiBinder apiBinder) throws Throwable {
        QueryApiBinder queryApiBinder = apiBinder.tryCast(QueryApiBinder.class);
        if (queryApiBinder == null) {
            return;
        }
        this.loadModule(queryApiBinder);
    }

    public void loadModule(QueryApiBinder apiBinder) throws Throwable;
}


