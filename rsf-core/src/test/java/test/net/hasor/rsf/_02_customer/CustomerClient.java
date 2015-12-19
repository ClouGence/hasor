package test.net.hasor.rsf._02_customer;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.bootstrap.RsfModule;
import test.net.hasor.rsf.services.EchoService;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("07_client-config.xml", new RsfModule() {
            public void loadModule(ApiBinder apiBinder, RsfBinder rsfBinder) throws Throwable {
                rsfBinder.rsfService(EchoService.class).bindAddress("rsf://127.0.0.1:8000/local").register();
            }
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 208; i++) {
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {}
        }
    }
}