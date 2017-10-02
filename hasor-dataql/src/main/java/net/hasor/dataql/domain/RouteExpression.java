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
import net.hasor.dataql.domain.compiler.CompilerStack.ContainsIndex;
import net.hasor.dataql.domain.compiler.InstQueue;
import net.hasor.dataql.domain.compiler.Opcodes;
import net.hasor.utils.StringUtils;
/**
 * 值路由
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RouteExpression extends Expression {
    private String routeExpression;
    public RouteExpression(String routeExpression) {
        super();
        this.routeExpression = routeExpression;
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (StringUtils.isBlank(this.routeExpression)) {
            return;
        }
        //
        // .方法区中
        ContainsIndex index = stackTree.containsWithTree(this.routeExpression);
        if (index.isValid()) {
            if (index.current) {
                queue.inst(LOAD, -1, index.index);
            } else {
                queue.inst(LOAD, index.depth, index.index);
            }
            return;
        }
        // .整个堆栈
        if (this.routeExpression.indexOf('.') >= 0) {
            index = stackTree.containsWithTree(this.routeExpression.split("\\.")[0]);
            if (index.isValid()) {
                if (index.current) {
                    queue.inst(LOAD, -1, index.index);
                } else {
                    queue.inst(LOAD, index.depth, index.index);
                }
                return;
            }
        }
        //
        // .路由数据
        queue.inst(Opcodes.ROU, this.routeExpression);
    }
}