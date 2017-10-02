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
package net.hasor.dataql.runtime;
import net.hasor.dataql.Option;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.UDF;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.LambdaCall;
/**
 * 代理 Lambda 使其成为 UDF.
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class LambdaCallProxy implements UDF {
    private InstSequence  instSequence;
    private MemStack      memStack;
    private StackStruts   localData;
    private ProcessContet context;
    //
    public LambdaCallProxy(InstSequence instSequence, MemStack memStack, StackStruts localData, ProcessContet context) {
        try {
            this.instSequence = instSequence;
            this.memStack = memStack.clone();
            this.localData = localData.clone();
            this.context = context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //
    @Override
    public Object call(Object[] values, Option readOnly) throws ProcessException, CloneNotSupportedException {
        //
        int address = this.instSequence.getAddress();
        InstSequence instSequence = this.instSequence.clone();
        MemStack memStack = this.memStack.create(address);
        StackStruts localData = this.localData.clone();
        //
        LambdaCall callInfo = new LambdaCall(address, values);
        memStack.push(callInfo);
        this.context.processInset(instSequence, memStack, localData);
        return memStack.getResult();
    }
}