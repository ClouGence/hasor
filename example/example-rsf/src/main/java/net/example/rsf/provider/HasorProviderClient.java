package net.example.rsf.provider;
import net.example.rsf.service.EchoService;
import net.example.rsf.service.EchoServiceImpl;
import net.example.rsf.service.MessageService;
import net.example.rsf.service.MessageServiceImpl;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorProviderClient {
    public static void main(String[] args) throws Throwable {
        //Client
        AppContext clientContext = Hasor.createAppContext("hasor-provider-config.xml", new RsfModule() {
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(EchoService.class).toProvider(apiBinder.converToProvider(//
                        apiBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register()//
                ));
                //
                apiBinder.bindType(MessageService.class).toProvider(apiBinder.converToProvider(//
                        apiBinder.rsfService(MessageService.class).toInstance(new MessageServiceImpl()).register()//
                ));
            }
        });
        System.out.println("server start.");
        System.in.read();
    }
}