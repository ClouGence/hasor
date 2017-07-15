package net.hasor.dataql.runtime.process;
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.domain.inst.Instruction;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
import net.hasor.dataql.runtime.struts.ObjectResultStruts;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
class ASM implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASM;
    }
    @Override
    public void doWork(Instruction inst, MemStack memStack, ProcessContet context) throws ProcessException {
        String typeString = inst.getString(0);
        Class<?> objectType = null;
        if (StringUtils.isNotBlank(typeString)) {
            objectType = context.loadType(typeString);
        } else {
            objectType = ObjectModel.class;
        }
        //
        Object toType = null;
        try {
            toType = objectType.newInstance();
        } catch (Exception e) {
            throw new ProcessException("ASM -> " + e.getMessage(), e);
        }
        //
        Object result = memStack.pop();
        memStack.push(new ObjectResultStruts(result, toType));
    }
}