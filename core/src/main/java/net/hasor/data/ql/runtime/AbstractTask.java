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
package net.hasor.data.ql.runtime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public abstract class AbstractTask {
    private   String                    nameOfParent = null; // 在父节点中的名字
    private   AbstractTask              parentTask   = null; // 解析结构父节点
    protected Map<String, AbstractTask> fieldTaskMap = null; // 解析结构子节点
    protected List<AbstractTask>        depTaskList  = null; // 依赖的任务
    private   AbstractTask              dataSource   = null; // 数据源
    //
    //
    public AbstractTask(String nameOfParent, AbstractTask parentTask, AbstractTask dataSource) {
        this.nameOfParent = nameOfParent;
        this.parentTask = parentTask;
        this.fieldTaskMap = new HashMap<String, AbstractTask>();
        this.depTaskList = new ArrayList<AbstractTask>();
        this.dataSource = dataSource;
        this.addDepTask(dataSource);
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
    }
    /** 添加字段 */
    protected void addFieldTask(String nameOfParent, AbstractTask fieldTask) {
        if (fieldTask == null) {
            return;
        }
        if (this.fieldTaskMap.containsKey(nameOfParent)) {
            return;
        }
        this.fieldTaskMap.put(nameOfParent, fieldTask);
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
    /** 节点在父关系中的名称 */
    public String getNameOfParent() {
        return this.nameOfParent;
    }
    /** 判断当前节点是否为根节点 */
    public boolean isRoot() {
        return this.parentTask == null;
    }
    //
    /** 执行任务 */
    public final void doTask(QueryContext taskContext) throws Throwable {
        Object defaultVal = taskContext.getInput();
        if (this.dataSource != null) {
            QueryContext dsContext = taskContext.newStack("@", defaultVal);
            this.dataSource.doTask(dsContext);
            defaultVal = dsContext.getOutput();
            taskContext.setInput(defaultVal);
        }
        this.doExceute(taskContext);
    }
    /** 执行任务 */
    protected abstract void doExceute(QueryContext taskContext) throws Throwable;
}