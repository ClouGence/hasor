package net.hasor.dataql.runtime.process;
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class CALL implements InsetProcess {
    @Override
    public int getOpcode() {
        return CALL;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        Instruction instruction = sequence.currentInst();
        String udfName = instruction.getString(0);
        int paramCount = instruction.getInt(1);
        //
        Object[] paramArrays = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            paramArrays[paramCount - 1 - i] = memStack.pop();
        }
        //
        UDF udf = context.findUDF(udfName);
        if (udf == null) {
            throw new ProcessException("CALL -> udf '" + udfName + "' is not found");
        }
        //
        Object result = udf.call(paramArrays);
        memStack.push(result);
    }
}