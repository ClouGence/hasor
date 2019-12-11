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
import net.hasor.dataql.compiler.ast.*;
import net.hasor.dataql.compiler.ast.expr.AtomExpression;
import net.hasor.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectVariable implements Inst, Variable {
    private List<String>          fieldSort;
    private String                objectType;
    private Map<String, Variable> objectData;

    public ObjectVariable() {
        this.fieldSort = new ArrayList<>();
        this.objectType = "";
        this.objectData = new HashMap<>();
    }

    /** 添加字段 */
    public void addField(String fieldName, Variable valueExp) {
        if (StringUtils.isBlank(fieldName) || this.fieldSort.contains(fieldName)) {
            return;
        }
        this.fieldSort.add(fieldName);
        this.objectData.put(fieldName, valueExp);
    }

    public List<String> getFieldSort() {
        return fieldSort;
    }

    public Map<String, Variable> getObjectData() {
        return objectData;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                for (Variable var : objectData.values()) {
                    var.accept(astVisitor);
                }
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        if (this.objectData.isEmpty()) {
            writer.write("{}");
            return;
        }
        depth = (depth == 0) ? 1 : depth;
        String fixedString = StringUtils.fixedString(' ', depth * fixedLength);
        //
        writer.write("{\n");
        for (int i = 0; i < this.fieldSort.size(); i++) {
            if (i > 0) {
                writer.write(",\n");
            }
            String key = this.fieldSort.get(i);
            String newKey = key.replace(String.valueOf(quoteChar), "\\" + quoteChar);
            writer.write(fixedString + quoteChar + newKey + quoteChar);
            //
            Variable variable = this.objectData.get(key);
            if (variable instanceof AtomExpression) {
                variable = ((AtomExpression) variable).getVariableExpression();
            }
            if (variable instanceof NameRouteVariable) {
                NameRouteVariable nameRouteVariable = (NameRouteVariable) variable;
                if (nameRouteVariable.getParent() instanceof EnterRouteVariable && !key.equals(nameRouteVariable.getName())) {
                    writer.write(" : ");
                    variable.doFormat(depth + 1, formatOption, writer);
                }
            } else {
                writer.write(" : ");
                variable.doFormat(depth + 1, formatOption, writer);
            }
        }
        //
        writer.write("\n" + StringUtils.fixedString(' ', (depth - 1) * fixedLength) + "}");
    }
}