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
import net.hasor.graphql.QueryContext;
import net.hasor.graphql.runtime.AbstractQueryTask;
import net.hasor.graphql.runtime.TaskType;
import net.hasor.graphql.runtime.TaskUtils;

import java.util.Arrays;
import java.util.List;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RouteSourceTask extends AbstractQueryTask {
    private String routeExpression;
    public RouteSourceTask(String nameOfParent, AbstractQueryTask dataSource, boolean isFormater) {
        super(nameOfParent, (isFormater ? TaskType.F : TaskType.V), dataSource);
    }
    //
    //
    //
    public void setRouteExpression(String routeExpression) {
        this.routeExpression = routeExpression;
    }
    //
    @Override
    public Object doTask(QueryContext taskContext, Object inData) throws Throwable {
        String[] routePath = this.routeExpression.split("\\.");
        //
        if (inData == null) {
            List<AbstractQueryTask> subList = super.getSubList();
            for (AbstractQueryTask task : subList) {
                if (!task.isFinish()) {
                    continue;
                }
                String nameOfParent = task.getNameOfParent();
                if (routePath[0].equals(nameOfParent)) {
                    routePath = Arrays.copyOfRange(routePath, 1, routePath.length);
                    return evalRoute(task.getValue(), routePath);
                }
            }
            return taskContext.get(this.routeExpression);
        }
        return this.evalRoute(inData, routePath);
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