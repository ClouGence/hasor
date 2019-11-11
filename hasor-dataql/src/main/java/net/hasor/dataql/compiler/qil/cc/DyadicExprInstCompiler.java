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
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

import java.util.Stack;

/**
 * 二元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DyadicExprInstCompiler extends InstCompiler<DyadicExpression> {
    @Override
    public void doCompiler(DyadicExpression inst, InstQueue queue, CompilerStack stackTree) {
        this.doCompiler(inst, queue, stackTree, new Stack<>());
    }

    protected void doCompiler(DyadicExpression inst, InstQueue queue, CompilerStack stackTree, Stack<String> last) {
        Expression fstExpression = inst.getFstExpression();
        String dyadicSymbol = inst.getDyadicSymbol();
        Expression secExpression = inst.getSecExpression();
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
        findInstCompilerByInst(fstExpression).doCompiler(fstExpression, queue, stackTree);
        //
        int selfPriority = priorityAt(dyadicSymbol);
        if (!last.isEmpty()) {
            while (!last.isEmpty()) {
                int lastPriority = priorityAt(last.peek());
                if (selfPriority > lastPriority) {
                    queue.inst(DO, last.pop());
                } else {
                    break;
                }
            }
        }
        last.push(dyadicSymbol);
        //
        if (secExpression instanceof DyadicExpression) {
            this.doCompiler((DyadicExpression) secExpression, queue, stackTree, last);
        } else {
            findInstCompilerByInst(secExpression).doCompiler(secExpression, queue, stackTree);
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
            //      0st: ()                            括号
            //      1st: ->                            取值
            //      2st: !  , ++ , --                  一元操作
            //      3st: *  , /  , \  , %              乘除法
            new String[] { "*", "/", "\\", "%" },
            //      4st: +  , -                        加减法
            new String[] { "+", "-" },
            //      5st: &  , |  , ^  , << , >> , >>>  位运算
            new String[] { "&", "|", "^", "<<", ">>", ">>>" },
            //      6st: >  , >= , == , != , <= , <    比较运算
            new String[] { ">", ">=", "!=", "==", "<=", "<" },
            //      7st: && , ||                       逻辑运算
            new String[] { "&&", "||" },
            //
    };
}