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
package net.hasor.data.ql.domain.inst;
import java.util.LinkedList;
/**
 * QL 指令序列
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class InstQueue {
    private int                     labelIndex = 0;
    private LinkedList<Instruction> instList   = new LinkedList<Instruction>();
    //
    //
    public int inst(byte inst, Object... param) {
        Instruction instObj = new Instruction(inst, param);
        this.instList.addLast(instObj);
        return this.instList.size() - 1;
    }
    //
    public Label labelDef() {
        return new Label(labelIndex++);
    }
    //
    @Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        for (Instruction inst : this.instList) {
            strBuffer.append(inst.toString());
            strBuffer.append("\n");
        }
        return strBuffer.toString();
    }
}