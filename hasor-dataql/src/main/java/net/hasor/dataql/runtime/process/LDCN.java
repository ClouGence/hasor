package net.hasor.dataql.runtime.process;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class LDCN implements InsetProcess {
    @Override
    public int getOpcode() {
        return LDC_N;
    }
    @Override
    public void doWork(Instruction inst, MemStack memStack, ProcessContet context) throws ProcessException {
        memStack.push(null);
    }
}