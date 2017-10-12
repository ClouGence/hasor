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
 * 权限提升，用于表示表达式中的括号
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class PrivilegeExpression extends Expression {
    private Expression expression;
    public PrivilegeExpression(Expression expression) {
        super();
        this.expression = expression;
    }
    @Override
    public String toString() {
        return "( " + this.expression.toString() + " )";
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        this.expression.doCompiler(queue, stackTree);
    }
}