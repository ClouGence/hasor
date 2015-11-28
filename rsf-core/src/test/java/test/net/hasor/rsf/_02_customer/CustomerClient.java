package test.net.hasor.rsf._02_customer;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import test.net.hasor.rsf.services.EchoService;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        String hostAddress = "127.0.0.1";//RSF服务绑定的本地IP地址。
        int hostPort = 8002;//使用的端口
        //
        RsfContext rsfContext = new RsfBootstrap().doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                //声明服务
                RegisterBuilder<?> regBuilder = rsfBinder.rsfService(EchoService.class);
                //声明服务
                regBuilder.bindAddress("rsf://127.0.0.1:8001/unit");
                //发布服务
                regBuilder.register();
            }
        }).socketBind(hostAddress, hostPort).sync();
        //
        System.out.println("server start.");
        EchoService echoService = rsfContext.getRsfClient().wrapper(EchoService.class);
        for (int i = 0; i < 200; i++) {
            System.out.println("[Client] " + echoService.sayHello("index =" + i));
        }
    }
}