package test.net.hasor.rsf.service;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import test.net.hasor.rsf.service.bean.EchoService;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("/rsf/client-config.xml", new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                rsfContext.binder().rsfService(EchoService.class).register();
            }
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 2000; i++) {
            try {
                Thread.sleep(1000);
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {
                System.out.println("call failed ->" + e.getMessage());
            }
        }
    }
}