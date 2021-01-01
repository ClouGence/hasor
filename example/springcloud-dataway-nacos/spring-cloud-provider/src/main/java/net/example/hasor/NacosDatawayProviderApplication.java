package net.example.hasor;
import net.hasor.spring.boot.EnableHasor;
import net.hasor.spring.boot.EnableHasorWeb;
import net.hasor.spring.boot.WorkAt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = { "net.example.hasor" })
@EnableDiscoveryClient
@EnableHasor()
@EnableHasorWeb(at = WorkAt.Interceptor)
public class NacosDatawayProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosDatawayProviderApplication.class, args);
    }
}