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
import net.hasor.dataql.compiler.ast.inst.InstSet;
import net.hasor.dataql.compiler.ast.value.LambdaVariable;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.util.List;

/**
 * lambda 函数对象
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class LambdaVariableInstCompiler implements InstCompiler<LambdaVariable> {
    @Override
    public void doCompiler(LambdaVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        //
        // .声明函数参数的变量位置
        List<String> paramList = astInst.getParamList();
        InstQueue newMethodInst = queue.newMethodInst();
        compilerContext.newFrame();
        for (int i = 0; i < paramList.size(); i++) {
            String name = paramList.get(i);
            int index = compilerContext.push(name);//将变量名压栈，并返回栈中的位置
            newMethodInst.inst(LOCAL, i, index, name);  //为栈中某个位置的变量命名
        }
        compilerContext.findInstCompilerByInst(astInst, InstSet.class).doCompiler(newMethodInst);
        compilerContext.dropFrame();
        //
        // .指向函数的指针
        queue.inst(M_REF, newMethodInst.getName());
    }
}