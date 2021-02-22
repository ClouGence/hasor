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
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.ast.*;
import net.hasor.dataql.compiler.ast.CodeLocation.CodeLocationInfo;
import net.hasor.dataql.compiler.ast.value.LambdaVariable;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * assert指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class AssertInst extends CodeLocationInfo implements Inst {
    private final Variable value;   //执行表达式

    public AssertInst(Variable value) {
        this.value = value;
    }

    public Variable getValue() {
        return value;
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
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        String fixedString = StringUtils.repeat(' ', depth * fixedLength);
        //
        writer.write(fixedString + "assert ");
        this.value.doFormat(depth, formatOption, writer);
        writer.write((this.value instanceof LambdaVariable) ? "\n" : ";\n");
    }
}
