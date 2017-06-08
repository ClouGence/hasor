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
import net.hasor.data.ql.ctx.QueryContext;

import java.util.Arrays;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RouteTask extends AbstractPrintTask {
    private boolean rootSource      = false;
    private String  routeExpression = null;
    public RouteTask(String nameOfParent, AbstractTask parentTask, AbstractTask dataSource) {
        super(nameOfParent, parentTask, dataSource);
    }
    //
    public void setRouteExpression(String routeExpression) {
        this.rootSource = routeExpression.startsWith("%{");
        if (this.rootSource) {
            routeExpression = routeExpression.substring(2, routeExpression.length() - 1);
        }
        this.routeExpression = routeExpression;
    }
    @Override
    public void doExceute(QueryContext taskContext) throws Throwable {
        //
        Object defaultVal = taskContext.getInput();
        if (this.routeExpression.contains(".")) {
            Object evalRoute = this.evalRoute(taskContext, this.routeExpression.split("\\."));
            taskContext.setOutput(evalRoute);
            return;
        }
        //
        if (defaultVal != null) {
            defaultVal = TaskUtils.readProperty(defaultVal, this.routeExpression);
        } else {
            defaultVal = taskContext.get(this.routeExpression);
        }
        //
        taskContext.setOutput(defaultVal);
    }
    private Object evalRoute(final QueryContext taskContext, String[] routeSplit) throws Throwable {
        QueryContext curTask = taskContext;
        Object inData = taskContext.getInput();
        Object outData = taskContext.getOutput();
        //
        // - 根节点
        if (this.rootSource) {
            while (curTask.getParent() != null) {
                curTask = curTask.getParent();
            }
            inData = curTask.getInput();
            outData = curTask.getOutput();
        }
        if (inData == null) {
            while (curTask.getParent() != null) {
                if (curTask.getOutput() != null) {
                    break;
                }
                curTask = curTask.getParent();
            }
        }
        // - 数据源选择
        Object useData = inData;
        if (routeSplit[0].charAt(0) == '~') {
            routeSplit = Arrays.copyOfRange(routeSplit, 1, routeSplit.length);
        }
        if (routeSplit[0].charAt(0) == '$') {
            useData = outData;
            routeSplit = Arrays.copyOfRange(routeSplit, 1, routeSplit.length);
        }
        //
        for (String nodeName : routeSplit) {
            if (useData == null) {
                return null;
            }
            useData = TaskUtils.readProperty(useData, nodeName);
        }
        return useData;
    }
}