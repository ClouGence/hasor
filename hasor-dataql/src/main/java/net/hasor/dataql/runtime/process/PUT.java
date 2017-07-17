package net.hasor.dataql.runtime.process;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
import net.hasor.dataql.runtime.struts.ObjectResultStruts;

import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class PUT implements InsetProcess {
    @Override
    public int getOpcode() {
        return PUT;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, ProcessContet context) throws ProcessException {
        String filedName = sequence.currentInst().getString(0);
        Object data = memStack.pop();
        //
        Object ors = memStack.peek();
        if (ors instanceof ObjectResultStruts) {
            ((ObjectResultStruts) ors).addResultField(filedName, data);
            return;
        }
        if (ors instanceof ObjectModel) {
            ((ObjectModel) ors).addField(filedName);
            ((ObjectModel) ors).put(filedName, data);
            return;
        }
        if (ors instanceof Map) {
            ((Map) ors).put(filedName, data);
            return;
        }
    }
}