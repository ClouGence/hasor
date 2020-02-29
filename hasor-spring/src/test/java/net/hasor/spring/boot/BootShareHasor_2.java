package net.hasor.spring.boot;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "net.hasor.test.spring.mod1" })
@EnableHasor(useProperties = false)
public class BootShareHasor_2 {
}
