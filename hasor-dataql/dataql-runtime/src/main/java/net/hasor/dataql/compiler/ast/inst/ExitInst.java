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
import net.hasor.dataql.compiler.ast.token.IntegerToken;
import net.hasor.dataql.compiler.ast.value.LambdaVariable;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * exit指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ExitInst extends CodeLocationInfo implements Inst {
    private final IntegerToken exitCode;
    private final Variable     exitData;

    public ExitInst(IntegerToken exitCode, Variable exitData) {
        this.exitCode = exitCode;
        this.exitData = exitData;
    }

    public IntegerToken getExitCode() {
        return exitCode;
    }

    public Variable getExitData() {
        return exitData;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                exitData.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        String fixedString = StringUtils.fixedString(' ', depth * fixedLength);
        //
        if (this.exitCode.getValue() != 0) {
            writer.write(fixedString + String.format("exit %s, ", this.exitCode.getValue()));
        } else {
            writer.write(fixedString + "exit ");
        }
        this.exitData.doFormat(depth + 1, formatOption, writer);
        writer.write((this.exitData instanceof LambdaVariable) ? "\n" : ";\n");
    }
}