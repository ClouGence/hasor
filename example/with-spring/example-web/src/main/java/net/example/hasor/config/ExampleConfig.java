package net.example.hasor.config;
import net.hasor.core.AppContext;
import net.hasor.dataway.DatawayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration()
public class ExampleConfig {
    @Bean(autowireCandidate = false)
    public DatawayService datawayService(AppContext appContext) {
        return appContext.getInstance(DatawayService.class);
    }
}