package net.hasor.dataql.runtime.process;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.MemStack;
/**
 * Created by yongchun.zyc on 2017/7/13.
 */
public class OpcodesPool {
    private InsetProcess[] processes = new InsetProcess[255];
    //
    public static OpcodesPool newPool() {
        OpcodesPool pool = new OpcodesPool();
        {
            //
            pool.addInsetProcess(new NO());
            pool.addInsetProcess(new NA());
            //
            pool.addInsetProcess(new LDCB());
            pool.addInsetProcess(new LDCD());
            pool.addInsetProcess(new LDCS());
            pool.addInsetProcess(new LDCN());
            //
            pool.addInsetProcess(new LOAD());
            pool.addInsetProcess(new STORE());
            //
            pool.addInsetProcess(new ASM());
            pool.addInsetProcess(new ASO());
            pool.addInsetProcess(new ASA());
            pool.addInsetProcess(new ASE());
            //
            pool.addInsetProcess(new PUT());
            pool.addInsetProcess(new PUSH());
            pool.addInsetProcess(new ROU());
            pool.addInsetProcess(new UO());
            pool.addInsetProcess(new DO());
        }
        return pool;
    }
    private void addInsetProcess(InsetProcess inst) {
        this.processes[inst.getOpcode()] = inst;
    }
    //
    public void doWork(InstSequence sequence, MemStack memStack, ProcessContet context) throws ProcessException {
        InsetProcess process = this.processes[sequence.currentInst().getInstCode()];
        if (process == null) {
            return;
        }
        process.doWork(sequence, memStack, context);
    }
}
