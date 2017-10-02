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
/**
 * 指令池
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
public class OpcodesPool {
    private InsetProcess[] processes = new InsetProcess[255];
    //
    public static OpcodesPool newPool() {
        OpcodesPool pool = new OpcodesPool();
        {
            //
            pool.addInsetProcess(new NO());
            pool.addInsetProcess(new NA());
            //
            pool.addInsetProcess(new LDC_B());
            pool.addInsetProcess(new LDC_D());
            pool.addInsetProcess(new LDC_S());
            pool.addInsetProcess(new LDC_N());
            //
            pool.addInsetProcess(new LOAD());
            pool.addInsetProcess(new STORE());
            //
            pool.addInsetProcess(new ASM());
            pool.addInsetProcess(new ASO());
            pool.addInsetProcess(new ASA());
            pool.addInsetProcess(new ASE());
            //
            pool.addInsetProcess(new PUT());
            pool.addInsetProcess(new PUSH());
            pool.addInsetProcess(new ROU());
            pool.addInsetProcess(new UO());
            pool.addInsetProcess(new DO());
            //
            pool.addInsetProcess(new CALL());
            pool.addInsetProcess(new LCALL());
            //
            pool.addInsetProcess(new METHOD());
            pool.addInsetProcess(new M_REF());
            //
            pool.addInsetProcess(new IF());
            pool.addInsetProcess(new GOTO());
            pool.addInsetProcess(new END());
            pool.addInsetProcess(new EXIT());
            pool.addInsetProcess(new ERR());
            //
            pool.addInsetProcess(new OPT());
            pool.addInsetProcess(new LOCAL());
        }
        return pool;
    }
    private void addInsetProcess(InsetProcess inst) {
        this.processes[inst.getOpcode()] = inst;
    }
    //
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        //
        InsetProcess process = this.processes[sequence.currentInst().getInstCode()];
        if (process == null) {
            return;
        }
        process.doWork(sequence, memStack, local, context);
    }
}
