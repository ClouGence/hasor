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
package net.hasor.data.ql.runtime.task;
import net.hasor.data.ql.runtime.QueryContext;
import net.hasor.data.ql.runtime.TaskType;
import net.hasor.data.ql.runtime.TaskUtils;

import java.util.Arrays;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RouteTask extends AbstractPrintTask {
    private String routeExpression;
    public RouteTask(String nameOfParent, AbstractTask parentTask, AbstractTask dataSource) {
        super(nameOfParent, parentTask, dataSource);
    }
    //
    @Override
    protected TaskType initTaskType() {
        return TaskType.F;
    }
    public void setRouteExpression(String routeExpression) {
        this.routeExpression = routeExpression;
    }
    @Override
    public Object doTask(QueryContext taskContext, Object inData) throws Throwable {
        //
        if (this.routeExpression.contains(".")) {
            return this.evalRoute(taskContext, this, this.routeExpression.split("\\."), inData);
        }
        //
        if (inData != null) {
            return TaskUtils.readProperty(inData, this.routeExpression);
        }
        //
        AbstractTask nearDS = this.getDataSource();
        if (TaskType.F == this.getTaskType()) {
            nearDS = TaskUtils.nearDS(this);//最近的数据源
        }
        //
        if (nearDS != null) {
            Object value = nearDS.getValue();
            return TaskUtils.readProperty(value, this.routeExpression);
        }
        //
        return taskContext.get(this.routeExpression);
    }
    private Object evalRoute(final QueryContext taskContext, final AbstractTask routeTask, final String[] routeSplit, final Object inData) throws Throwable {
        AbstractTask curTask = routeTask;
        AbstractTask dataTask = null;
        AbstractTask dataSource = curTask.getDataSource();
        //
        // - 根节点
        if (routeSplit[0].charAt(0) == '$') {
            while (curTask.getParent() != null) {
                curTask = curTask.getParent();
            }
            dataTask = curTask;
            dataSource = TaskUtils.nearDS(curTask); //先找到根节点，然后定位根节点的数据源
        }
        // - 最近的DS
        if (routeSplit[0].charAt(0) == '~') {
            dataTask = TaskUtils.nearData(curTask);
            dataSource = TaskUtils.nearDS(dataTask);//最近的数据源
        }
        // - 其它
        if (dataTask == null) {
            AbstractTask tempTask = curTask;
            while (tempTask != null) {
                dataTask = tempTask.findFieldTask(routeSplit[0]);
                if (dataTask != null) {
                    dataSource = TaskUtils.nearDS(dataTask);
                    break;
                }
                tempTask = tempTask.getParent();
            }
        }
        // - 定位 route 表达式所处的 Task
        AbstractTask atTask = dataTask;
        String[] newRouteSplit = Arrays.copyOfRange(routeSplit, 1, routeSplit.length);
        for (String nodeName : newRouteSplit) {
            atTask = atTask.findFieldTask(nodeName);
            if (atTask == null)
                break;
        }
        // - 如果无法定位到 route 的所处 Task 那么采用 DS 中的数据。
        if (atTask == null) {
            if (dataSource != null) {
                Object value = dataSource.getValue();
                for (String nodeName : newRouteSplit) {
                    if (value == null) {
                        return null;
                    }
                    value = TaskUtils.readProperty(value, nodeName);
                }
                return value;
            }
            return null;
        }
        //
        if (TaskType.F == atTask.getTaskType()) {
            RouteTask routeAtTask = (RouteTask) atTask;
            return routeAtTask.doTask(taskContext, inData);//如果找到的目标节点是一个 F 节点，那么继续递归求值
        }
        return taskContext.get(this.routeExpression);
        // throw new IllegalStateException("result is not ready or task is does support run.");
    }
}