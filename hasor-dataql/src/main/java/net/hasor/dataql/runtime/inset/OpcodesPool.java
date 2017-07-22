package net.hasor.dataql.runtime.inset;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.LocalData;
import net.hasor.dataql.runtime.mem.MemStack;
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
            pool.addInsetProcess(new LDC_B());
            pool.addInsetProcess(new LDC_D());
            pool.addInsetProcess(new LDC_S());
            pool.addInsetProcess(new LDC_N());
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
            //
            pool.addInsetProcess(new CALL());
            pool.addInsetProcess(new LCALL());
            //
            pool.addInsetProcess(new METHOD());
            pool.addInsetProcess(new M_REF());
            //
            pool.addInsetProcess(new IF());
            pool.addInsetProcess(new GOTO());
            pool.addInsetProcess(new END());
            pool.addInsetProcess(new EXIT());
            pool.addInsetProcess(new ERR());
            //
            pool.addInsetProcess(new OPT());
            pool.addInsetProcess(new LOCAL());
        }
        return pool;
    }
    private void addInsetProcess(InsetProcess inst) {
        this.processes[inst.getOpcode()] = inst;
    }
    //
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        //
        InsetProcess process = this.processes[sequence.currentInst().getInstCode()];
        if (process == null) {
            return;
        }
        process.doWork(sequence, memStack, local, context);
    }
}
