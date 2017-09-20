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
import net.hasor.dataql.Option;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.UDF;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
/**
 * 指令执行器接口
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-14
 */
public interface ProcessContet extends Option {
    public Class<?> loadType(String type) throws ClassNotFoundException;

    public void processInset(InstSequence sequence, MemStack memStack, StackStruts local) throws ProcessException;

    public OperatorProcess findOperator(Symbol unary, String dyadicSymbol, Class<?> fstType, Class<?> secType);

    public UDF findUDF(String udfName);
}