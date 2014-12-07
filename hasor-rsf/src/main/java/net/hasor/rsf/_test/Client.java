package net.hasor.rsf._test;
import java.net.InetAddress;
import net.hasor.rsf.plugins.local.LocalPrefPlugin;
import net.hasor.rsf.plugins.qps.QPSPlugin;
import net.hasor.rsf.runtime.RsfBinder;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.client.RsfClient;
import net.hasor.rsf.runtime.client.RsfClientFactory;
import net.hasor.rsf.runtime.context.DefaultRsfContext;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        RsfContext rsfContext = new DefaultRsfContext();
        final QPSPlugin qps = new QPSPlugin();
        RsfBinder rsfBinder = rsfContext.getRegisterCenter().getRsfBinder();
        rsfBinder.bindFilter(qps);
        rsfBinder.bindFilter(new LocalPrefPlugin());
        rsfBinder.rsfService(ITestServices.class).register();
        //
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
        //初始化RsfClientFactory
        RsfClientFactory factory = new RsfClientFactory(rsfContext);
        RsfClient client = factory.connect(InetAddress.getLocalHost().getHostAddress(), 8000);
        //获取服务
        final ITestServices bean = client.getRemote("net.hasor.rsf._test.ITestServices");
        //
        for (int i = 0; i < 1; i++) {
            new Thread() {
                public void run() {
                    call(bean);
                };
            }.start();
        }
        //关闭连接
        //client.close();
    }
    public static void call(ITestServices bean) {
        for (int i = 0; i < 1000000; i++) {
            try {
                System.out.println(bean.sayHello("你好...(" + i + ")"));;//发起调用.
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}