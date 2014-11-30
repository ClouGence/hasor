package net.hasor.rsf._test;
import java.net.InetAddress;
import net.hasor.rsf.runtime.client.RsfClient;
import net.hasor.rsf.runtime.client.RsfClientFactory;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        //初始化RsfClientFactory
        RsfClientFactory factory = new RsfClientFactory(new ClientRsfContext());
        //连接Server
        RsfClient client = factory.connect(InetAddress.getLocalHost().getHostAddress(), 8000);
        //获取服务
        final ITestServices bean = client.wrapper("net.hasor.rsf._test.TestServices", ITestServices.class);
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
            System.out.println(bean.sayHello("你好..."));//发起调用.
        }
    }
}