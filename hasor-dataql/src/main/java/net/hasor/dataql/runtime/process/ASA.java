package net.hasor.dataql.runtime.process;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.result.ListModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.ListResultStruts;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class ASA implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASA;
    }
    @Override
    public void doWork(Instruction inst, MemStack memStack, ProcessContet context) throws ProcessException {
        String typeString = inst.getString(0);
        Class<?> objectType = null;
        if (StringUtils.isNotBlank(typeString)) {
            objectType = context.loadType(typeString);
        } else {
            objectType = ListModel.class;
        }
        //
        Object toType = null;
        try {
            toType = objectType.newInstance();
        } catch (Exception e) {
            throw new ProcessException("ASA -> " + e.getMessage(), e);
        }
        //
        Object result = memStack.pop();
        memStack.push(new ListResultStruts(result, toType));
    }
}