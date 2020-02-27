package net.hasor.spring.boot;
import net.hasor.test.spring.mod1.TestDimModuleA;
import net.hasor.test.spring.mod1.TestDimModuleB;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "net.hasor.test.spring.mod1" })
@EnableHasor(autoScan = true, startWith = { TestDimModuleA.class, TestDimModuleB.class })
public class BootEnableHasor_1 {
}
//  refProperties,
