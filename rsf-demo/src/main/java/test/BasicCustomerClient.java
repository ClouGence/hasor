package test;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.RsfResult;
import net.hasor.rsf.address.InterAddress;
import test.services.EchoService;
import test.services.MessageService;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class BasicCustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config-basic.xml", new RsfModule() {
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                //不使用注册中心，需要配置提供者地址（可以是数组列表）
                InterAddress[] addressArray = new InterAddress[2];
                addressArray[0] = new InterAddress("rsf://127.0.0.1:2180/default");
                addressArray[1] = new InterAddress("rsf://127.0.0.1:2180/default");
                //
                apiBinder.rsfService(EchoService.class).bindAddress(null, addressArray).register();
                apiBinder.rsfService(MessageService.class).bindAddress(null, addressArray).register();
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
            } catch (Exception e) {
            }
        }
        //
        MessageService messageService = client.wrapper(MessageService.class);
        for (int i = 0; i < 208; i++) {
            try {
                RsfResult res = messageService.sayHello("Hello Word");//客户端会瞬间返回,服务端执行一个消息需要 500毫秒。
                System.out.println(res);
            } catch (Exception e) {
            }
        }
    }
}