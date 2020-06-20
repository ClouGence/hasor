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
package net.hasor.dataql.compiler.qil.cc;
import net.hasor.dataql.compiler.ast.value.PrimitiveVariable;
import net.hasor.dataql.compiler.ast.value.PrimitiveVariable.ValueType;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

/**
 * 基础类型值，用于表示【String、Number、Null、Boolean】四种基本类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class PrimitiveVariableInstCompiler implements InstCompiler<PrimitiveVariable> {
    @Override
    public void doCompiler(PrimitiveVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        this.instLocation(queue, astInst);
        ValueType valueType = astInst.getValueType();
        if (valueType == ValueType.Boolean) {
            queue.inst(LDC_B, Boolean.parseBoolean(astInst.getValue().toString()));
        }
        if (valueType == ValueType.Null) {
            queue.inst(LDC_N);
        }
        if (valueType == ValueType.Number) {
            queue.inst(LDC_D, astInst.getValue());
        }
        if (valueType == ValueType.String) {
            queue.inst(LDC_S, astInst.getValue().toString());
        }
    }
}