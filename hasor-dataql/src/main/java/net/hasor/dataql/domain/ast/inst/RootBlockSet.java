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
package net.hasor.dataql.domain.ast.inst;
import net.hasor.dataql.Option;
import net.hasor.dataql.domain.compiler.CompilerStack;
import net.hasor.dataql.domain.compiler.InstQueue;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 指令序列
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public class RootBlockSet extends InstSet {
    private List<OptionInst> optionSet = new ArrayList<>();
    private List<ImportInst> importSet = new ArrayList<>();

    /** 添加选项 */
    public void addOptionInst(OptionInst inst) {
        this.optionSet.add(Objects.requireNonNull(inst, "option inst npe."));
    }

    /** 添加导入 */
    public void addImportInst(ImportInst inst) {
        this.importSet.add(Objects.requireNonNull(inst, "import inst npe."));
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        //        if (this.instList == null || this.instList.isEmpty()) {
        //            return;
        //        }
        //        for (Inst inst : this.instList) {
        //            inst.doCompiler(queue, stackTree);
        //        }
    }

    @Override
    public void doFormat(int depth, Option formatOption, Writer writer) {
        //
    }
}