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
package net.hasor.dataql.compiler.qil.cc;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.token.StringToken;
import net.hasor.dataql.compiler.ast.value.SubscriptRouteVariable;
import net.hasor.dataql.compiler.ast.value.SubscriptRouteVariable.SubType;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;

/**
 * 对 RouteVariable 的下标操作
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SubscriptRouteVariableInstCompiler implements InstCompiler<SubscriptRouteVariable> {
    @Override
    public void doCompiler(SubscriptRouteVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        compilerContext.findInstCompilerByInst(astInst.getParent()).doCompiler(queue);
        //
        SubType subType = astInst.getSubType();
        if (subType == SubType.String) {
            StringToken subValue = astInst.getSubValue();
            instLocation(queue, subValue);
            queue.inst(GET, subValue.getValue());
        }
        if (subType == SubType.Integer) {
            StringToken subValue = astInst.getSubValue();
            instLocation(queue, subValue);
            queue.inst(PULL, Integer.parseInt(subValue.getValue()));
        }
        if (subType == SubType.Expr) {
            Label nextLabel = null;
            Label finalLabel = queue.labelDef();
            //
            Expression exprValue = astInst.getExprValue();
            compilerContext.findInstCompilerByInst(exprValue).doCompiler(queue);
            instLocation(queue, exprValue);
            queue.inst(COPY);   // 表达式值Copy 一份用来计算 typeof
            queue.inst(TYPEOF);
            queue.inst(COPY);   // typeof 的值Copy 一份用来做两次 if 判断
            //
            nextLabel = queue.labelDef();
            queue.inst(LDC_S, "string");
            queue.inst(DO, "==");
            queue.inst(IF, nextLabel);
            queue.inst(POP);
            queue.inst(GET);
            queue.inst(GOTO, finalLabel);
            queue.inst(LABEL, nextLabel);
            //
            nextLabel = queue.labelDef();
            queue.inst(LDC_S, "number");
            queue.inst(DO, "==");
            queue.inst(IF, nextLabel);
            queue.inst(PULL);
            queue.inst(GOTO, finalLabel);
            queue.inst(LABEL, nextLabel);
            //
            queue.inst(LDC_S, "type is not string or number.");
            queue.inst(THROW, 500);
            queue.inst(LABEL, finalLabel);
        }
    }
}