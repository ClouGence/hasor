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
import net.hasor.dataql.compiler.ast.CodeLocation.CodeLocationInfo;
import net.hasor.dataql.compiler.ast.expr.AtomExpression;
import net.hasor.dataql.compiler.ast.token.StringToken;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.SpecialType;
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
public class ObjectVariable extends CodeLocationInfo implements Inst, Variable {
    private final List<String>             fieldSort;
    private final String                   objectType;
    private final Map<String, StringToken> objectKey;
    private final Map<String, Variable>    objectData;

    public ObjectVariable() {
        this.fieldSort = new ArrayList<>();
        this.objectType = "";
        this.objectKey = new HashMap<>();
        this.objectData = new HashMap<>();
    }

    /** 添加字段 */
    public void addField(StringToken fieldName, Variable valueExp) {
        String nameValue = fieldName.getValue();
        if (StringUtils.isBlank(nameValue) || this.fieldSort.contains(nameValue)) {
            return;
        }
        this.fieldSort.add(nameValue);
        this.objectKey.put(nameValue, fieldName);
        this.objectData.put(nameValue, valueExp);
    }

    public List<String> getFieldSort() {
        return fieldSort;
    }

    public Map<String, StringToken> getObjectKeys() {
        return this.objectKey;
    }

    public Map<String, Variable> getObjectValues() {
        return this.objectData;
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
        String fixedString = StringUtils.repeat(' ', depth * fixedLength);
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
                if (!key.equals(nameRouteVariable.getName().getValue())) {
                    writer.write(" : ");
                    variable.doFormat(depth + 1, formatOption, writer);
                } else {
                    RouteVariable tempParent = nameRouteVariable.getParent();
                    if (tempParent instanceof NameRouteVariable && StringUtils.isBlank(((NameRouteVariable) tempParent).getName().getValue())) {
                        tempParent = tempParent.getParent();
                    }
                    if (tempParent instanceof EnterRouteVariable) {
                        SpecialType specialType = ((EnterRouteVariable) tempParent).getSpecialType();
                        if (specialType != null && specialType != SpecialType.Special_A) {
                            writer.write(" : ");
                            variable.doFormat(depth + 1, formatOption, writer);
                        }
                    }
                }
            } else {
                writer.write(" : ");
                if (variable instanceof EnterRouteVariable) {
                    writer.write(((EnterRouteVariable) variable).getSpecialType().getCode());
                } else {
                    variable.doFormat(depth + 1, formatOption, writer);
                }
            }
        }
        //
        writer.write("\n" + StringUtils.repeat(' ', (depth - 1) * fixedLength) + "}");
    }
}