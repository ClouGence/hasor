package net.hasor.spring.boot;
import net.hasor.spring.SpringModule;
import net.hasor.spring.beans.SpringTypeSupplier;
import net.hasor.test.spring.web.Hello;
import net.hasor.test.spring.web.JsonRender;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHasorWeb()
@EnableHasor(startWith = WebBootEnableHasor_1.class)
@SpringBootApplication(scanBasePackages = "net.hasor.test.spring.web")
public class WebBootEnableHasor_1 implements WebModule, SpringModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) {
        SpringTypeSupplier springTypeSupplier = getSpringTypeSupplier(apiBinder);
        //Hello的创建使用 Spring，因为它已经被 Spring 托管了
        apiBinder.loadMappingTo(Hello.class, springTypeSupplier);
        apiBinder.addRender("json").toInstance(new JsonRender());
    }
}
