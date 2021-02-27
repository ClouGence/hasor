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
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.inset.OpcodesPool;

/**
 * 代理 Lambda 使其成为 UDF.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RefLambdaCall implements Udf {
    private final InstSequence        instSequence;
    private final DataHeap            dataHeap;
    private final EnvStack            envStack;
    private final InsetProcessContext context;

    public RefLambdaCall(InstSequence instSequence, DataHeap dataHeap, EnvStack envStack, InsetProcessContext context) {
        this.instSequence = instSequence;
        this.dataHeap = dataHeap;
        this.envStack = envStack;
        this.context = context;
    }

    @Override
    public Object call(Hints readOnly, Object... params) throws Throwable {
        //
        DataStack cloneStack = new DataStack() {{
            push(new RefLambdaCallStruts(params));
        }};
        //
        InstSequence instSequence = this.instSequence.clone();
        OpcodesPool opcodesPool = OpcodesPool.defaultOpcodesPool();
        DataHeap dataHeap = new DataHeap(this.dataHeap);
        while (instSequence.hasNext()) {
            opcodesPool.doWork(     //
                    instSequence,   //
                    dataHeap,       //
                    cloneStack,     //
                    this.envStack,  //
                    this.context    //
            );
            instSequence.doNext(1);
        }
        DataModel result = cloneStack.getResult();
        if (cloneStack.getExitType() != ExitType.Throw) {
            return (result != null) ? result.unwrap() : DomainHelper.nullDomain();
        } else {
            throw new RefLambdaCallException(       //
                    instSequence.programLocation(), //
                    cloneStack.getResultCode(),     //
                    cloneStack.getResult()          //
            );
        }
    }
}
