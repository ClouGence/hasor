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
import net.hasor.dataql.compiler.ast.token.StringToken;
import net.hasor.dataql.compiler.ast.value.LambdaVariable;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * var指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class VarInst extends CodeLocationInfo implements Inst {
    private final StringToken varName; //变量名
    private final Variable    value;   //变量表达式

    public VarInst(StringToken varName, Variable value) {
        this.varName = varName;
        this.value = value;
    }

    public StringToken getVarName() {
        return varName;
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
        writer.write(fixedString + String.format("var %s = ", this.varName.getValue()));
        this.value.doFormat(depth, formatOption, writer);
        if (this.value instanceof LambdaVariable) {
            return;
        }
        writer.write(";\n");
    }
}