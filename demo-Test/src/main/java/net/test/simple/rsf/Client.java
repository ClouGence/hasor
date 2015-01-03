package net.test.simple.rsf;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.test.simple.rsf.client.MyService;
import net.test.simple.rsf.client.RsfConsumer;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        AppContext app = Hasor.createAppContext(new RsfConsumer());
        //
        MyService myService = app.getInstance(MyService.class);
        for (int i = 0; i < 1000000; i++) {
            String echoMsg = myService.callEcho("你好..");
            System.out.println(echoMsg);
        }
    }
}