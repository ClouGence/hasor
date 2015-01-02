package net.hasor.rsf._test;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
import net.hasor.rsf.plugins.local.LocalPrefPlugin;
import net.hasor.rsf.plugins.qps.QPSPlugin;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        RsfContext rsfContext = new RsfBootstrap().doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws UnknownHostException {
                rsfBinder.bindFilter("QPS", new QPSPlugin());
                rsfBinder.bindFilter("LocalPre", new LocalPrefPlugin());
                //
                rsfBinder.rsfService(ITestServices.class, new TestServices())//
                        .addBindAddress(InetAddress.getLocalHost().getHostAddress(), 8000)//
                        .addBindAddress(InetAddress.getLocalHost().getHostAddress(), 8001)//
                        .addBindAddress(InetAddress.getLocalHost().getHostAddress(), 8002)//
                        .addBindAddress(InetAddress.getLocalHost().getHostAddress(), 8003)//
                        .register();
            }
        }).workAt(WorkMode.Customer).sync();
        //
        //QPS
        final QPSPlugin qps = rsfContext.findFilter(ITestServices.class, "QPS");
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {}
                    System.out.println("QPS         :" + qps.getQPS());
                    System.out.println("requestCount:" + qps.getOkCount());
                    System.out.println();
                }
            }
        }).start();
        //
        //RsfClient
        RsfClient client = rsfContext.getRsfClient();
        final ITestServices bean = client.getRemote("net.hasor.rsf._test.ITestServices");
        for (int i = 0; i < 20; i++) {
            new Thread() {
                public void run() {
                    call(bean);
                };
            }.start();
        }
    }
    public static void call(ITestServices bean) {
        for (int i = 0; i < 1000000; i++) {
            try {
                bean.sayHello("你好...(" + i + ")");//发起调用.
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("time out (" + i + ")");//发起调用.
            }
        }
    }
}