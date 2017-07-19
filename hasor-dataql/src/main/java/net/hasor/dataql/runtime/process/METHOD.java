package net.hasor.dataql.runtime.process;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.LambdaCall;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class METHOD implements InsetProcess {
    @Override
    public int getOpcode() {
        return METHOD;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        int paramCount = sequence.currentInst().getInt(0);
        LambdaCall result = (LambdaCall) memStack.peek();
        //
        if (result.getArrays() == null || paramCount != result.getArrays().length) {
            Object[] finalParamArray = new Object[paramCount];
            Object[] inParams = result.getArrays();
            for (int i = 0; i < paramCount; i++) {
                if (i > inParams.length) {
                    break;
                }
                finalParamArray[i] = inParams[i];
            }
            result.updateArrays(finalParamArray);
        }
        //
    }
}