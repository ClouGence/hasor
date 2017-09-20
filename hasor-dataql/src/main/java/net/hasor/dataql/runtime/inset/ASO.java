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
package net.hasor.dataql.runtime.inset;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.OriResultStruts;
/**
 * ASO，指令处理器。用于将结果作为原封不动的进行返回。
 *
 * 与 ASO 指令配对的还有一个对应的 ASE，在这一对 ASO -> ASE 范围内的指令。
 *
 * ASO 指令后续通常紧跟着一个 ASE。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class ASO implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASO;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        Object result = memStack.pop();
        memStack.push(new OriResultStruts(result));
    }
}