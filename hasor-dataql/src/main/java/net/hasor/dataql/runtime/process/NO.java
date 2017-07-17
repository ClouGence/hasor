package net.hasor.dataql.runtime.process;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class NO implements InsetProcess {
    @Override
    public int getOpcode() {
        return NO;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, ProcessContet context) throws ProcessException {
        String typeString = sequence.currentInst().getString(0);
        Class<?> objectType = null;
        if (StringUtils.isNotBlank(typeString)) {
            objectType = context.loadType(typeString);
        } else {
            objectType = ObjectModel.class;
        }
        //
        try {
            memStack.push(objectType.newInstance());
        } catch (Exception e) {
            throw new ProcessException("NO -> " + e.getMessage(), e);
        }
    }
}