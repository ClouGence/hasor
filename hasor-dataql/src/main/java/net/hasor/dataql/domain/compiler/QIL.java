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
import net.hasor.utils.StringUtils;
/**
 * Query intermediate language 中间查询语言
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class QIL {
    private Instruction[][] queueSet;
    //
    QIL(Instruction[][] queueSet) {
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
            strBuffer.append(StringUtils.leftPad(String.valueOf(i), length, '0'));
            strBuffer.append("  ");
            strBuffer.append(inst.toString());
            strBuffer.append("\n");
        }
        strBuffer.append("\n");
    }
    //
    //
    /** 方法总数 */
    public int iqlPoolSize() {
        return this.queueSet.length;
    }
    /** 方法的指令序列长度 */
    public int iqlSize(int address) {
        return this.queueSet[address].length;
    }
    /** 获取指令 */
    public Instruction instOf(int address, int index) {
        return this.queueSet[address][index];
    }
    /** 获取方法指令序列的迭代器 */
    public Instruction[] iqlArrays(int address) {
        return this.queueSet[address].clone();
    }
}