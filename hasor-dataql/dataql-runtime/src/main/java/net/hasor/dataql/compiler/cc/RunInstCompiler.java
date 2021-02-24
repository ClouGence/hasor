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
import net.hasor.dataql.parser.ast.Variable;
import net.hasor.dataql.parser.ast.inst.RunInst;

/**
 * Run
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RunInstCompiler implements InstCompiler<RunInst> {
    @Override
    public void doCompiler(RunInst astInst, InstQueue queue, CompilerContext compilerContext) {
        Variable varValue = astInst.getValue();
        compilerContext.findInstCompilerByInst(varValue).doCompiler(queue);
        //
        instLocation(queue, astInst);
        queue.inst(POP);
    }
}
