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
import net.hasor.dataql.compiler.ast.value.FragmentVariable;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.util.List;

import static net.hasor.dataql.compiler.qil.CompilerContext.ContainsIndex;

/**
 * Fragment 片段
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class FragmentVariableInstCompiler implements InstCompiler<FragmentVariable> {
    @Override
    public void doCompiler(FragmentVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        InstQueue newMethodInst = queue.newMethodInst();
        List<String> paramList = astInst.getParamList();
        compilerContext.newFrame();
        // .LOCAL 变量表
        for (int i = 0; i < paramList.size(); i++) {
            String name = paramList.get(i);
            int index = compilerContext.push(name);     //将变量名压栈，并返回栈中的位置
            newMethodInst.inst(LOCAL, i, index, name);  //为栈中某个位置的变量命名
        }
        // .声明片段入口
        newMethodInst.inst(M_FRAG, astInst.getFragmentName());
        //  .加载入参变量
        for (int i = 0; i < paramList.size(); i++) {
            String name = paramList.get(i);
            ContainsIndex index = compilerContext.containsWithTree(name);
            newMethodInst.inst(LOAD, index.depth, index.index);
        }
        // .最后一个参数是的片段内容
        newMethodInst.inst(LDC_S, astInst.getFragmentString());
        // .执行函数调用
        int paramCount = paramList.size() + 1;
        newMethodInst.inst(CALL, paramCount);
        newMethodInst.inst(RETURN, 0);
        compilerContext.dropFrame();
        //
        // .指向函数的指针
        queue.inst(M_REF, newMethodInst.getName());
    }
}