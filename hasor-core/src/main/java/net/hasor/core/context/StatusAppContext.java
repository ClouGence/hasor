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
package net.hasor.core.context;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.container.BeanContainer;
/**
 * 通过{@link BeanContainer}提供{@link AppContext}接口功能。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class StatusAppContext extends TemplateAppContext {
    private BeanContainer container   = null;
    private Environment   environment = null;
    //
    public StatusAppContext(Environment environment, BeanContainer container) {
        this.environment = environment;
        this.container = Hasor.assertIsNotNull(container);
    }
    @Override
    protected BeanContainer getContainer() {
        return this.container;
    }
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
}