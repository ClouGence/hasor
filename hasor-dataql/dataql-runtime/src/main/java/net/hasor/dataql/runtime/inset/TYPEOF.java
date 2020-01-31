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
package net.hasor.dataql.runtime.inset;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * TYPEOF   // 计算表达式值的类型。
 *         - 参数说明：共0参数；
 *         - 栈行为：消费1，产出1，产出内容为：string、number、boolean、object、list、udf、null
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-01-24
 */
class TYPEOF implements InsetProcess {
    @Override
    public int getOpcode() {
        return TYPEOF;
    }

    public static enum TypeOfEnum {
        String("string"),   //
        Number("number"),   //
        Boolean("boolean"), //
        Object("object"),   //
        List("list"),       //
        Udf("udf"),         //
        Null("null");       //
        private String typeOfEnum;

        public String typeCode() {
            return typeOfEnum;
        }

        TypeOfEnum(String typeOfEnum) {
            this.typeOfEnum = typeOfEnum;
        }
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        DataModel dataModel = DomainHelper.convertTo(dataStack.pop());
        if (dataModel.isObjectModel()) {
            dataStack.push(TypeOfEnum.Object.typeCode());
            return;
        }
        if (dataModel.isListModel()) {
            dataStack.push(TypeOfEnum.List.typeCode());
            return;
        }
        if (dataModel.isUdfModel()) {
            dataStack.push(TypeOfEnum.Udf.typeCode());
            return;
        }
        if (dataModel.isValueModel()) {
            ValueModel val = (ValueModel) dataModel;
            if (val.isNull()) {
                dataStack.push(TypeOfEnum.Null.typeCode());
                return;
            }
            if (val.isNumber()) {
                dataStack.push(TypeOfEnum.Number.typeCode());
                return;
            }
            if (val.isString()) {
                dataStack.push(TypeOfEnum.String.typeCode());
                return;
            }
            if (val.isBoolean()) {
                dataStack.push(TypeOfEnum.Boolean.typeCode());
                return;
            }
        }
        //
        throw new InstructRuntimeException("DataModel type is unknown.");
    }
}