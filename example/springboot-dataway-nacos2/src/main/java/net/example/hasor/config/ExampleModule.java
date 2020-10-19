package net.example.hasor.config;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.spring.SpringModule;
import org.springframework.stereotype.Component;

@DimModule
@Component
public class ExampleModule implements SpringModule {
    //Method 2 : nacos is used through Spring-boot-starter
    @NacosInjected
    private ConfigService configService;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // Method 2 : nacos is used through Spring-boot-starter
        apiBinder.bindType(ConfigService.class).toInstance(this.configService);
        //
        // .custom DataQL
        //
    }
}