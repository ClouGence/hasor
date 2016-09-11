package net.hasor.paxos;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.paxos.serverid.ServerIDMessage;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.utils.TimerManager;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.stack.Protocol;
import org.jgroups.util.MessageBatch;
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