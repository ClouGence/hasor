package net.hasor.dataql.runtime.process;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class LOAD implements InsetProcess {
    @Override
    public int getOpcode() {
        return LOAD;
    }
    @Override
    public void doWork(Instruction inst, MemStack memStack, ProcessContet context) throws ProcessException {
        int position = inst.getInt(0);
        Object data = context.loadData(position);
        memStack.push(data);
    }
}
