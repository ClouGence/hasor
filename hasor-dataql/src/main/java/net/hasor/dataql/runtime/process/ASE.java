package net.hasor.dataql.runtime.process;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
import net.hasor.dataql.runtime.struts.ResultStruts;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class ASE implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASE;
    }
    @Override
    public void doWork(Instruction inst, MemStack memStack, ProcessContet context) throws ProcessException {
        ResultStruts rs = (ResultStruts) memStack.pop();
        memStack.push(rs.getResult());
    }
}