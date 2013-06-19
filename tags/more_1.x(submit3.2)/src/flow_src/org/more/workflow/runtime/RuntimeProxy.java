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
package org.more.workflow.runtime;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.object.RuntimeEvent;
import org.more.workflow.metadata.AbstractObject;
import org.more.workflow.util.Config;
/**
 * 该类是{@link Runtime}接口的一个实现，主要用于代理{@link Runtime}对象，
 * 并且提供元信息和{@link RuntimeStateHolder}的绑定。并且对每个阶段的事件进行引发.
 * Date : 2010-6-14
 * @author 赵永春
 */
public class RuntimeProxy extends AbstractObject implements Runtime {
    private Runtime runtime = null;
    /**创建一个RuntimeProxy对象*/
    protected RuntimeProxy(String runtimeID, Runtime runtime, RuntimeStateHolder runtimeStateHolder) {
        super(runtimeID, runtimeStateHolder);
        if (runtime == null)
            throw new NullPointerException("创建代理runtime出现异常，不能创建一个空runtime引用的代理。");
        this.runtime = runtime;
    };
    @Override
    public void init(Config config) throws Throwable {
        RuntimeEvent event = new RuntimeEvent(this.runtime);
        event.setAttribute("config", config);
        this.event(event.getEventPhase()[0]);
        this.runtime.init(config);
    };
    @Override
    public void beforeRun(Config config, RunContext runContext) throws Throwable {
        RuntimeEvent event = new RuntimeEvent(this.runtime);
        event.setAttribute("config", config);
        event.setAttribute("runContext", runContext);
        this.event(event.getEventPhase()[1]);
        this.runtime.beforeRun(config, runContext);
    };
    @Override
    public Object doRun(Config config, RunContext runContext) throws Throwable {
        RuntimeEvent event = new RuntimeEvent(this.runtime);
        event.setAttribute("config", config);
        event.setAttribute("runContext", runContext);
        this.event(event.getEventPhase()[2]);
        return this.runtime.doRun(config, runContext);
    };
    @Override
    public void afterRun(Config config, RunContext runContext) throws Throwable {
        RuntimeEvent event = new RuntimeEvent(this.runtime);
        event.setAttribute("config", config);
        event.setAttribute("runContext", runContext);
        this.event(event.getEventPhase()[3]);
        this.runtime.afterRun(config, runContext);
    };
    @Override
    public void destroy() {
        RuntimeEvent event = new RuntimeEvent(this.runtime);
        this.event(event.getEventPhase()[4]);
        this.runtime.destroy();
    };
    /** 获取此代理类所代理的具体Runtime对象。*/
    public Runtime getTargetBean() {
        return this.runtime;
    };
};