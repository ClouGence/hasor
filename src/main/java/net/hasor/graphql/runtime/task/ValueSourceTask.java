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
import net.hasor.graphql.dsl.domain.ValueType;
import net.hasor.graphql.runtime.AbstractQueryTask;
import net.hasor.graphql.runtime.QueryContext;
import net.hasor.graphql.runtime.TaskType;
/**
 * 固定值，任务。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ValueSourceTask extends AbstractQueryTask {
    private Object    value;
    private ValueType valueType;
    public ValueSourceTask(String nameOfParent, Object value, ValueType valueType) {
        super(nameOfParent, TaskType.V, null);
        this.value = value;
        this.valueType = valueType;
    }
    @Override
    public Object doTask(QueryContext taskContext, Object inData) throws Throwable {
        return this.value;
    }
}