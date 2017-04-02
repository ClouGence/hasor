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
package net.hasor.graphql.task.source;
import net.hasor.core.utils.BeanUtils;
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.task.AbstractQueryTask;
import net.hasor.graphql.task.TaskUtils;

import java.util.Arrays;
import java.util.List;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RouteSourceTask extends SourceQueryTask {
    private String            routeExpression;
    private AbstractQueryTask dataSource;
    public RouteSourceTask(String nameOfParent, TaskContext taskContext, AbstractQueryTask dataSource, String routeExpression) {
        super(taskContext, nameOfParent);
        if (dataSource != null) {
            super.addSubTask(dataSource);
        }
        this.routeExpression = routeExpression;
        this.dataSource = dataSource;
    }
    //
    public String getRouteExpression() {
        return this.routeExpression;
    }
    //
    @Override
    protected Object doTask(TaskContext taskContext) throws Throwable {
        String routeExpression = this.getRouteExpression();
        String[] routePath = routeExpression.split("\\.");
        if (this.dataSource == null) {
            // 利用 nameOfParent 处理 同级别字段引用
            List<AbstractQueryTask> subList = super.getSubList();
            for (AbstractQueryTask task : subList) {
                if (!task.isFinish()) {
                    continue;
                }
                String nameOfParent = (String) BeanUtils.readPropertyOrField(task, "nameOfParent");
                if (routePath[0].equals(nameOfParent)) {
                    routePath = Arrays.copyOfRange(routePath, 1, routePath.length);
                    return evalRoute(task.getValue(), routePath);
                }
            }
            return taskContext.get(routeExpression);
        }
        return evalRoute(this.dataSource.getValue(), routePath);
    }
    private Object evalRoute(final Object data, final String[] routePath) throws Exception {
        // demo : aaa.bbb.name
        Object curObject = data;
        for (String nodeName : routePath) {
            if (curObject == null)
                continue;
            curObject = TaskUtils.readProperty(curObject, nodeName);
        }
        return curObject;
    }
}