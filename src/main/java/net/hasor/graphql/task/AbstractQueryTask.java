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
package net.hasor.graphql.task;
import net.hasor.core.future.BasicFuture;
import net.hasor.core.utils.StringUtils;
import net.hasor.graphql.task.source.RouteSourceTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
/**
 * 任务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractQueryTask implements QueryTask {
    private List<AbstractQueryTask> subList     = new ArrayList<AbstractQueryTask>();
    private TaskStatus              taskStatus  = TaskStatus.Prepare;
    private Observable              observable  = new Observable();
    private BasicFuture<Object>     result      = new BasicFuture<Object>();
    private TaskContext             taskContext = null;
    //
    public AbstractQueryTask(TaskContext taskContext) {
        this.taskContext = taskContext;
    }
    protected TaskContext getTaskContext() {
        return this.taskContext;
    }
    //
    public void addSubTask(AbstractQueryTask subTask) {
        subTask.observable.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                // .子任务执行失败，整体失败
                if (TaskStatus.Failed.equals(arg)) {
                    updateStatus(TaskStatus.Failed);
                    return;
                }
                // .尝试跳入 Waiting 阶段
                if (TaskStatus.Complete.equals(arg)) {
                    for (AbstractQueryTask task : subList) {
                        if (!task.isFinish()) {
                            return;
                        }
                    }
                    updateStatus(TaskStatus.Waiting);//所有子任务Finish，那么进入 Waiting
                }
            }
        });
        this.subList.add(subTask);
    }
    /* 子节点中如果存在 Routel类型的任务，自动让其它非 Route 类型的任务作为自己的子任务（因为 Route 需要依赖到当前节点） */
    AbstractQueryTask fixRouteDep() {
        List<AbstractQueryTask> routeList = new ArrayList<AbstractQueryTask>();
        List<AbstractQueryTask> nonRouteList = new ArrayList<AbstractQueryTask>();
        for (AbstractQueryTask task : this.subList) {
            task.fixRouteDep();
            if (task instanceof RouteSourceTask) {
                routeList.add(task);
            } else {
                nonRouteList.add(task);
            }
        }
        for (AbstractQueryTask task : routeList) {
            for (AbstractQueryTask subTask : nonRouteList) {
                task.addSubTask(subTask);
            }
        }
        return this;
    }
    //
    public boolean isWaiting() {
        return TaskStatus.Waiting.equals(this.taskStatus);
    }
    public boolean isFinish() {
        return TaskStatus.Complete.equals(this.taskStatus) || TaskStatus.Failed.equals(this.taskStatus);
    }
    private void updateStatus(TaskStatus taskStatus) {
        try {
            this.taskStatus = taskStatus;
            if (taskStatus == TaskStatus.Prepare) {
                for (AbstractQueryTask task : this.subList) {
                    if (!task.isFinish()) {
                        return;/* 所有子任务都完成，才进入 Waiting */
                    }
                }
                this.updateStatus(TaskStatus.Waiting);
            }
        } finally {
            this.observable.notifyObservers(this.taskStatus);
        }
    }
    //
    /** 打印执行任务树 */
    public String printTaskTree(boolean detail) {
        StringBuilder builder = new StringBuilder();
        if (detail) {
            this.printDetailTaskTree(builder, 0);
        } else {
            List<AbstractQueryTask> depList = new ArrayList<AbstractQueryTask>();
            this.printTaskTree(builder, 0, depList);
        }
        return builder.toString();
    }
    //
    private static String genDepthStr(int depth) {
        return StringUtils.leftPad("", depth * 2, " ");
    }
    private void printDetailTaskTree(StringBuilder builder, int depth) {
        if (depth == 0) {
            builder.append("[" + taskStatus(this) + "] ");
        }
        builder.append(this.getClass().getSimpleName());
        for (int i = 0; i < this.subList.size(); i++) {
            AbstractQueryTask task = this.subList.get(i);
            builder.append("\n[" + taskStatus(task) + "] " + genDepthStr(depth) + " -> ");
            task.printDetailTaskTree(builder, depth + 1);
        }
    }
    private boolean printTaskTree(StringBuilder builder, int depth, List<AbstractQueryTask> depList) {
        boolean result = !depList.contains(this);
        if (result) {
            if (depth == 0) {
                builder.append("[" + taskStatus(this) + "] ");
            }
            builder.append(this.getClass().getSimpleName());
        }
        depList.add(this);
        for (int i = 0; i < this.subList.size(); i++) {
            AbstractQueryTask task = this.subList.get(i);
            if (!depList.contains(task)) {
                builder.append("\n[" + taskStatus(task) + "] " + genDepthStr(depth) + " -> ");
            }
            int nextDepth = (result) ? (depth + 1) : depth;
            task.printTaskTree(builder, nextDepth, depList);
        }
        return result;
    }
    private static String taskStatus(AbstractQueryTask abstractQueryTask) {
        return StringUtils.center(abstractQueryTask.taskStatus.name(), maxStatusStringLength, " ");
    }
    private static int maxStatusStringLength = 0;

    static {
        for (TaskStatus ts : TaskStatus.values()) {
            int length = ts.name().length();
            if (maxStatusStringLength <= length) {
                maxStatusStringLength = length;
            }
        }
        maxStatusStringLength = maxStatusStringLength + 2;
    }
    //
    /** 获取所有待执行的任务列表。 */
    public List<AbstractQueryTask> getAllTask() {
        return addToAllTask(this, new ArrayList<AbstractQueryTask>());
    }
    private static List<AbstractQueryTask> addToAllTask(AbstractQueryTask task, List<AbstractQueryTask> allTask) {
        if (!allTask.contains(task)) {
            allTask.add(task);
        }
        for (AbstractQueryTask subTask : task.subList) {
            if (!allTask.contains(subTask)) {
                allTask.add(subTask);
            }
            addToAllTask(subTask, allTask);
        }
        return allTask;
    }
    //
    @Override
    public final Object getValue() throws ExecutionException, InterruptedException {
        return this.result.get();
    }
    @Override
    public final void run() {
        if (!this.isWaiting()) {
            return;
        }
        try {
            this.updateStatus(TaskStatus.Running);
            Object taskResult = this.doTask(this.taskContext);
            this.result.completed(taskResult);
            this.updateStatus(TaskStatus.Complete);
        } catch (Throwable e) {
            this.updateStatus(TaskStatus.Failed);
            this.result.failed(e);
        }
    }
    /** 叶子节点的任务，跳入 Waiting 阶段 */
    public final void initTask() {
        List<AbstractQueryTask> allTask = this.getAllTask();
        for (AbstractQueryTask task : allTask) {
            if (task.subList.isEmpty()) {
                task.updateStatus(TaskStatus.Waiting);
            }
        }
    }
    /** 执行任务*/
    protected abstract Object doTask(TaskContext taskContext) throws Throwable;
}