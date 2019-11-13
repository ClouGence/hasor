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
import net.hasor.dataql.compiler.ast.value.SubscriptRouteVariable;
import net.hasor.dataql.compiler.ast.value.SubscriptRouteVariable.SubType;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

/**
 * 对 RouteVariable 的下标操作
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SubscriptRouteVariableInstCompiler implements InstCompiler<SubscriptRouteVariable> {
    @Override
    public void doCompiler(SubscriptRouteVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        compilerContext.findInstCompilerByInst(astInst.getParent()).doCompiler(queue);
        //
        SubType subType = astInst.getSubType();
        String subValue = astInst.getSubValue();
        if (subType == SubType.String) {
            queue.inst(GET, subValue);
        }
        if (subType == SubType.Integer) {
            queue.inst(PULL, Integer.parseInt(subValue));
        }
    }
}