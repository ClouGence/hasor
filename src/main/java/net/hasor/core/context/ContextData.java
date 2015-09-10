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
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.factory.FactoryBeanBuilder;
/**
 * 负责承载Hasor {@link AppContext}的状态数据。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class ContextData {
    private AtomicBoolean   inited    = new AtomicBoolean(false);
    private DefineContainer container = new DefineContainer();
    private BeanBuilder     builder   = new FactoryBeanBuilder();
    //
    public boolean isStart() {
        return this.inited.get();
    }
    public DefineContainer getBindInfoContainer() {
        return container;
    }
    public BeanBuilder getBeanBuilder() {
        return this.builder;
    }
    public abstract Environment getEnvironment();
    /*---------------------------------------------------------------------------------------Life*/
    public void doInitializeCompleted(AppContext appContext) {
        this.container.doInitializeCompleted(appContext);
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
    }
    public void doShutdownCompleted(AppContext appContext) {
        this.container.doInitializeCompleted(appContext);
        if (!this.inited.compareAndSet(true, false)) {
            return;/*避免被销毁多次*/
        }
    }
}