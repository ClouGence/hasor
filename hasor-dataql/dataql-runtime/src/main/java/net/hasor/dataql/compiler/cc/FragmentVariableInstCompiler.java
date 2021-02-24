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
import net.hasor.dataql.parser.ast.token.StringToken;
import net.hasor.dataql.parser.ast.value.FragmentVariable;

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
        List<StringToken> paramList = astInst.getParamList();
        boolean isBatch = astInst.isBatchMode();
        compilerContext.newFrame();
        // .LOCAL 变量表
        for (int i = 0; i < paramList.size(); i++) {
            StringToken paramToken = paramList.get(i);
            String name = paramToken.getValue();
            int index = compilerContext.push(name);     //将变量名压栈，并返回栈中的位置
            instLocation(newMethodInst, paramToken);
            newMethodInst.inst(LOCAL, i, index, name);  //为栈中某个位置的变量命名
        }
        // .声明片段入口
        StringToken fragmentName = astInst.getFragmentName();
        instLocation(newMethodInst, fragmentName);
        newMethodInst.inst(M_FRAG, isBatch, fragmentName.getValue());
        //  .加载入参变量
        newMethodInst.inst(NEW_O);
        for (StringToken stringToken : paramList) {
            String name = stringToken.getValue();
            ContainsIndex index = compilerContext.containsWithTree(name);
            instLocation(newMethodInst, stringToken);
            newMethodInst.inst(LOAD, index.depth, index.index);
            newMethodInst.inst(PUT, name);
        }
        // .最后一个参数是的片段内容
        StringToken fragmentToken = astInst.getFragmentString();
        instLocation(newMethodInst, fragmentToken);
        newMethodInst.inst(LDC_S, fragmentToken.getValue());
        // .执行函数调用
        instLocation(newMethodInst, astInst);
        newMethodInst.inst(CALL, 2);
        newMethodInst.inst(RETURN, 0);
        compilerContext.dropFrame();
        //
        // .指向函数的指针
        instLocation(queue, astInst);
        queue.inst(M_REF, newMethodInst.getName());
    }
}
