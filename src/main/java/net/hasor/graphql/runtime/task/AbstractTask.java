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
package net.hasor.graphql.runtime.task;
import net.hasor.core.future.BasicFuture;
import net.hasor.graphql.runtime.QueryContext;
import net.hasor.graphql.runtime.QueryTask;
import net.hasor.graphql.runtime.TaskStatus;
import net.hasor.graphql.runtime.TaskType;

import java.util.*;
import java.util.concurrent.ExecutionException;
/**
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractTask extends Observable implements QueryTask {
    // - 核心字段
    private   String                    nameOfParent = null; // 在父节点中的名字
    private   AbstractTask              parentTask   = null; // 解析结构父节点
    protected Map<String, AbstractTask> fieldTaskMap = null; // 解析结构子节点
    protected List<AbstractTask>        depTaskList  = null; // 依赖的任务
    private   AbstractTask              dataSource   = null; // 数据源
    // - 运行字段
    private   TaskStatus                taskStatus   = null;
    private   TaskType                  taskType     = null; // 任务类型：D,数据源、F,字段
    private   BasicFuture<Object>       result       = null;
    //
    //
    public AbstractTask(String nameOfParent, AbstractTask parentTask, AbstractTask dataSource) {
        //
        this.nameOfParent = nameOfParent;
        this.parentTask = parentTask;
        this.fieldTaskMap = new HashMap<String, AbstractTask>();
        this.depTaskList = new ArrayList<AbstractTask>();
        this.dataSource = dataSource;
        this.addDepTask(dataSource);
        //
        this.taskStatus = TaskStatus.Prepare;
        this.taskType = initTaskType();// 默认为 dataSource
        this.result = new BasicFuture<Object>();
    }
    protected TaskType initTaskType() {
        return TaskType.D;
    }
    //
    //
    /** 添加依赖的子任务 */
    public void addDepTask(AbstractTask depTask) {
        if (depTask == null) {
            return;
        }
        if (this.depTaskList.contains(depTask)) {
            return;
        }
        this.depTaskList.add(depTask);
        depTask.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                // .子任务执行失败，整体失败
                if (TaskStatus.Failed.equals(arg)) {
                    try {
                        ((AbstractTask) o).getValue();
                    } catch (ExecutionException e) {
                        updateStatus(TaskStatus.Failed, e.getCause());
                    } catch (Throwable e) {
                        updateStatus(TaskStatus.Failed, e);
                    } finally {
                        return;
                    }
                }
                // .尝试跳入 Waiting 阶段
                if (TaskStatus.Complete.equals(arg)) {
                    for (AbstractTask task : depTaskList) {
                        if (!task.isFinish()) {
                            return;
                        }
                    }
                    updateStatus(TaskStatus.Waiting, null);//所有子任务Finish，那么进入 Waiting
                }
            }
        });
        this.taskStatus = TaskStatus.Prepare;
    }
    /** 添加字段 */
    protected void addFieldTask(String nameOfParent, AbstractTask fieldTask) {
        if (fieldTask == null) {
            return;
        }
        if (this.fieldTaskMap.containsKey(nameOfParent)) {
            return;
        }
        /* 当一个 Task 成为 Field 之后。如果它不具备 DataSource，那么它的 TaskType 被设定为 F */
        this.fieldTaskMap.put(nameOfParent, fieldTask);
        if (fieldTask instanceof ValueTask || fieldTask instanceof CallerTask) {
            fieldTask.taskType = TaskType.D;
        } else {
            fieldTask.taskType = (fieldTask.dataSource == null ? TaskType.F : TaskType.D);
        }
        //
        this.addDepTask(fieldTask);
    }
    /** 获取字段 */
    public AbstractTask findFieldTask(String nameOfParent) {
        return this.fieldTaskMap.get(nameOfParent);
    }
    /** 获取父节点 */
    public AbstractTask getParent() {
        return this.parentTask;
    }
    /** 获取节点的数据源 */
    public AbstractTask getDataSource() {
        return this.dataSource;
    }
    /** 获取节点的数据类型 */
    public TaskType getTaskType() {
        return this.taskType;
    }
    /** 节点在父关系中的名称 */
    public String getNameOfParent() {
        return this.nameOfParent;
    }
    /** 判断当前节点是否为根节点 */
    public boolean isRoot() {
        return this.parentTask == null;
    }
    /** 节点状态 */
    public TaskStatus getTaskStatus() {
        return this.taskStatus;
    }
    /** 获取节点运行结果 */
    public final Object getValue() throws ExecutionException, InterruptedException {
        if (TaskType.F.equals(this.taskType)) {
            return null;
        }
        if (this.result.isDone() && TaskType.D.equals(this.taskType)) {
            return this.result.get();
        }
        if (this.result.isDone()) {
            return this.result.get();
        }
        throw new IllegalStateException("result is not ready. -> cur status is " + this.taskStatus.name());
    }
    /** 是否等待调度：status = Waiting */
    public boolean isWaiting() {
        return TaskStatus.Waiting.equals(this.taskStatus);
    }
    /** 节点是否执行完毕：status = Complete | Failed */
    public boolean isFinish() {
        return TaskStatus.Complete.equals(this.taskStatus) || TaskStatus.Failed.equals(this.taskStatus);
    }
    //
    //
    public void run(QueryContext taskContext, Object inData) {
        //
        // - F 节点只更新状态。
        if (TaskType.F.equals(this.getTaskType())) {
            this.updateStatus(TaskStatus.Complete, null);
            return;
        }
        // - 只处理 Waiting 状态的节点
        if (!TaskStatus.Waiting.equals(this.getTaskStatus())) {
            return;
        }
        synchronized (this) {
            try {
                if (this.dataSource != null) {
                    inData = this.dataSource.getValue();
                }
                //
                this.updateStatus(TaskStatus.Running, null);
                Object taskResult = this.doTask(taskContext, inData);
                this.result.completed(taskResult);
                this.updateStatus(TaskStatus.Complete, null);
            } catch (Throwable e) {
                this.updateStatus(TaskStatus.Failed, e);
            }
        }
        return;
    }
    protected void updateStatus(TaskStatus taskStatus, Throwable e) {
        if (taskStatus == TaskStatus.Failed) {
            this.result.failed(e);
        }
        try {
            if (taskStatus == TaskStatus.Waiting) {
                for (AbstractTask task : this.depTaskList) {
                    if (!task.isFinish()) {
                        return;/* 所有子任务都完成，才进入 Waiting */
                    }
                }
            }
            this.taskStatus = taskStatus;
            if (taskStatus == TaskStatus.Prepare) {
                for (AbstractTask task : this.depTaskList) {
                    if (!task.isFinish()) {
                        return;/* 所有子任务都完成，才进入 Waiting */
                    }
                }
                this.updateStatus(TaskStatus.Waiting, null);
            }
        } finally {
            this.setChanged();
            this.notifyObservers(this.taskStatus);
        }
    }
    /** 执行任务*/
    public abstract Object doTask(QueryContext taskContext, Object inData) throws Throwable;
    //
    //
    public AbstractTask fixRouteDep() {
        for (AbstractTask task : this.depTaskList) {
            task.fixRouteDep();
        }
        if (this.depTaskList.isEmpty()) {
            if (TaskType.F == this.taskType) {
                this.updateStatus(TaskStatus.Complete, null);
            } else {
                this.updateStatus(TaskStatus.Waiting, null);
            }
        }
        return this;
    }
    /** 获取所有待执行的任务列表。 */
    public List<AbstractTask> getAllTask() {
        return addToAllTask(this, new ArrayList<AbstractTask>());
    }
    private static List<AbstractTask> addToAllTask(AbstractTask task, List<AbstractTask> allTask) {
        if (!allTask.contains(task)) {
            allTask.add(task);
        }
        for (AbstractTask subTask : task.depTaskList) {
            if (!allTask.contains(subTask)) {
                allTask.add(subTask);
            }
            addToAllTask(subTask, allTask);
        }
        return allTask;
    }
}