package net.hasor.neuron;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.neuron._.ServerIDMessage;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.utils.TimerManager;
/**
 * Created by zhaoyongchun on 16/9/10.
 */
public class ddd {
    public static void main(String[] args) {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config.xml", new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                RsfBinder rsfBinder = rsfContext.binder();
                rsfBinder.rsfService(ServerIDMessage.class).register();
            }
        });
        //
        TimerManager timerManager = new TimerManager(6000, "PAXOS");
        System.out.println("server start.");
    }
}