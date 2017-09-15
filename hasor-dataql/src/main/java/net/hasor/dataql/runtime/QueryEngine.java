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
import net.hasor.dataql.OperatorProcess;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.Query;
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.runtime.inset.OpcodesPool;
import net.hasor.dataql.runtime.mem.LocalData;
import net.hasor.dataql.runtime.mem.MemStack;
/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryEngine extends OptionSet implements ProcessContet {
    private final static OpcodesPool     opcodesPool = OpcodesPool.newPool();
    private final        UdfManager      udfManager  = new UdfManager();
    private final        OperatorManager opeManager  = new OperatorManager();
    private final QIL          queryType;
    private final QueryRuntime runtime;
    //
    QueryEngine(QueryRuntime runtime, QIL queryType) {
        super(runtime);
        this.runtime = runtime;
        this.queryType = queryType;
    }
    //
    /** 添加 UDF */
    public void addQueryUDF(String udfName, UDF udf) {
        this.udfManager.addUDF(udfName, udf);
    }
    @Override
    public UDF findUDF(String udfName) {
        UDF udf = this.udfManager.findUDF(udfName);
        if (udf == null) {
            udf = this.runtime.findUDF(udfName);
        }
        return udf;
    }
    @Override
    public OperatorProcess findOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType) {
        //
        OperatorProcess operator = this.opeManager.findOperator(symbolType, symbolName, fstType, secType);
        if (operator == null) {
            operator = this.runtime.findOperator(symbolType, symbolName, fstType, secType);
        }
        return operator;
    }
    //
    /** 创建一个新查询实例。 */
    public Query newQuery() {
        return new QueryInstance(this, this.queryType);
    }
    //
    @Override
    public Class<?> loadType(String type) throws ClassNotFoundException {
        return this.runtime.loadClass(type);
    }
    @Override
    public void processInset(InstSequence sequence, MemStack memStack, LocalData local) throws ProcessException {
        //
        while (sequence.hasNext()) {
            opcodesPool.doWork(sequence, memStack, local, this);
            sequence.doNext(1);
        }
    }
}