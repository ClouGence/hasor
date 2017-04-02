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
import net.hasor.graphql.ListResult;
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.result.ListModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ListStrutsTask extends StrutsQueryTask {
    private StrutsQueryTask listBody;
    public ListStrutsTask(TaskContext taskContext, StrutsQueryTask listBody) {
        super(taskContext);
        super.addSubTask(listBody);
        this.listBody = listBody;
    }
    //
    @Override
    protected ListResult doTask(TaskContext taskContext) throws Throwable {
        Object value = this.listBody.getValue();
        Collection<Object> listData = null;
        if (value == null) {
            listData = new ArrayList<Object>();
        } else {
            if (!(value instanceof Collection)) {
                if (value.getClass().isArray()) {
                    listData = new ArrayList<Object>();
                    for (Object obj : (Object[]) value) {
                        listData.add(obj);
                    }
                } else {
                    listData = Arrays.asList(value);
                }
            } else {
                listData = (Collection<Object>) value;
            }
        }
        return new ListModel(listData);
    }
}