package net.hasor.dataql.runtime.process;
import net.hasor.dataql.result.ListModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.ListResultStruts;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;

import java.util.Collection;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class PUSH implements InsetProcess {
    @Override
    public int getOpcode() {
        return PUSH;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        Object data = memStack.pop();
        Object ors = memStack.peek();
        //
        if (ors instanceof ListResultStruts) {
            ((ListResultStruts) ors).addResult(data);
            return;
        }
        if (ors instanceof ListModel) {
            ((ListModel) ors).add(data);
            return;
        }
        if (ors instanceof Collection) {
            ((Collection) ors).add(data);
            return;
        }
    }
}