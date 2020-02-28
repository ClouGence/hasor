package net.hasor.spring.boot;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableHasor(scanPackages = "net.hasor.test.spring.mod1.*")
public class BootEnableHasor_3 {
}
