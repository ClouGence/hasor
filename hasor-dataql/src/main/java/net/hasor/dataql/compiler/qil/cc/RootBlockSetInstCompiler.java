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
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.ast.inst.ImportInst;
import net.hasor.dataql.compiler.ast.inst.OptionInst;
import net.hasor.dataql.compiler.ast.inst.RootBlockSet;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.util.List;

/**
 * 指令序列
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public class RootBlockSetInstCompiler extends InstCompiler<RootBlockSet> {
    @Override
    public void doCompiler(RootBlockSet rootBlockSet, InstQueue queue, CompilerStack stackTree) {
        //
        List<OptionInst> optionSet = rootBlockSet.getOptionSet();
        if (optionSet != null) {
            InstCompiler<OptionInst> compiler = findInstCompilerByInstType(OptionInst.class);
            for (OptionInst optionInst : optionSet) {
                compiler.doCompiler(optionInst, queue, stackTree);
            }
        }
        //
        List<ImportInst> importSet = rootBlockSet.getImportSet();
        if (importSet != null) {
            InstCompiler<ImportInst> compiler = findInstCompilerByInstType(ImportInst.class);
            for (ImportInst importInst : importSet) {
                compiler.doCompiler(importInst, queue, stackTree);
            }
        }
        //
        if (!rootBlockSet.isEmpty()) {
            for (Inst inst : rootBlockSet) {
                findInstCompilerByInst(inst).doCompiler(inst, queue, stackTree);
            }
        }
    }
}