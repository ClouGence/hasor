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
 * return指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public class ReturnInst extends BlockLocation implements Inst {
    private final IntegerToken returnCode;
    private final Variable     resultData;

    public ReturnInst(IntegerToken returnCode, Variable resultData) {
        this.returnCode = returnCode;
        this.resultData = resultData;
    }

    public IntegerToken getReturnCode() {
        return returnCode;
    }

    public Variable getResultData() {
        return resultData;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                resultData.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        String fixedString = StringUtils.repeat(' ', depth * fixedLength);
        //
        if (this.returnCode.getValue() != 0) {
            writer.write(fixedString + String.format("return %s, ", this.returnCode.getValue()));
        } else {
            writer.write(fixedString + "return ");
        }
        this.resultData.doFormat(depth + 1, formatOption, writer);
        writer.write((this.resultData instanceof LambdaVariable) ? "\n" : ";\n");
    }
}
