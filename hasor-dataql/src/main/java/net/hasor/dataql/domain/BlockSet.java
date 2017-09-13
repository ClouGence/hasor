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

import java.util.ArrayList;
import java.util.List;
/**
 * 指令序列
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class BlockSet implements InstCompiler {
    protected List<Inst> instList = new ArrayList<Inst>();
    public BlockSet() {
    }
    public BlockSet(List<Inst> instList) {
        if (instList != null && !instList.isEmpty()) {
            for (Inst inst : instList) {
                this.addInst(inst);
            }
        }
    }
    //
    /** 批量添加指令集 */
    public void addInstSet(BlockSet inst) {
        this.instList.addAll(inst.instList);
    }
    /** 添加一条指令 */
    public void addInst(Inst inst) {
        if (inst != null) {
            this.instList.add(inst);
        }
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (this.instList == null || this.instList.isEmpty()) {
            return;
        }
        for (Inst inst : this.instList) {
            inst.doCompiler(queue, stackTree);
        }
    }
}