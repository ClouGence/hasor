package net.example.hasor;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import net.hasor.spring.boot.EnableHasor;
import net.hasor.spring.boot.EnableHasorWeb;
import net.hasor.spring.boot.WorkAt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableNacosConfig()
//@EnableDiscoveryClient()
@EnableHasor()
@EnableHasorWeb(at = WorkAt.Interceptor)
@SpringBootApplication(scanBasePackages = { "net.example.hasor" })
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}