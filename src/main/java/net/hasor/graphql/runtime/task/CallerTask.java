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
import net.hasor.graphql.GraphUDF;
import net.hasor.graphql.runtime.QueryContext;
import net.hasor.graphql.runtime.TaskType;

import java.util.HashMap;
import java.util.Map;
/**
 * 执行调用，任务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CallerTask extends AbstractPrintTask {
    private String                         callerName = null;
    private Map<String, AbstractPrintTask> callParams = new HashMap<String, AbstractPrintTask>();
    public CallerTask(String nameOfParent, AbstractTask parentTask, String callerName) {
        super(nameOfParent, parentTask, null);
        this.callerName = callerName;
    }
    //
    //
    public void addParam(String paramName, AbstractPrintTask paramSource) {
        this.callParams.put(paramName, paramSource);
        super.addFieldTask(paramName, paramSource);
    }
    @Override
    public Object doTask(QueryContext taskContext, Object inData) throws Throwable {
        //
        Map<String, Object> values = new HashMap<String, Object>();
        for (Map.Entry<String, AbstractPrintTask> ent : this.callParams.entrySet()) {
            AbstractPrintTask task = ent.getValue();
            Object taskValue = null;
            if (TaskType.F.equals(task.getTaskType())) {
                taskValue = task.doTask(taskContext, inData);
            } else {
                taskValue = task.getValue();
            }
            values.put(ent.getKey(), taskValue);
        }
        GraphUDF graphUdf = taskContext.findUDF(this.callerName);
        if (graphUdf == null) {
            throw new NullPointerException("graphUDF '" + this.callerName + "' is not found.");
        }
        return graphUdf.call(values);
    }
}