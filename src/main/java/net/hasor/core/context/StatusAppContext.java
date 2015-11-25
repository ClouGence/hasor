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
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
/**
 * 负责创建和维护{@link DataContext}。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class StatusAppContext<C extends BeanContainer> extends TemplateAppContext<C> {
    private C           container   = null;
    private Environment environment = null;
    //
    public StatusAppContext(Environment environment, C container) {
        this.environment = environment;
        this.container = Hasor.assertIsNotNull(container);
    }
    public StatusAppContext(Environment environment, DataContextCreater<C> creater) throws Throwable {
        this.environment = environment;
        this.container = Hasor.assertIsNotNull(creater.create(environment));
    }
    @Override
    protected C getContainer() {
        return this.container;
    }
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
}