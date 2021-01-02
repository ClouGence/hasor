package net.example.db;
import net.hasor.spring.boot.EnableHasor;
import net.hasor.spring.boot.EnableHasorWeb;
import net.hasor.spring.boot.WorkAt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHasor()
@EnableHasorWeb(at = WorkAt.Interceptor)
@SpringBootApplication(scanBasePackages = { "net.example.db" })
public class DbDatawayApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbDatawayApplication.class, args);
    }
}