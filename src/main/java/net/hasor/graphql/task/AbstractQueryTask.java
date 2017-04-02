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
import net.hasor.core.utils.StringUtils;
import net.hasor.graphql.task.source.RouteSourceTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
/**
 * 任务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractQueryTask extends Observable implements QueryTask {
    private List<AbstractQueryTask> subList    = new ArrayList<AbstractQueryTask>();
    private TaskStatus              taskStatus = TaskStatus.Plan;
    //
    public void addSubTask(AbstractQueryTask task) {
        task.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                // .子任务执行失败，整体失败
                if (TaskStatus.Failed.equals(arg)) {
                    updateStatus(TaskStatus.Failed);
                    return;
                }
                // .子任务执行完毕，父任务从计划中跳转到准备阶段
                if (TaskStatus.Complete.equals(arg) && taskStatus == TaskStatus.Plan) {
                    updateStatus(TaskStatus.Prepare);
                }
            }
        });
        this.subList.add(task);
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
    public boolean isComplete() {
        return TaskStatus.Complete.equals(this.taskStatus);
    }
    private void updateStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
        notifyObservers(this.taskStatus);
        if (taskStatus == TaskStatus.Prepare) {
            for (AbstractQueryTask task : this.subList) {
                if (!task.isComplete()) {
                    return;
                }
            }
            this.updateStatus(TaskStatus.Waiting);
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
    protected void runTask() {
        //
    }
}