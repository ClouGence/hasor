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
package net.hasor.dataql.domain.compiler;
import net.hasor.dataql.utils.Objects;
/**
 * DataQL 查询
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class QueryType {
    private Instruction[][] queueSet;
    //
    QueryType(Instruction[][] queueSet) {
        this.queueSet = queueSet;
    }
    //
    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        for (int i = 0; i < this.queueSet.length; i++) {
            Instruction[] instList = this.queueSet[i];
            printInstList(i, instList, strBuffer);
        }
        return strBuffer.toString();
    }
    private static void printInstList(int name, Instruction[] instList, StringBuilder strBuffer) {
        strBuffer.append("[");
        strBuffer.append(name);
        strBuffer.append("]\n");
        int length = String.valueOf(instList.length).length();
        for (int i = 0; i < instList.length; i++) {
            Instruction inst = instList[i];
            strBuffer.append("  #");
            strBuffer.append(Objects.leftPad(String.valueOf(i), length, '0'));
            strBuffer.append("  ");
            strBuffer.append(inst.toString());
            strBuffer.append("\n");
        }
        strBuffer.append("\n");
    }
    public Instruction[][] getArrays() {
        return this.queueSet;
    }
}
