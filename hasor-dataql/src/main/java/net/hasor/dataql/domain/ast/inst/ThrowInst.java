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
package net.hasor.dataql.domain.ast.inst;
import net.hasor.dataql.Option;
import net.hasor.dataql.domain.ast.Inst;
import net.hasor.dataql.domain.ast.Variable;
import net.hasor.dataql.domain.compiler.CompilerStack;
import net.hasor.dataql.domain.compiler.InstQueue;

import java.io.Writer;

/**
 * throw指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ThrowInst extends Inst {
    private int      errorCode;
    private Variable throwData;

    public ThrowInst(int exitCode, Variable exitData) {
        this.errorCode = exitCode;
        this.throwData = exitData;
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        queue.inst(LDC_D, this.errorCode);
        this.throwData.doCompiler(queue, stackTree);
        queue.inst(ERR);
    }

    @Override
    public void doFormat(int depth, Option formatOption, Writer writer) {
        //
    }
}