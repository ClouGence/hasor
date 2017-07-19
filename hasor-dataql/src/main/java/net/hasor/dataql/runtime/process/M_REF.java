package net.hasor.dataql.runtime.process;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.LambdaCallStruts;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class M_REF implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_REF;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        //
        Instruction inst = sequence.currentInst();
        int address = inst.getInt(0);
        int paramCount = inst.getInt(1);
        //
        // .前把函数入口定义，打包成一个 LambdaCallStruts
        memStack.push(new LambdaCallStruts(address, paramCount));
    }
}