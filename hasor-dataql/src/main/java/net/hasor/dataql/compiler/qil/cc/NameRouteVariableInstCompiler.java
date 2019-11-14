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
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
import net.hasor.dataql.compiler.ast.value.NameRouteVariable;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.CompilerContext.ContainsIndex;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

/**
 * 编译 NameRouteVariable
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class NameRouteVariableInstCompiler implements InstCompiler<NameRouteVariable> {
    @Override
    public void doCompiler(NameRouteVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        String astInstName = astInst.getName();
        if (astInst.getParent() instanceof EnterRouteVariable) {
            EnterRouteVariable parent = (EnterRouteVariable) astInst.getParent();
            if (parent.getRouteType() == RouteType.Context) {
                ContainsIndex withTree = compilerContext.containsWithTree(astInstName);
                if (withTree.isValid()) {
                    queue.inst(LOAD, withTree.depth, withTree.index);
                    return;
                }
            }
        }
        //
        compilerContext.findInstCompilerByInst(astInst.getParent()).doCompiler(queue);
        queue.inst(GET, astInstName);
    }
}