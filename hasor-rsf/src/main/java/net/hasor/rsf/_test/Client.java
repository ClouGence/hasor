package net.hasor.rsf._test;
import java.net.InetAddress;
import net.hasor.rsf.plugins.local.LocalPrefPlugin;
import net.hasor.rsf.plugins.qps.QPSPlugin;
import net.hasor.rsf.runtime.RsfBinder;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.client.RsfClient;
import net.hasor.rsf.runtime.client.RsfClientFactory;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        RsfContext rsfContext = new ServerRsfContext();
        RsfBinder rsfBinder = rsfContext.getRegisterCenter().getRsfBinder();
        rsfBinder.bindFilter(new QPSPlugin());
        rsfBinder.bindFilter(new LocalPrefPlugin());
        rsfBinder.rsfService(ITestServices.class).register();
        //
        //初始化RsfClientFactory
        RsfClientFactory factory = new RsfClientFactory(rsfContext);
        RsfClient client = factory.connect(InetAddress.getLocalHost().getHostAddress(), 8000);
        //获取服务
        final ITestServices bean = client.getRemote("net.hasor.rsf._test.TestServices");
        //
        for (int i = 0; i < 200; i++) {
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
            bean.sayHello("你好...");//发起调用.
        }
    }
}