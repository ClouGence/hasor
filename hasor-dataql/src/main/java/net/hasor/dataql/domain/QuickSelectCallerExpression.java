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
 * 函数调用
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QuickSelectCallerExpression extends CallerExpression {
    private RouteExpression quickSelect = null;
    public QuickSelectCallerExpression(String callName, RouteExpression quickSelect) {
        super(callName);
        this.quickSelect = quickSelect;
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        int index = stackTree.contains(this.callName);
        if (index > -1) {
            // .存在函数定义
            queue.inst(LOAD, index);
        } else {
            // .使用UDF进行调用
            queue.inst(ROU, this.callName);
        }
        this.quickSelect.doCompiler(queue, stackTree);
        super.doCompilerFormat(queue, stackTree);
    }
}