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
package net.hasor.dataql.parser.ast.inst;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.ast.*;
import net.hasor.dataql.parser.ast.token.IntegerToken;
import net.hasor.dataql.parser.ast.value.LambdaVariable;
import net.hasor.dataql.parser.location.BlockLocation;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * throw指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ThrowInst extends BlockLocation implements Inst {
    private final IntegerToken errorCode;
    private final Variable     throwData;

    public ThrowInst(IntegerToken exitCode, Variable exitData) {
        this.errorCode = exitCode;
        this.throwData = exitData;
    }

    public IntegerToken getErrorCode() {
        return errorCode;
    }

    public Variable getThrowData() {
        return throwData;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                throwData.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        String fixedString = StringUtils.repeat(' ', depth * fixedLength);
        //
        if (this.errorCode.getValue() != 0) {
            writer.write(fixedString + String.format("throw %s, ", this.errorCode.getValue()));
        } else {
            writer.write(fixedString + "throw ");
        }
        this.throwData.doFormat(depth + 1, formatOption, writer);
        writer.write((this.throwData instanceof LambdaVariable) ? "\n" : ";\n");
    }
}
