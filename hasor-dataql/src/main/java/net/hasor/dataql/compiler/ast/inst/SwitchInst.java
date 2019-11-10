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
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;
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
        boolean useMultiple = this.elseBlockSet == null || this.elseBlockSet.size() > 1;
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

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (this.testBlockSet.isEmpty()) {
            queue.inst(LDC_N, 0);
            queue.inst(LDC_S, "inst if -> error.");
            queue.inst(ERR);
            return;
        }
        /*
            if (a == b)
                var a = b
            elseif (a == null)
                var b = a
            else
                var c = a
            end

            -----               // if (a == b)
            LABEL   "label_0"
            LOAD    1
            LOAD    2
            DO      "eq"
            IF      "label_1"
            ...
            GOTO    "label_4"
            -----               // elseif (a == null)
            LABEL   "label_1"
            LOAD    1
            LDC_N
            DO      "eq"
            IF      "label_2"
            ...
            GOTO    "label_4"
            -----               // else
            GOTO    "label_2"
            ...
            -----
            GOTO    "label_4"
        */
        //
        // .if 和 elseif
        //
        Label finalLabel = queue.labelDef();  // else出口 Label
        Label lastEnterIn = queue.labelDef(); // 最后一个入口 Label（随着if分支发生变化）
        //
        for (SwitchExpression switchExp : this.testBlockSet) {
            //
            // .标记if分支入口
            queue.inst(LABEL, lastEnterIn);
            //
            // .产生新的入口，流给下一个if分支使用
            lastEnterIn = queue.labelDef();
            //
            // .条件判断
            Expression testExpression = switchExp.testExpression;
            testExpression.doCompiler(queue, stackTree);
            queue.inst(IF, lastEnterIn);//如果判断失败，跳转到下一个Label
            //
            // .if的body
            InstSet instBlockSet = switchExp.instBlockSet;
            instBlockSet.doCompiler(queue, stackTree);
            queue.inst(GOTO, finalLabel);//执行完毕，跳转到总出口
            //
        }
        // .else
        queue.inst(LABEL, lastEnterIn);
        if (this.elseBlockSet != null) {
            this.elseBlockSet.doCompiler(queue, stackTree);
            queue.inst(GOTO, finalLabel);
        }
        // .if 的结束点
        queue.inst(LABEL, finalLabel);
    }
}