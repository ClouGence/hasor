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
package org.more.hypha.commons;
import org.more.hypha.ApplicationContext;
import org.more.hypha.Service;
import org.more.util.attribute.IAttribute;
/**
 * 接口{@link Service}的抽象实现。该类的子类可以通过该类受保护的方法获取到其他服务。
 * Date : 2011-4-8
 * @author 赵永春
 */
public abstract class AbstractService implements Service {
    private ApplicationContext context = null;
    public void init(ApplicationContext context, IAttribute<Object> flash) {
        this.context = context;
    }
    protected ApplicationContext getContext() {
        return this.context;
    }
    protected <T extends Service> T getService(Class<T> service) {
        return this.context.getService(service);
    }
};