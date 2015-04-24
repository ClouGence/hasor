package net.test.hasor.rsf._01_echo;
import java.net.URI;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
import net.test.hasor.rsf.EchoService;
import net.test.hasor.rsf.Monitor;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        //
        //1.使用 RSF 引导程序创建 RSF。
        RsfBootstrap boot = new RsfBootstrap();
        final RsfContext rsfContext = boot.doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                URI host1 = new URI("rsf://192.168.137.1:8001/local");
                URI host2 = new URI("rsf://192.168.137.1:8002/local");
                rsfBinder.rsfService(EchoService.class).bindFilter("QPS", new Monitor()).bindAddress(host1).bindAddress(host2).register();
            }
        }).workAt(WorkMode.Customer).sync();
        //
        //2.获取远程服务的包装类
        EchoService myService = rsfContext.getRsfClient().wrapper("RSF", EchoService.class.getName(), "1.0.0", EchoService.class);
        //3.发起调用
        for (int i = 0; i < 1000000; i++) {
            String echoMsg = myService.echo("你好..", null);
            System.out.println(echoMsg);
        }
    }
}