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
/**
 * 循环执行任务，当有需要重复执行的需求就可以使用LoopTask类型任务。LoopTask可以创建执行指定次数的任务
 * 也可以用于创建无限循环的任务，LoopTask通过灵活的配置任务执行次数来达到这些目的。当不希望任务被执行时
 * 可以设置任务执行次数为0。LoopTask类的默认执行次数是无限次。
 * Date : 2009-5-15
 * @author 赵永春
 */
public class LoopTask extends ItemTask {
    /**  */
    private static final long serialVersionUID = 1573066209349130377L;
    /** 决定当前循环任务是否继续下一次执行的标记，如果为true则表示要求继续下一次执行。false则表示当前执行结束之后跳出执行。 */
    private int               doRunCount       = -1;
    /**
     * 创建可执行的任务项该类任务项是一个特定的执行任务。
     * @param runnable 准备执行的目标任务。
     */
    public LoopTask(Runnable runnable) {
        super(runnable);
    }
    @Override
    protected void doRun() throws Exception {
        while (this.doRunCount != 0) {
            if (this.doRunCount > 0)
                //大于 0 减少循环次数
                this.doRunCount--;
            super.doRun();
        }
    }
    @Override
    protected void onException(Exception e) {
        this.doRunCount = 0;
        super.onException(e);
    }
    /** 当本次任务执行完毕之后不在进行下一次执行。 */
    public void breakRun() {
        this.doRunCount = 0;
    }
    /**
     * 获取当前任务执行多少次，如果返回的是0则表示当前任务不执行，如果返回一个负数则表示当前任务无限执行。
     * @return 获取当前任务执行多少次，如果返回的是0则表示当前任务不执行，如果返回一个负数则表示当前任务无限执行。
     */
    public int getDoRunCount() {
        return doRunCount;
    }
    /**
     * 设置当前任务执行多少次，如果设置的值是0则表示当前任务不执行，如果设置的值是一个负数则表示当前任务无限执行。
     * @param doRunCount 要被设置的执行次数
     */
    public void setDoRunCount(int doRunCount) {
        this.doRunCount = doRunCount;
    }
}