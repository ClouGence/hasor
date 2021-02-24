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
import net.hasor.dataql.parser.ast.inst.VarInst;
import net.hasor.dataql.parser.ast.token.StringToken;

/**
 * var指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class VarInstCompiler implements InstCompiler<VarInst> {
    @Override
    public void doCompiler(VarInst astInst, InstQueue queue, CompilerContext compilerContext) {
        // .如果当前堆栈中存在该变量的定义，那么直接覆盖。否则新增一个本地变量
        StringToken varNameToken = astInst.getVarName();
        String varName = varNameToken.getValue();
        int index = compilerContext.containsWithCurrent(varName);
        if (index < 0) {
            index = compilerContext.push(varName);
        }
        //
        // .编译表达式
        Variable varValue = astInst.getValue();
        compilerContext.findInstCompilerByInst(varValue).doCompiler(queue);
        //
        instLocation(queue, varNameToken);
        queue.inst(STORE, index);
    }
}
