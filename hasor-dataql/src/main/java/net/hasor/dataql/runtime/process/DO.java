package net.hasor.dataql.runtime.process;
import net.hasor.dataql.OperatorProcess;
import net.hasor.dataql.runtime.*;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class DO implements InsetProcess {
    @Override
    public int getOpcode() {
        return DO;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        String dyadicSymbol = sequence.currentInst().getString(0);
        Object secExpData = memStack.pop();
        Object fstExpData = memStack.pop();
        //
        Class<?> fstType = (fstExpData == null) ? Void.class : fstExpData.getClass();
        Class<?> secType = (secExpData == null) ? Void.class : secExpData.getClass();
        OperatorProcess process = context.findOperator(Symbol.Dyadic, dyadicSymbol, fstType, secType);
        //
        if (process == null) {
            throw new ProcessException("DO -> " + dyadicSymbol + " OperatorProcess is Undefined");
        }
        //
        Object result = process.doProcess(dyadicSymbol, new Object[] { fstExpData, secExpData });
        memStack.push(result);
    }
}