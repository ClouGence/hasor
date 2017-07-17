package net.hasor.dataql.runtime.process;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class LDCB implements InsetProcess {
    @Override
    public int getOpcode() {
        return LDC_B;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, ProcessContet context) throws ProcessException {
        Boolean value = sequence.currentInst().getBoolean(0);
        memStack.push(value);
    }
}