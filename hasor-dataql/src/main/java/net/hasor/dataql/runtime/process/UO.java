package net.hasor.dataql.runtime.process;
import net.hasor.dataql.OperatorProcess;
import net.hasor.dataql.runtime.*;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class UO implements InsetProcess {
    @Override
    public int getOpcode() {
        return UO;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, ProcessContet context) throws ProcessException {
        String dyadicSymbol = sequence.currentInst().getString(0);
        Object expData = memStack.pop();
        //
        Class<?> expType = (expData == null) ? Void.class : expData.getClass();
        OperatorProcess process = context.findOperator(Symbol.Unary, dyadicSymbol, expType, null);
        //
        if (process == null) {
            throw new ProcessException("UO -> " + dyadicSymbol + " OperatorProcess is Undefined");
        }
        //
        Object result = process.doProcess(dyadicSymbol, new Object[] { expData });
        memStack.push(result);
    }
}