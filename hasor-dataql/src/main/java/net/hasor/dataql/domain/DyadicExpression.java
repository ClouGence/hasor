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
        this.doCompiler(queue, stackTree, null);
    }
    protected void doCompiler(InstQueue queue, CompilerStack stackTree, Runnable callback) {
        //
        //  规则：
        //      在输出完第一个操作数之后，如果后一组表达式优先那么完整的输出后一组表达式之后输出运算符。
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
        // .第二个比第一个优先（comparePriority，方法返回 true 表示 当前被测试对象优先）
        //        if (this.fstExpression.comparePriority(this.secExpression)) {
        //        }
        //        this.fstExpression.doCompiler(queue, stackTree);
        //        // .第二个表达式运算优先
        //        if (this.secExpression.priorityTo(this.fstExpression)) {
        //            this.secExpression.doCompiler(queue, stackTree, new Runnable() {
        //                @Override
        //                public void run() {
        //                }
        //            });
        //        } else {
        //        }
        //
        this.fstExpression.doCompiler(queue, stackTree);
        this.secExpression.doCompiler(queue, stackTree);
        queue.inst(DO, this.dyadicSymbol);
    }
    /**
     * comparePriority，方法返回 true 表示 当前被测试对象优先
     * @param testExpression 被测试表达式。
     * @return 如果 testExpression 优先于 this 那么返回true
     */
    public boolean comparePriority(Expression testExpression) {
        return false;
    }
}