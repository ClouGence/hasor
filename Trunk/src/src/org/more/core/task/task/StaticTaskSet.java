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
import java.util.LinkedList;
import java.util.List;
import org.more.core.task.LocationTask;
import org.more.core.task.Task;
import org.more.core.task.TaskState;
/**
 * 包含其他任务的任务项，ListTask类型任务。该类型任务的存在目的是为了使任务可以包含其他子任务。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class StaticTaskSet extends LocationTask {
    /**  */
    private static final long serialVersionUID = -6342672748568848797L;
    /** 存放子任务集合 */
    private List<Task>        list             = new LinkedList<Task>();
    /** 当前正在执行的子任务。 */
    private Task              currentTaskCache = null;
    /**
     * 添加要执行的任务。
     * @param item 新的执行任务。
     * @throws UnsupportedOperationException 当任务正在执行时调用该方法将会抛出该异常。
     */
    public void addTaskItem(Task item) throws UnsupportedOperationException {
        this.log.debug("addTaskItem Task name=" + item.getName());
        if (this.getState() == TaskState.Run)
            throw new UnsupportedOperationException("任务正在中不允许调用该方法。");
        this.list.add(item);
        super.setCountTask(this.list.size());
    }
    /**
     * 添加要执行的任务。
     * @param item 新的执行任务。
     * @throws UnsupportedOperationException 当任务正在执行时调用该方法将会抛出该异常。
     */
    public void addTaskItem(Runnable item) throws UnsupportedOperationException {
        this.log.debug("addTaskItem Runnable");
        this.addTaskItem(new ItemTask(item));
    }
    /**
     * 添加要执行的任务。
     * @param item 新的执行任务。
     * @throws UnsupportedOperationException 当任务正在执行时调用该方法将会抛出该异常。
     */
    public void addTaskItem(List<? extends Runnable> item) throws UnsupportedOperationException {
        this.log.debug("addTaskItem list size=" + item.size());
        for (Runnable i : item)
            if (i instanceof Task)
                this.addTaskItem((Task) i);
            else
                this.addTaskItem(i);
    }
    @Override
    protected void doRun() throws Exception {
        this.log.debug("Task doRun...");
        //设置静态集合任务中任务数是多少。
        super.setCountTask(this.list.size());
        //循环每个任务
        for (int i = 0; i < this.list.size(); i++) {
            this.currentTaskCache = this.list.get(i);
            this.currentTaskCache.run();//执行
            super.step();//执行任务完毕一个。
        }
        this.currentTaskCache = null;
    }
    @Override
    public Task getCurrent() {
        if (this.currentTaskCache == null)
            return this;
        else
            return this.currentTaskCache;
    }
}