package net.hasor.dataql.runtime.process;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.LambdaCall;
import net.hasor.dataql.runtime.struts.LambdaCallStruts;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class LCALL implements InsetProcess {
    @Override
    public int getOpcode() {
        return LCALL;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        //
        LambdaCallStruts callStruts = (LambdaCallStruts) memStack.pop();
        int address = callStruts.getMethod();
        int paramCount = callStruts.getParamCount();
        //
        // .参数准备
        Object[] paramArrays = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            paramArrays[paramCount - 1 - i] = memStack.pop();
        }
        LambdaCall callInfo = new LambdaCall(address, paramArrays);
        //
        // .查找方法指令序列
        InstSequence methodSeq = sequence.methodSet(address);
        if (methodSeq == null) {
            throw new ProcessException("LCALL -> InstSequence '" + address + "' is not found.");
        }
        // .执行调用，调用前把所有入惨打包成一个 Array，交给 METHOD 指令去处理。
        {
            MemStack sub = memStack.create();
            sub.push(callInfo);
            context.processInset(methodSeq, sub, local);
            callInfo.setResult(sub.getResult());
        }
        // .返回值处理
        Object result = callInfo.getResult();
        memStack.push(result);
    }
}