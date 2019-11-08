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
package net.hasor.dataql.compiler.ast.inst;
import net.hasor.dataql.Option;
import net.hasor.dataql.compiler.FormatWriter;
import net.hasor.dataql.compiler.InstCompiler;
import net.hasor.dataql.compiler.InstFormat;
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 指令序列
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class InstSet extends ArrayList<Inst> implements InstCompiler, InstFormat {
    /** 批量添加指令集 */
    public void addInstSet(InstSet inst) {
        this.addAll(inst);
    }

    /** 添加一条指令 */
    public void addInst(Inst inst) {
        if (inst != null) {
            this.add(inst);
        }
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        for (Inst inst : this) {
            inst.doFormat(depth, formatOption, writer);
        }
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (this.isEmpty()) {
            return;
        }
        for (Inst inst : this) {
            inst.doCompiler(queue, stackTree);
        }
    }
}