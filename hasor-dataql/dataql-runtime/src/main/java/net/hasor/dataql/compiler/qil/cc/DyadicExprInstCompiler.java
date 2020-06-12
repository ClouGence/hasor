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
import net.hasor.dataql.compiler.ast.expr.DyadicExpression;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.util.Stack;

/**
 * 二元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DyadicExprInstCompiler implements InstCompiler<DyadicExpression> {
    @Override
    public void doCompiler(DyadicExpression astInst, InstQueue queue, CompilerContext compilerContext) {
        this.doCompiler(astInst, queue, compilerContext, new Stack<>());
    }

    protected void doCompiler(DyadicExpression astInst, InstQueue queue, CompilerContext compilerContext, Stack<String> last) {
        Expression fstExpression = astInst.getFstExpression();
        String dyadicSymbol = astInst.getDyadicSymbol().getSymbol();
        Expression secExpression = astInst.getSecExpression();
        //
        //  优先级：
        //      0st: ()                            括号
        //      1st: ->                            取值
        //      2st: !  , ++ , --                  一元操作
        //      3st: *  , /  , \  , %              乘除法
        //      4st: +  , -                        加减法
        //      5st: &  , |  , ^  , << , >> , >>>  位运算
        //      6st: >  , >= , == , != , <= , <    比较运算
        //      7st: && , ||                       逻辑运算
        //
        //  例：
        //      a + b * c - d       ->  a,b,c,*,+,d,-
        //      a + b * c - d / e   ->  a,b,c,*,+,d,e,/,-
        //      a + b * c < d ^ 2   ->  a,b,c,*,+,d,2,^,<
        //
        //  算法说明：
        //      逆波兰
        //
        //  算法逻辑：
        //      put fstExpression
        //      if last = empty then
        //          goto self
        //      end
        //
        //      while last.empty = false
        //          if self <= last.peek then   <- 根据优先级Tab，计算 slef 的运算符是否比 last 中最后一个放进去的优先级要低
        //              put last.pop
        //          else
        //              goto self
        //          end
        //      end
        //      self : last.push( self )
        //
        //      if next = null then
        //          put secExpression
        //          while last.empty = false
        //              put last.pop
        //          end
        //      end
        //
        //  -----------------------------------------------------------------------------
        //
        //
        // .输出第一个表达式
        compilerContext.findInstCompilerByInst(fstExpression).doCompiler(queue);
        //
        int selfPriority = priorityAt(dyadicSymbol);
        if (!last.isEmpty()) {
            while (!last.isEmpty()) {
                int lastPriority = priorityAt(last.peek());
                if (selfPriority >= lastPriority) {
                    queue.inst(DO, last.pop());
                } else {
                    break;
                }
            }
        }
        last.push(dyadicSymbol);
        //
        if (secExpression instanceof DyadicExpression) {
            this.doCompiler((DyadicExpression) secExpression, queue, compilerContext, last);
        } else {
            compilerContext.findInstCompilerByInst(secExpression).doCompiler(queue);
            while (!last.isEmpty()) {
                queue.inst(DO, last.pop());
            }
        }
    }

    private static int priorityAt(String dyadicSymbol) {
        for (int symbolArraysIndex = 0; symbolArraysIndex < ComparePriorityKeys.length; symbolArraysIndex++) {
            String[] symbolArrays = ComparePriorityKeys[symbolArraysIndex];
            for (String symbol : symbolArrays) {
                if (symbol.equalsIgnoreCase(dyadicSymbol)) {
                    return symbolArraysIndex;
                }
            }
        }
        throw new UnsupportedOperationException("symbol " + dyadicSymbol + " undefined priority.");
    }

    private static final String[][] ComparePriorityKeys = new String[][] {
            // 二元运算优先级定义表
            //      1st: *  , /  , \  , %              乘除法（乘法、除法、整除、求余）
            new String[] { "*", "/", "\\", "%" },
            //      2st: +  , -                        加减法（加法、减法）
            new String[] { "+", "-" },
            //      3st: &  , |  , ^  , << , >> , >>>  位运算（与运算、或运算、非运算、左移位、有符号右移位、无符号右移位）
            new String[] { "&", "|", "^", "<<", ">>", ">>>" },
            //      4st: >  , >= , == , != , <= , <    比较运算（大于、大于等于、不等于、等于、小于等于、小于）
            new String[] { ">", ">=", "!=", "==", "<=", "<" },
            //      5st: && , ||                       逻辑运算（逻辑与、逻辑或）
            new String[] { "&&", "||" },
            //
    };
}