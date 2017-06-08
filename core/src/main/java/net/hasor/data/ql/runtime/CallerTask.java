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
import net.hasor.core.Hasor;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.Var;
import net.hasor.data.ql.dsl.domain.EqType;

import java.util.HashMap;
import java.util.Map;
/**
 * 执行调用，任务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CallerTask extends AbstractPrintTask {
    private String                    callerName = null;
    private Map<String, EqType>       varEqTypes = new HashMap<String, EqType>();
    private Map<String, AbstractTask> callParams = new HashMap<String, AbstractTask>();
    public CallerTask(String nameOfParent, AbstractTask parentTask, String callerName) {
        super(nameOfParent, parentTask, null);
        this.callerName = callerName;
    }
    //
    //
    public void addParam(String paramName, EqType eqType, AbstractTask paramSource) {
        eqType = Hasor.assertIsNotNull(eqType);
        paramSource = Hasor.assertIsNotNull(paramSource);
        //
        this.varEqTypes.put(paramName, eqType);
        this.callParams.put(paramName, paramSource);
        super.addFieldTask(paramName, paramSource);
    }
    @Override
    protected void doExceute(QueryContext taskContext) throws Throwable {
        //
        Object inData = taskContext.getInput();
        Map<String, Var> values = new HashMap<String, Var>();
        for (Map.Entry<String, AbstractTask> ent : this.callParams.entrySet()) {
            String keyName = ent.getKey();
            QueryContext paramContext = taskContext.newStack(keyName, inData);
            //
            ent.getValue().doTask(paramContext);
            Object paramValue = paramContext.getOutput();
            //
            EqType eqType = this.varEqTypes.get(keyName);
            values.put(keyName, new Var(eqType, paramValue));
        }
        UDF dataUDF = taskContext.findUDF(this.callerName);
        if (dataUDF == null) {
            throw new NullPointerException("dataUDF '" + this.callerName + "' is not found.");
        }
        Object callData = dataUDF.call(values);
        taskContext.setOutput(callData);
    }
}