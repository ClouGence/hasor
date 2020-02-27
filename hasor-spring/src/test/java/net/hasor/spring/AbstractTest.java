package net.hasor.spring;
import net.hasor.spring.boot.EnableHasor;
import net.hasor.test.spring.mod1.TestDimModuleA;
import net.hasor.test.spring.mod1.TestDimModuleB;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "net.hasor.test.spring.mod1" })
@EnableHasor(autoScan = true, startWith = { TestDimModuleA.class, TestDimModuleB.class })
public class AbstractTest {
}
//  refProperties,
