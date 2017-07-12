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
import net.hasor.dataql.domain.inst.CompilerStack;
import net.hasor.dataql.domain.inst.InstOpcodes;
import net.hasor.dataql.domain.inst.InstQueue;
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
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        // .方法区中
        int index = stackTree.contains(this.routeExpression);
        if (index >= 0) {
            queue.inst(InstOpcodes.LOAD, index);
            return;
        }
        // .整个堆栈
        //
        // .路由数据
        queue.inst(InstOpcodes.ROU, this.routeExpression);
    }
}