package net.hasor.rsf._test;
import java.net.InetAddress;
import net.hasor.rsf.runtime.client.RsfClient;
import net.hasor.rsf.runtime.client.RsfClientFactory;
import org.more.future.FutureCallback;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client implements FutureCallback<Object> {
    public static void main(final String[] args) throws Throwable {
        for (int i = 0; i < 50; i++) {
            new Thread() {
                public void run() {
                    try {
                        main1(args);
                    } catch (Throwable e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                };
            }.start();
        }
    }
    public static void main1(String[] args) throws Throwable {
        Client.start();
        //
        //初始化RsfClientFactory
        RsfClientFactory factory = new RsfClientFactory(new ServerRsfContext());
        //连接Server
        RsfClient client = factory.connect(InetAddress.getLocalHost().getHostAddress(), 8000);
        //
        //发起调用.
        for (int i = 0; i < 1000000; i++) {
            client.doCallBackInvoke("net.hasor.rsf._test.TestServices", "sayHello", //
                    new Class<?>[] { String.class }, new Object[] { "你好..." }, new Client());
            sendCount++;
            Thread.sleep(5);
        }
        //关闭连接
        //        client.close();
    }
    private static void start() {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                        aa();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }.start();
    }
    //
    //
    private volatile static long sendCount = 0;
    private volatile static long errCount  = 0;
    private volatile static long okCount   = 0;
    private volatile static long start     = System.currentTimeMillis();
    public static void aa() {
        long duration = System.currentTimeMillis() - start;
        System.out.println("send Count:" + sendCount);
        System.out.println("ok   Count:" + okCount);
        System.out.println("err  Count:" + errCount);
        //
        System.out.println("send QPS  :" + (sendCount * 1000 / duration));
        System.out.println("ok QPS   :" + (okCount * 1000 / duration));
        System.out.println("ok(%)    :" + okCount / sendCount);
        System.out.println();
    }
    public void completed(Object result) {
        okCount++;
        //
    }
    public void failed(Throwable ex) {
        errCount++;
    }
    public void cancelled() {
        // TODO Auto-generated method stub
    }
}