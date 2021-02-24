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
package net.hasor.dataql.runtime;
import net.hasor.dataql.compiler.qil.Opcodes;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * 指令执行器接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-14
 */
public interface InsetProcess extends Opcodes {
    /** 执行器，用于处理的指令 Code */
    public int getOpcode();

    /** 执行指令 */
    public void doWork(             //
            InstSequence sequence,  // 指令序列
            DataHeap dataHeap,      // 数据堆
            DataStack dataStack,    // 数据栈
            EnvStack envStack,      // 环境栈
            InsetProcessContext context   // 执行器上下文
    ) throws QueryRuntimeException;
}
