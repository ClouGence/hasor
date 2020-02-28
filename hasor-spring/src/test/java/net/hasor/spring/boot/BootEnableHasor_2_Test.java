package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.test.spring.mod1.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = BootEnableHasor_2.class)
public class BootEnableHasor_2_Test {
    @Autowired
    private AppContext         appContext;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
        Set<Class<?>> hasType = new HashSet<>();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            hasType.add(applicationContext.getType(name));
        }
        //
        //
        assert appContext.getBindInfo(TestModuleA.class) == null;
        assert !hasType.contains(TestModuleA.class);
        //
        //
        assert appContext.getBindInfo(TestModuleB.class) == null;
        assert !hasType.contains(TestModuleB.class);
        //
        //
        assert appContext.getBindInfo(TestModuleC.class) == null;
        assert !hasType.contains(TestModuleC.class);
        //
        //
        assert appContext.getBindInfo(TestModuleD.class) == null;
        assert !hasType.contains(TestModuleD.class);
        //
        //
        // 有DimModule、在ComponentScan范围内、在EnableHasor范围外、无Component
        assert appContext.getBindInfo(TestDimModuleA.class) == null; // 范围外不加载
        assert !hasType.contains(TestDimModuleA.class);// 无Component，Spring 中不存在它。
        //
        //
        // 有DimModule、在ComponentScan范围内、在EnableHasor范围外、有Component
        assert appContext.getBindInfo(TestDimModuleB.class) != null; // 虽然 Hasor 扫描范围外，但是Hasor 会加载 Spring Bean 中所有 DimModule 的 Module
        assert hasType.contains(TestDimModuleB.class);
        TestDimModuleB dimModuleB = appContext.getInstance(TestDimModuleB.class);
        assert dimModuleB.getApplicationContext() == applicationContext;
        //
        //
        // 无DimModule、在ComponentScan范围内、在EnableHasor范围外、有Component
        assert appContext.getBindInfo(TestDimModuleC.class) == null; // 不是一个有效的 Module
        assert hasType.contains(TestDimModuleC.class);// 是Spring Bean
        TestDimModuleC dimModuleC_1 = appContext.getInstance(TestDimModuleC.class);
        TestDimModuleC dimModuleC_2 = applicationContext.getBean(TestDimModuleC.class);
        assert dimModuleC_1.getApplicationContext() == null;// Hasor 当成普通 Bean 创建
        assert dimModuleC_2.getApplicationContext() == applicationContext;// Spring 会创建它
    }
}
