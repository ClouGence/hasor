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
package net.hasor.dataql.runtime.mem;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.inset.OpcodesPool;

/**
 * 代理 Lambda 使其成为 UDF.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RefLambdaCall implements UDF {
    private InstSequence        instSequence;
    private DataHeap            dataHeap;
    private DataStack           dataStack;
    private EnvStack            envStack;
    private InsetProcessContext context;

    public RefLambdaCall(InstSequence instSequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        this.instSequence = instSequence.clone();
        this.dataHeap = dataHeap.clone();
        this.dataStack = dataStack.clone();
        this.envStack = envStack.clone();
        this.context = context;
    }

    @Override
    public Object call(Object[] values, Option readOnly) throws InstructRuntimeException {
        //
        RefLambdaCallStruts callStruts = new RefLambdaCallStruts(values);
        this.dataStack.push(callStruts);
        //
        OpcodesPool opcodesPool = new OpcodesPool();
        while (this.instSequence.hasNext()) {
            opcodesPool.doWork(         //
                    this.instSequence,  //
                    this.dataHeap,      //
                    this.dataStack,     //
                    this.envStack,      //
                    this.context        //
            );
            this.instSequence.doNext(1);
        }
        return dataStack.getResult();// 针对 Lambda 的函数调用，调用入口是无法拿到函数执行完毕的退出码。 退出码的设计目标是为了宿主机在调用的时使用。
    }
}