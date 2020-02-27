package net.hasor.spring.boot;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "net.hasor.test.spring" })
@EnableHasorWeb(path = "/*")
@EnableHasor(autoScan = true, autoScanPackages = "net.hasor.test.spring.*")
public class WebBootEnableHasor_1 implements WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
    }
}
