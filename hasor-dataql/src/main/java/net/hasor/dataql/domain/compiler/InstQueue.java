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
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * QL 指令序列
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class InstQueue {
    private final int                               name;
    private final AtomicInteger                     labelIndex;
    private final AtomicInteger                     methodName;
    private final List<LinkedList<InstructionInfo>> instSet;
    //
    public InstQueue() {
        this.name = 0;
        this.labelIndex = new AtomicInteger(0);
        this.methodName = new AtomicInteger(0);
        this.instSet = new ArrayList<LinkedList<InstructionInfo>>();
        this.instSet.add(new LinkedList<InstructionInfo>());
    }
    private InstQueue(int methodName, InstQueue dataPool) {
        this.name = methodName;
        this.labelIndex = dataPool.labelIndex;
        this.methodName = dataPool.methodName;
        this.instSet = dataPool.instSet;
    }
    public int getName() {
        return this.name;
    }
    //
    //
    /** 添加指令 */
    public int inst(byte inst, Object... param) {
        //
        // .加入到指令集
        LinkedList<InstructionInfo> instList = this.instSet.get(this.name);
        InstructionInfo instObj = new InstructionInfo(inst, param);
        instList.addLast(instObj);
        int index = instList.size() - 1;
        //
        // .Label指令索引更新
        if (inst == Opcodes.LABEL) {
            for (Object obj : param) {
                if (obj instanceof Label) {
                    ((Label) obj).updateIndex(index);
                }
            }
        }
        return index;
    }
    /** 最后加入的那条指令 */
    public InstructionInfo lastInst() {
        LinkedList<InstructionInfo> instList = this.instSet.get(this.name);
        return instList.isEmpty() ? null : instList.getLast();
    }
    //
    /**新函数指令集*/
    public InstQueue newMethodInst() {
        LinkedList<InstructionInfo> instList = new LinkedList<InstructionInfo>();
        this.instSet.add(instList);
        int name = -1;
        for (int i = 0; i < this.instSet.size(); i++) {
            if (this.instSet.get(i) == instList) {
                name = i;
                break;
            }
        }
        return new InstQueue(name, this);
    }
    //
    public Label labelDef() {
        return new Label(this.labelIndex.incrementAndGet());
    }
    //
    public Instruction[][] buildArrays() throws ParseException {
        for (LinkedList<InstructionInfo> instList : this.instSet) {
            for (InstructionInfo inst : instList) {
                if (!inst.replaceLabel()) {
                    throw new ParseException("compiler error -> inst(" + inst.getInstCode() + ") encounter not insert Label.");
                }
            }
        }
        //
        InstructionInfo[][] buildDatas = new InstructionInfo[this.instSet.size()][];
        for (int i = 0; i < this.instSet.size(); i++) {
            LinkedList<InstructionInfo> instList = this.instSet.get(i);
            InstructionInfo[] instSet = instList.toArray(new InstructionInfo[instList.size()]);
            buildDatas[i] = instSet;
        }
        return buildDatas;
    }
    //
    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        for (int i = 0; i < this.instSet.size(); i++) {
            LinkedList<InstructionInfo> instList = this.instSet.get(i);
            printInstList(i, instList, strBuffer);
        }
        return strBuffer.toString();
    }
    private static void printInstList(int name, LinkedList<InstructionInfo> instList, StringBuilder strBuffer) {
        strBuffer.append("[");
        strBuffer.append(name);
        strBuffer.append("]\n");
        int length = String.valueOf(instList.size()).length();
        for (int i = 0; i < instList.size(); i++) {
            InstructionInfo inst = instList.get(i);
            strBuffer.append("  #");
            strBuffer.append(StringUtils.leftPad(String.valueOf(i), length, '0'));
            strBuffer.append("  ");
            strBuffer.append(inst.toString());
            strBuffer.append("\n");
        }
        strBuffer.append("\n");
    }
}