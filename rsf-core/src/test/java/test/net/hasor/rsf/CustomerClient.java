package test.net.hasor.rsf;
import java.lang.reflect.Method;
import java.util.List;
import org.more.classcode.delegate.faces.InnerChainMethodDelegate;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.address.InterAddress;
import test.net.hasor.rsf.services.EchoService;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("customer-config.xml", new RsfModule() {
            @Override
            public void loadRsf(RsfContext rsfContext) throws Throwable {
                RsfBinder rsfBinder = rsfContext.binder();
                InterAddress local = new InterAddress("rsf://127.0.0.1:8100/default");
                rsfBinder.rsfService(EchoService.class).bindAddress(local).register();
            }
        });
        System.out.println("server start.");
        //
        //Client -> Server
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 208; i++) {
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {}
        }
    }
    public int getNames(int abc, Object abcc) {
        try {
            Class<?>[] arrayOfClass = new Class[] { int.class, Object.class };
            Object[] arrayOfObject = new Object[] { abc, abcc };
            //
            Method localMethod = List.class.getMethod("getNames", arrayOfClass);
            ClassLoader localLoader = getClass().getClassLoader();
            //
            Object target = new InnerChainMethodDelegate("xxxx", "", localLoader).invoke(localMethod, this, arrayOfObject);
            return ((Integer) target).intValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}