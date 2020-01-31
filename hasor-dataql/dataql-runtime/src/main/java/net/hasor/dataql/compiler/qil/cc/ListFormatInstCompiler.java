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
import net.hasor.dataql.compiler.ast.RouteVariable;
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.dataql.compiler.ast.fmt.ListFormat;
import net.hasor.dataql.compiler.ast.value.PrimitiveVariable;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;

import static net.hasor.dataql.compiler.ast.value.EnterRouteVariable.SpecialType.Special_A;

/**
 * 函数调用的返回值处理格式，List格式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ListFormatInstCompiler implements InstCompiler<ListFormat> {
    @Override
    public void doCompiler(ListFormat astInst, InstQueue queue, CompilerContext compilerContext) {
        // .特殊情况优化处理
        if (astInst.getFormatTo().getExpressionList().isEmpty()) {
            RouteVariable formVariable = astInst.getForm();
            compilerContext.findInstCompilerByInst(formVariable).doCompiler(queue);
            queue.inst(POP);    // 丢弃表达式结果
            queue.inst(NEW_A);  // 构造集合
            return;
        }
        //
        // .编译表达式
        RouteVariable formVariable = astInst.getForm();
        compilerContext.findInstCompilerByInst(formVariable).doCompiler(queue);
        queue.inst(CAST_I); // 栈顶数据转换为迭代器
        queue.inst(E_PUSH); // 将栈顶数据压入环境栈
        {
            queue.inst(NEW_A);  // 构造集合
            Label enterLoop = queue.labelDef();
            Label breakLoop = queue.labelDef();
            {
                // .声明循环起点
                queue.inst(LABEL, enterLoop);
                // .加载迭代器并尝试获取下一条数据，如果数据获取失败就跳出迭代器结束遍历
                queue.inst(E_LOAD, Special_A.getCode());
                queue.inst(GET, "next");
                queue.inst(IF, breakLoop);
                //
                for (Variable variable : astInst.getFormatTo().getExpressionList()) {
                    if (!(variable instanceof PrimitiveVariable)) {
                        // 从环境栈上取得迭代器并拿到数据，然后在放到环境栈顶。等待后面 路由表达式使用
                        queue.inst(E_LOAD, Special_A.getCode());
                        queue.inst(GET, "data");
                        queue.inst(E_PUSH);
                        compilerContext.findInstCompilerByInst(variable).doCompiler(queue);
                        queue.inst(E_POP);
                    } else {
                        compilerContext.findInstCompilerByInst(variable).doCompiler(queue);
                    }
                    // .执行表达式并推入结果
                    queue.inst(PUSH);
                }
                //
                queue.inst(GOTO, enterLoop);    //跳回开始执行下一条数据
            }
            queue.inst(LABEL, breakLoop);
        }
        queue.inst(E_POP);// 丢弃环境栈顶元素
    }
}