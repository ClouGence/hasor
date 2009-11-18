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
package org.more.core.task.task;
import org.more.core.task.Task;
/**
 * 等待任务，该任务被执行时会自动调用Thread.sleep方法以睡眠当前线程。
 * 该类型任务是可以用来优化CPU资源和控制任务执行间隔。
 * Date : 2009-5-15
 * @author 赵永春
 */
public class WaitTask extends Task {
    /**  */
    private static final long serialVersionUID = 8058549358582784372L;
    /** 当前等待任务执行时的等待时间 */
    private int               wait             = 50;
    /** 创建等待任务，默认任务间隔是50毫秒。 */
    public WaitTask() {}
    /**
     * 创建等待任务
     * @param wait
     */
    public WaitTask(int wait) {
        this.wait = wait;
    }
    /** 执行等待 */
    protected void doRun() throws Exception {
        this.log.debug("Task doRun... wait " + this.wait);
        Thread.sleep(this.wait);
    }
    /**
     * 获得等待类任务的等待时间间隔，毫秒为单位。
     * @return 获得等待类任务的等待时间间隔，毫秒为单位。
     */
    public int getWait() {
        return wait;
    }
    /**
     * 设置等待类任务的等待时间间隔，毫秒为单位。
     * @param wait 要设置的等待时间
     * @throws IllegalArgumentException 当企图设置一个小于0的数值时引发该异常。
     */
    public void setWait(int wait) throws IllegalArgumentException {
        if (wait < 0)
            throw new IllegalArgumentException();
        else
            this.wait = wait;
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
