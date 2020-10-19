package net.example.hasor.config;
import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.spring.SpringModule;
import org.springframework.stereotype.Component;

@DimModule
@Component
public class ExampleModule implements SpringModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // .custom DataQL
        //
    }
}