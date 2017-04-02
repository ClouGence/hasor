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
package net.hasor.graphql.task.struts;
import net.hasor.graphql.QueryResult;
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.result.ValueModel;
import net.hasor.graphql.task.TaskUtils;
import net.hasor.graphql.task.source.SourceQueryTask;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ValueStrutsTask extends StrutsQueryTask {
    private String          fieldName;
    private SourceQueryTask dataSource;
    public ValueStrutsTask(TaskContext taskContext, String fieldName, SourceQueryTask dataSource) {
        super(taskContext);
        this.fieldName = fieldName;
        this.dataSource = dataSource;
        super.addSubTask(dataSource);
    }
    //
    @Override
    protected QueryResult doTask(TaskContext taskContext) throws Throwable {
        Object val = this.dataSource.getValue();
        if (isBasicType(val) || val.getClass() == String.class) {
            //
        } else {
            val = TaskUtils.readProperty(val, this.fieldName);
        }
        //
        return new ValueModel(val);
    }
    private boolean isBasicType(Object val) {
        if (val == null) {
            return true;
        }
        Class<?> aClass = val.getClass();
        if (aClass.isPrimitive() || aClass == String.class) {
            return true;
        }
        if (aClass == Boolean.class || aClass == Boolean.TYPE) {
            return true;
        } else if (aClass == Byte.class || aClass == Byte.TYPE) {
            return true;
        } else if (aClass == Short.class || aClass == Short.TYPE) {
            return true;
        } else if (aClass == Integer.class || aClass == Integer.TYPE) {
            return true;
        } else if (aClass == Long.class || aClass == Long.TYPE) {
            return true;
        } else if (aClass == Float.class || aClass == Float.TYPE) {
            return true;
        } else if (aClass == Double.class || aClass == Double.TYPE) {
            return true;
        } else if (aClass == Character.class || aClass == Character.TYPE) {
            return true;
        }
        return false;
    }
}