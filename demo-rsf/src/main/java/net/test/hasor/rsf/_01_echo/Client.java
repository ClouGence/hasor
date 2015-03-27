package net.test.hasor.rsf._01_echo;
import java.net.InetAddress;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.plugins.local.LocalPrefPlugin;
import net.hasor.rsf.plugins.qps.QPSPlugin;
import net.test.hasor.rsf.EchoService;
import net.test.hasor.rsf.Utils;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        //
        //1.使用 RSF 引导程序创建 RSF。
        RsfBootstrap bootstrap = new RsfBootstrap();
        bootstrap.doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                rsfBinder.bindFilter("QPS", new QPSPlugin());
                rsfBinder.bindFilter("LocalPre", new LocalPrefPlugin());
                //
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                rsfBinder.bindAddress(hostAddress, 8001);//分布式的远程服务提供者：1
                rsfBinder.bindAddress(hostAddress, 8002);//分布式的远程服务提供者：2
                rsfBinder.rsfService(EchoService.class).register();
            }
        });
        RsfContext rsfContext = bootstrap.sync();
        Utils.startQPS(rsfContext);/*启动QPS实时报告*/
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