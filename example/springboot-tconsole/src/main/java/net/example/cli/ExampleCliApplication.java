package net.example.cli;
import net.hasor.core.AppContext;
import net.hasor.spring.boot.EnableHasor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableHasor
@SpringBootApplication
public class ExampleCliApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleCliApplication.class, args);
        AppContext appContext = context.getBean(AppContext.class);
        appContext.join();
    }
}