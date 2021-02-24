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
package net.hasor.dataql.parser.ast.value;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.ast.*;
import net.hasor.dataql.parser.ast.inst.InstSet;
import net.hasor.dataql.parser.ast.token.StringToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * lambda 函数对象
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class LambdaVariable extends InstSet implements Variable {
    private final Map<String, StringToken> paramMap  = new HashMap<>();
    private final List<StringToken>        paramList = new ArrayList<>();

    public LambdaVariable() {
        super(true);
    }

    /** 添加入参 */
    public void addParam(StringToken name) {
        if (this.paramMap.containsKey(name.getValue())) {
            throw new java.lang.IllegalStateException(name + " param existing.");
        }
        this.paramMap.put(name.getValue(), name);
        this.paramList.add(name);
    }

    public List<StringToken> getParamList() {
        return paramList;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                for (Inst var : LambdaVariable.this) {
                    var.accept(astVisitor);
                }
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        writer.write("(");
        for (int i = 0; i < this.paramList.size(); i++) {
            if (i > 0) {
                writer.write(", ");
            }
            writer.write(this.paramList.get(i).getValue());
        }
        writer.write(") -> ");
        if (this.isMultipleInst()) {
            super.doFormat(depth - 1, formatOption, writer);
        } else {
            super.doFormat(0, formatOption, writer);
        }
    }
}
