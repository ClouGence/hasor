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
 * import 语法
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ImportInst extends Inst {
    private String packageName = null;
    private String udfName     = null;
    public ImportInst(String packageName, String udfName) {
        super();
        this.packageName = packageName;
        this.udfName = udfName;
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        InstQueue instQueue = queue.newMethodInst();
        instQueue.inst(LCALL, this.packageName);
        //
        // .指向函数的指针
        int methodAddress = instQueue.getName();
        queue.inst(M_REF, methodAddress);
        //
        // .如果当前堆栈中存在该变量的定义，那么直接覆盖
        int index = stackTree.containsWithCurrent(this.udfName);
        if (index >= 0) {
            queue.inst(STORE, index);
        } else {
            int storeIndex = stackTree.push(this.udfName);
            queue.inst(STORE, storeIndex);
        }
    }
}