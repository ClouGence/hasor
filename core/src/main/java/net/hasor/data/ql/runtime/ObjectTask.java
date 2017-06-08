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
import net.hasor.data.ql.result.ObjectModel;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectTask extends AbstractPrintTask {
    private List<String> fieldList = new ArrayList<String>();
    public ObjectTask(String nameOfParent, AbstractTask parentTask, AbstractTask dataSource) {
        super(nameOfParent, parentTask, dataSource);
    }
    //
    //
    public void addField(String name, AbstractPrintTask dataSource) {
        this.fieldList.add(name);
        super.addFieldTask(name, dataSource);
    }
    @Override
    public void doExceute(QueryContext taskContext) throws Throwable {
        //
        Object inData = taskContext.getInput();
        ObjectModel objectData = new ObjectModel(this.fieldList);
        taskContext.setOutput(objectData);
        for (String fieldName : this.fieldList) {
            //
            QueryContext fieldContext = taskContext.newStack(fieldName, inData);
            super.findFieldTask(fieldName).doTask(fieldContext);
            //
            Object taskValue = fieldContext.getOutput();
            objectData.put(fieldName, taskValue);
        }
        //
    }
}