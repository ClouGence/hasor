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
package net.hasor.dataql.domain;
import net.hasor.dataql.domain.compiler.CompilerStack;
import net.hasor.dataql.domain.compiler.InstQueue;
/**
 * 基础类型值，用于表示【String、Number、Null、Boolean】四种基本类型
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class PrimitiveExpression extends Expression {
    public static enum ValueType {
        Boolean, Number, String, Null
    }
    //
    private Object    value;
    private ValueType valueType;
    //
    public PrimitiveExpression(Object value, ValueType valueType) {
        this.value = value;
        this.valueType = valueType;
    }
    public Object getValue() {
        return this.value;
    }
    public ValueType getValueType() {
        return this.valueType;
    }
    @Override
    public String toString() {
        return "Primitive - '" + this.value + "'";
    }
    //
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (this.valueType == ValueType.Boolean) {
            queue.inst(LDC_B, this.value);
        }
        if (this.valueType == ValueType.Null) {
            queue.inst(LDC_N, this.value);
        }
        if (this.valueType == ValueType.Number) {
            queue.inst(LDC_D, this.value);
        }
        if (this.valueType == ValueType.String) {
            queue.inst(LDC_S, this.value);
        }
    }
}