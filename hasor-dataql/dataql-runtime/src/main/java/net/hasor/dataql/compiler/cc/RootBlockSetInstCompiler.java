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
package net.hasor.dataql.compiler.cc;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.parser.ast.Inst;
import net.hasor.dataql.parser.ast.inst.HintInst;
import net.hasor.dataql.parser.ast.inst.ImportInst;
import net.hasor.dataql.parser.ast.inst.RootBlockSet;

import java.util.List;

/**
 * 指令序列
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public class RootBlockSetInstCompiler implements InstCompiler<RootBlockSet> {
    @Override
    public void doCompiler(RootBlockSet rootBlockSet, InstQueue queue, CompilerContext compilerContext) {
        List<HintInst> optionSet = rootBlockSet.getOptionSet();
        if (optionSet != null) {
            for (HintInst hintInst : optionSet) {
                compilerContext.findInstCompilerByInst(hintInst).doCompiler(queue);
            }
        }
        List<ImportInst> importSet = rootBlockSet.getImportSet();
        if (importSet != null) {
            for (ImportInst importInst : importSet) {
                compilerContext.findInstCompilerByInst(importInst).doCompiler(queue);
            }
        }
        if (!rootBlockSet.isEmpty()) {
            for (Inst inst : rootBlockSet) {
                compilerContext.findInstCompilerByInst(inst).doCompiler(queue);
            }
        }
    }
}
