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
package org.more.task.task;
import org.more.task.Task;
/**
 * 单项可执行的任务，该类任务中不包含子任务创建该对象可以将任何任务Task类型或者Runnable接口的
 * 任务，如果被包含的Task类型任务是一个拥有子任务集合的任务，那么这些子任务将会作为一个独立的任务执行。
 * 提示：Task类型已经实现了Runnable接口因此Task也可以看作是Runnable接口对象。
 * 通过ItemTask任务不可以获得有关TaskLocation接口的功能支持。
 * Date : 2009-5-15
 * @author 赵永春
 */
public class ItemTask extends Task {
    /**  */
    private static final long serialVersionUID = 1573066209349130377L;
    /** 任务目标 */
    private Runnable          runnable         = null;
    /**
     * 创建可执行的任务项该类任务项是一个特定的执行任务。
     * @param runnable 准备执行的目标任务。
     */
    public ItemTask(Runnable runnable) {
        this.runnable = runnable;
    }
    /** 执行调用目标的run方法。 */
    @Override
    protected void doRun() throws Exception {
        this.runnable.run();
    }
    /**
     * 获得ItemTask的执行方法，该方法是实现了Runnable接口的类决定。
     * @return 返回ItemTask的执行方法。
     */
    public Runnable getRunnable() {
        return runnable;
    }
    /**
     * 获得当前任务中正在执行的任务对象，返回自己(this)
     * @return 获得当前任务中正在执行的任务对象，返回自己(this)
     */
    @Override
    public Task getCurrent() {
        return this;
    }
}