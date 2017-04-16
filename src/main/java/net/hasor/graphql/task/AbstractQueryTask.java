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
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.task.source.RouteSourceTask;

import java.util.*;
import java.util.concurrent.ExecutionException;
/**
 * QL String：
 * <pre>
 {
 "userInfo" :  {
 "info" : findUserByID ("userID" = 12345) {
 "name",
 "age",
 "nick"
 },
 "nick" : info.nick
 },
 "source" : "GraphQL"
 }
 </pre>
 * Task Tree :
 * <pre>
 [ S ] ObjectStrutsTask           | {
 [ S ]  -> ObjectStrutsTask       |     "userInfo" :  {
 [ S ]    -> ObjectStrutsTask     |         "info" : \
 [ V ]      -> CallerSourceTask   |                  findUserByID \
 [ V ]        -> ValueSourceTask  |                               ("userID" = 12345) {
 [ F ]      -> RouteSourceTask    |           "name",
 [ F ]      -> RouteSourceTask    |           "age",
 [ F ]      -> RouteSourceTask    |           "nick"
 |                                |         },
 [ V ]    -> RouteSourceTask      |         "nick" : info.nick
 [ S ]      -> ObjectStrutsTask   |           "info" : findUserByID("userID" = 12345) { ......
 |                                |            ......
 |                                |     },
 [ V ]  -> ValueSourceTask        |     "source" : "GraphQL"
 |                                | }
 </pre>
 * 任务结构特点：
 *  1. 任何一个节点都可以有多个子节点。
 *  2. 任何一个节点的执行(可执行节点)，在执行前都必须依赖所有子节点(可执行节点)的顺利执行完毕。
 *  3. 节点分为三种类型 -> S：结构节点、F：格式化、V：取值。
 *  4. S结构性节点，可以依赖一另一个 S 节点，或者 V 节点。用作结构的数据源 DataSource。
 *  5. F 节点的初始状态为：Complete、其它类型节点初始状态为：Prepare
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractQueryTask extends Observable implements QueryTask {
    protected List<AbstractQueryTask> subList      = new ArrayList<AbstractQueryTask>();
    private   String                  nameOfParent = null;
    private   TaskStatus              taskStatus   = null;
    private   TaskType                taskType     = null;
    private   BasicFuture<Object>     result       = new BasicFuture<Object>();
    private   AbstractQueryTask       dataSource   = null;
    //
    public AbstractQueryTask(String nameOfParent, TaskType taskType, AbstractQueryTask dataSource) {
        this.nameOfParent = nameOfParent;
        this.taskType = taskType;
        this.taskStatus = TaskStatus.Prepare;
        if (dataSource != null) {
            this.addSubTask(dataSource);
            this.dataSource = dataSource;
        }
    }
    //
    //
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
    private static String taskStatus(AbstractQueryTask task) {
        return task.getTaskType().name() + ":" + StringUtils.center(task.getTaskStatus().name(), maxStatusStringLength, " ");
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
    //
    //
    /** 节点在父关系中的名称。 */
    public String getNameOfParent() {
        return nameOfParent;
    }
    /** 获取所有子任务 */
    protected List<AbstractQueryTask> getSubList() {
        return Collections.unmodifiableList(this.subList);
    }
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
    //
    //
    /** 添加子任务 */
    public void addSubTask(AbstractQueryTask subTask) {
        if (this.subList.contains(subTask)) {
            return;
        }
        subTask.addObserver(new Observer() {
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
    /** 节点类型，SVF（S：结构，V：取值，F：格式） */
    public boolean isRunTask() {
        return TaskType.S.equals(this.taskType) || TaskType.V.equals(this.taskType);
    }
    /** 是否等待调度：status = Waiting */
    public boolean isWaiting() {
        return TaskStatus.Waiting.equals(this.taskStatus);
    }
    /** 节点是否执行完毕：status = Complete | Failed */
    public boolean isFinish() {
        return TaskStatus.Complete.equals(this.taskStatus) || TaskStatus.Failed.equals(this.taskStatus);
    }
    /** 任务类型，任务创建之后不可改变。 */
    public TaskType getTaskType() {
        return this.taskType;
    }
    public TaskStatus getTaskStatus() {
        return this.taskStatus;
    }
    protected void updateStatus(TaskStatus taskStatus) {
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
            this.setChanged();
            this.notifyObservers(this.taskStatus);
        }
    }
    //
    //
    //
    @Override
    public final Object getValue() throws ExecutionException, InterruptedException {
        if (this.result.isDone() && !TaskType.F.equals(this.taskType)) {
            return this.result.get();
        }
        throw new IllegalStateException("result is not ready.");
    }
    public void run(TaskContext taskContext, Object inData) {
        //
        // 1.格式化节点：只更新状态。
        if (TaskType.F.equals(this.getTaskType())) {
            this.updateStatus(TaskStatus.Complete);
            return;
        }
        //
        if (!this.isWaiting()) {
            return;
        }
        synchronized (this) {
            try {
                if (this.dataSource != null) {
                    inData = this.dataSource.getValue();
                }
                //
                this.updateStatus(TaskStatus.Running);
                Object taskResult = this.doTask(taskContext, inData);
                this.result.completed(taskResult);
                this.updateStatus(TaskStatus.Complete);
            } catch (Throwable e) {
                this.updateStatus(TaskStatus.Failed);
                this.result.failed(e);
            }
        }
        return;
    }
    /** 执行任务*/
    public abstract Object doTask(TaskContext taskContext, Object inData) throws Throwable;
    //
    //
    //
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
    /** 叶子节点的任务，跳入 Waiting 阶段 */
    public final void initTask() {
        List<AbstractQueryTask> allTask = this.getAllTask();
        for (AbstractQueryTask task : allTask) {
            if (task.subList.isEmpty()) {
                task.updateStatus(TaskStatus.Waiting);
            }
        }
    }
    //
}