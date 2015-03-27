package net.test.hasor.rsf._02_hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.test.hasor.rsf.EchoService;
import net.test.hasor.rsf.Utils;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public static void main(String[] args) throws Throwable {
        AppContext appContext = Hasor.createAppContext(new RsfConsumer());
        Utils.startQPS(appContext);/*启动QPS实时报告*/
        //
        EchoService myService = appContext.getInstance(EchoService.class);
        for (int i = 0; i < 1000000; i++) {
            String echoMsg = myService.echo("你好..", i);
            System.out.println(echoMsg);
        }
    }
}