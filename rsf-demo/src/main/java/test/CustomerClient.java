package test;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.address.InterAddress;
import test.services.EchoService;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config.xml", new RsfModule() {
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                //不使用注册中心，需要配置提供者地址（可以是数组列表）
                InterAddress[] addressArray = new InterAddress[2];
                addressArray[0] = new InterAddress("rsf://127.0.0.1:9001/default");
                addressArray[1] = new InterAddress("rsf://127.0.0.1:9001/default");
                rsfContext.binder().rsfService(EchoService.class).bindAddress(null, addressArray).register();
                //
                //使用注册中心，不需要配置地址
                //rsfContext.binder().rsfService(EchoService.class).register();
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
    }
}