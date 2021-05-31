package webapp;

import net.hasor.solon.boot.EnableHasor;
import net.hasor.solon.boot.EnableHasorWeb;
import org.noear.solon.XApp;
import webapp.dso.module.StartModule;

@EnableHasor(startWith = StartModule.class)
@EnableHasorWeb
public class DatawayApp {
    public static void main(String[] args) throws Throwable {
        XApp.start(DatawayApp.class, args);
    }
}
