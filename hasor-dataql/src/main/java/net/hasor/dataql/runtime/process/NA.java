package net.hasor.dataql.runtime.process;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.result.ListModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;

import java.util.Collection;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class NA implements InsetProcess {
    @Override
    public int getOpcode() {
        return NA;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        String typeString = sequence.currentInst().getString(0);
        Class<?> listType = null;
        if (StringUtils.isNotBlank(typeString)) {
            listType = context.loadType(typeString);
        } else {
            listType = ListModel.class;
        }
        //
        if (!Collection.class.isAssignableFrom(listType)) {
            throw new ProcessException("NA -> type " + listType + " is not Collection");
        }
        //
        try {
            memStack.push(listType.newInstance());
        } catch (Exception e) {
            throw new ProcessException("NA -> " + e.getMessage(), e);
        }
    }
}