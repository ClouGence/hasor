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
import net.hasor.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * if指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SwitchInst implements Inst {
    public static class SwitchExpression {
        private Expression testExpression;
        private InstSet    instBlockSet;
    }

    private List<SwitchExpression> testBlockSet = new ArrayList<>();
    private InstSet                elseBlockSet = null;

    public List<SwitchExpression> getTestBlockSet() {
        return testBlockSet;
    }

    public InstSet getElseBlockSet() {
        return elseBlockSet;
    }

    /** 添加条件分支 */
    public void addElseif(Expression testExp, InstSet blockSet) {
        SwitchExpression se = new SwitchExpression();
        se.testExpression = testExp;
        se.instBlockSet = blockSet;
        this.testBlockSet.add(se);
    }

    /** 设置默认条件分支 */
    public void setElseBlockSet(InstSet instBlockSet) {
        this.elseBlockSet = instBlockSet;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                for (SwitchExpression switchExpr : testBlockSet) {
                    switchExpr.testExpression.accept(astVisitor);
                    switchExpr.instBlockSet.accept(astVisitor);
                }
                if (elseBlockSet != null) {
                    elseBlockSet.accept(astVisitor);
                }
            }
        });
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        String fixedString = StringUtils.fixedString(' ', depth * fixedLength);
        boolean useMultiple = false;
        if (this.elseBlockSet != null && this.elseBlockSet.size() > 1) {
            useMultiple = true;
        }
        for (SwitchExpression switchExpr : this.testBlockSet) {
            if (useMultiple) {
                break;
            }
            useMultiple = switchExpr.instBlockSet.size() > 1;
        }
        //
        //
        writer.write(fixedString + "if (");
        SwitchExpression switchExpr = this.testBlockSet.get(0);
        switchExpr.testExpression.doFormat(depth + 1, formatOption, writer);
        if (useMultiple) {
            writer.write(") {\n");
        } else {
            writer.write(")\n");
        }
        switchExpr.instBlockSet.doFormat(depth + 1, formatOption, writer);
        //
        //
        for (int i = 1; i < this.testBlockSet.size(); i++) {
            switchExpr = this.testBlockSet.get(i);
            //
            if (useMultiple) {
                writer.write(fixedString + "} else if (");
            } else {
                writer.write(fixedString + "else if (");
            }
            switchExpr.testExpression.doFormat(depth + 1, formatOption, writer);
            if (useMultiple) {
                writer.write(") {\n");
            } else {
                writer.write(")\n");
            }
            switchExpr.instBlockSet.doFormat(depth + 1, formatOption, writer);
        }
        if (this.elseBlockSet == null) {
            if (useMultiple) {
                writer.write("}\n");
            }
            return;
        }
        //
        if (useMultiple) {
            writer.write(fixedString + "} else {\n");
        } else {
            writer.write(fixedString + "else\n");
        }
        this.elseBlockSet.doFormat(depth + 1, formatOption, writer);
        if (useMultiple) {
            writer.write("}\n");
        } else {
            writer.write("\n");
        }
    }
}