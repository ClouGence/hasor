package test;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.RsfResult;
import test.services.EchoService;
import test.services.MessageService;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterCustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config-center.xml", new RsfModule() {
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                apiBinder.rsfService(EchoService.class).register();
                apiBinder.rsfService(MessageService.class).register();
            }
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 2080; i++) {
            Thread.sleep(500);
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        //
        MessageService messageService = client.wrapper(MessageService.class);
        for (int i = 0; i < 2080; i++) {
            try {
                RsfResult res = messageService.sayHello("Hello Word");//客户端会瞬间返回,服务端执行一个消息需要 500毫秒。
                System.out.println(res);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}