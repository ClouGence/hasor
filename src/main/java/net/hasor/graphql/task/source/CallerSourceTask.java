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
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.UDF;
import net.hasor.graphql.task.AbstractQueryTask;

import java.util.HashMap;
import java.util.Map;
/**
 * 执行调用，任务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CallerSourceTask extends SourceQueryTask {
    private String                         callerName = null;
    private Map<String, AbstractQueryTask> callParams = new HashMap<String, AbstractQueryTask>();
    //
    public CallerSourceTask(String nameOfParent, TaskContext taskContext, String callerName) {
        super(taskContext, nameOfParent);
        this.callerName = callerName;
    }
    //
    public void addParam(String paramName, AbstractQueryTask dataSource) {
        this.callParams.put(paramName, dataSource);
        super.addSubTask(dataSource);
    }
    public String getCallerName() {
        return this.callerName;
    }
    //
    @Override
    protected Object doTask(TaskContext taskContext) throws Throwable {
        //
        Map<String, Object> values = new HashMap<String, Object>();
        for (Map.Entry<String, AbstractQueryTask> ent : this.callParams.entrySet()) {
            values.put(ent.getKey(), ent.getValue().getValue());
        }
        UDF udf = taskContext.findUDF(this.getCallerName());
        return udf.call(values);
    }
}