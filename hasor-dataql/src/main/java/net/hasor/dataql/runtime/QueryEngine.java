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
import net.hasor.dataql.*;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.runtime.inset.OpcodesPool;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.utils.Objects;
/**
 * 用于封装和引发 QL 查询执行。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryEngine extends OptionSet implements DataQLEngine, ProcessContet {
    private final static OpcodesPool opcodesPool = OpcodesPool.newPool();
    private       ClassLoader     classLoader;
    private final OperatorManager opeManager;
    private final UdfFinder       udfFinder;
    private final QIL             queryType;
    //
    public QueryEngine(UdfManager udfManager, QIL queryType) {
        Objects.requireNonNull(udfManager, "udfManager is null.");
        Objects.requireNonNull(queryType, "qil is null.");
        //
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.opeManager = OperatorManager.DEFAULT;
        this.udfFinder = new UdfFinder(udfManager);
        this.queryType = queryType;
    }
    //
    @Override
    public QIL getQil() {
        return this.queryType;
    }
    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = (classLoader == null) ? Thread.currentThread().getContextClassLoader() : classLoader;
    }
    @Override
    public UDF findUDF(String udfName) {
        return this.udfFinder.get(udfName);
    }
    @Override
    public OperatorProcess findOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType) {
        return this.opeManager.findOperator(symbolType, symbolName, fstType, secType);
    }
    @Override
    public Class<?> loadType(String type) throws ClassNotFoundException {
        return this.classLoader.loadClass(type);
    }
    //
    //
    /** 创建一个新查询实例。 */
    public Query newQuery() {
        return new QueryInstance(this, this.queryType);
    }
    @Override
    public void processInset(InstSequence sequence, MemStack memStack, StackStruts local) throws ProcessException {
        //
        while (sequence.hasNext()) {
            opcodesPool.doWork(sequence, memStack, local, this);
            sequence.doNext(1);
        }
    }
}