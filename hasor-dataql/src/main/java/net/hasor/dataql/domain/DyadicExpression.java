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
package net.hasor.dataql.domain;
import net.hasor.dataql.domain.compiler.CompilerStack;
import net.hasor.dataql.domain.compiler.InstQueue;

import java.util.Stack;
/**
 * 二元运算表达式
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DyadicExpression extends Expression {
    private Expression fstExpression;   //第一个表达式
    private String     dyadicSymbol;    //运算符
    private Expression secExpression;   //第二个表达式
    public DyadicExpression(Expression fstExpression, String dyadicSymbol, Expression secExpression) {
        super();
        this.fstExpression = fstExpression;
        this.dyadicSymbol = dyadicSymbol;
        this.secExpression = secExpression;
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        this.doCompiler(queue, stackTree, new Stack<String>());
    }
    protected void doCompiler(final InstQueue queue, CompilerStack stackTree, Stack<String> last) {
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
        //      算法的研发是在没有做参考任何资料情况下完全自主演算得出。
        //      另：该算法没有核实是否市面上存在类似算法，因此不能做独创性宣传。如确实属于独创那么保留算法独创的全部权利。
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
        // .输出第一个表达式
        this.fstExpression.doCompiler(queue, stackTree);
        //
        int selfPriority = priorityAt(this.dyadicSymbol);
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
        last.push(this.dyadicSymbol);
        //
        if (this.secExpression instanceof DyadicExpression) {
            ((DyadicExpression) this.secExpression).doCompiler(queue, stackTree, last);
        } else {
            this.secExpression.doCompiler(queue, stackTree);
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