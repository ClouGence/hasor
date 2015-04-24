package test.net.hasor.rsf.rpc.customer;
import java.util.List;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        RsfBootstrap boot = new RsfBootstrap();
        final RsfContext rsfContext = boot.doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                rsfBinder.rsfService(List.class).bindAddress("rsf://127.0.0.1:8001/aaa").register();
            }
        }).socketBind("127.0.0.1", 8001).workAt(WorkMode.Customer).sync();
        //
        //
        for (int i = 0; i < 200; i++) {
            final int index = i;
            new Thread() {
                public void run() {
                    doCall("T" + index, rsfContext);
                };
            }.start();
        }
        System.in.read();
        System.out.println(rsfContext);
    }
    //
    private static void doCall(String threadName, RsfContext rsfContext) {
        RsfClient rsfClient = rsfContext.getRsfClient();
        RsfBindInfo<List> bindInfo = rsfContext.getBindCenter().getService(List.class);
        List remoteList = rsfClient.getRemote(bindInfo);
        //
        while (true) {
            Object result = remoteList.get(1);
            System.out.println(result);
        }
    }
}