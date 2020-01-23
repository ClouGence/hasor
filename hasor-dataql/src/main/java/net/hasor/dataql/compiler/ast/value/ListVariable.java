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
package net.hasor.dataql.compiler.ast.value;
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.InstVisitorContext;
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ListVariable implements Variable {
    private List<Variable> expressionList = new ArrayList<>();

    /** 添加元素 */
    public void addItem(Variable valueExp) {
        if (valueExp != null) {
            this.expressionList.add(valueExp);
        }
    }

    public List<Variable> getExpressionList() {
        return expressionList;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                for (Variable var : expressionList) {
                    var.accept(astVisitor);
                }
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        if (this.expressionList.isEmpty()) {
            writer.write("[]");
            return;
        }
        String fixedString = StringUtils.fixedString(' ', depth * fixedLength);
        boolean innerLine = this.expressionList.stream().allMatch(variable -> variable instanceof PrimitiveVariable);
        if (innerLine) {
            fixedString = "";
        } else {
            fixedString = "\n" + fixedString;
        }
        //
        writer.write("[" + fixedString);
        for (int i = 0; i < this.expressionList.size(); i++) {
            if (i > 0) {
                writer.write("," + fixedString);
            }
            Variable expr = this.expressionList.get(i);
            if (expr instanceof EnterRouteVariable) {
                writer.write(((EnterRouteVariable) expr).getSpecialType().getCode());
            } else {
                expr.doFormat(depth + 1, formatOption, writer);
            }
        }
        //
        if (innerLine) {
            writer.write("]");
        } else {
            writer.write("\n" + StringUtils.fixedString(' ', (depth - 1) * fixedLength) + "]");
        }
    }
}