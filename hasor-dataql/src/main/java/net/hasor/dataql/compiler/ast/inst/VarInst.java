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
package net.hasor.dataql.compiler.ast.inst;
import net.hasor.dataql.Option;
import net.hasor.dataql.compiler.ast.*;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * var指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class VarInst implements Inst {
    private String   varName; //变量名
    private Variable value;   //变量表达式

    public VarInst(String varName, Variable value) {
        this.varName = varName;
        this.value = value;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                value.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        String fixedString = StringUtils.fixedString(' ', depth * fixedLength);
        //
        writer.write(fixedString + String.format("var %s = ", this.varName));
        this.value.doFormat(depth, formatOption, writer);
        writer.write(";\n");
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        //        // .表达式指令
        //        this.value.doCompiler(queue, stackTree);
        //        //
        //        // .如果当前堆栈中存在该变量的定义，那么直接覆盖
        //        int index = stackTree.containsWithCurrent(this.varName);
        //        if (index >= 0) {
        //            queue.inst(STORE, index);
        //        } else {
        //            int storeIndex = stackTree.push(this.varName);
        //            queue.inst(STORE, storeIndex);
        //        }
    }
}