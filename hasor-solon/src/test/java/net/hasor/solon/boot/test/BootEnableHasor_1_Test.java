package net.hasor.solon.boot.test;

import net.hasor.test.spring.mod1.*;
import net.hasor.core.AppContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.HashSet;
import java.util.Set;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BootEnableHasor_1.class)
public class BootEnableHasor_1_Test {
    @Inject
    private AppContext         appContext;

    @Test
    public void contextLoads() {
        Set<Class<?>> hasType = new HashSet<>();
        Aop.beanForeach((bw)->{
            hasType.add(bw.clz());
        });
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

    }

    @Test
    public void contextLoads21(){
        Set<Class<?>> hasType = new HashSet<>();
        Aop.beanForeach((bw)->{
            hasType.add(bw.clz());
        });

        //
        //
        // 有DimModule、在ComponentScan范围内、在EnableHasor范围内、无Component
        assert appContext.getBindInfo(TestDimModuleA.class) != null; // Hasor 加载了
        assert hasType.contains(TestDimModuleA.class);// 无Component，Spring 中不存在它。
        TestDimModuleA dimModuleA = appContext.getInstance(TestDimModuleA.class);
    }

    @Test
    public void contextLoads22(){
        Set<Class<?>> hasType = new HashSet<>();
        Aop.beanForeach((bw)->{
            hasType.add(bw.clz());
        });
        //
        //
        // 有DimModule、在ComponentScan范围内、在EnableHasor范围内、有Component
        assert appContext.getBindInfo(TestDimModuleB.class) != null; // Hasor 加载了
        assert hasType.contains(TestDimModuleB.class);
        TestDimModuleB dimModuleB = appContext.getInstance(TestDimModuleB.class);
        assert dimModuleB != null;
    }

    @Test
    public void contextLoads23(){
        Set<Class<?>> hasType = new HashSet<>();
        Aop.beanForeach((bw)->{
            hasType.add(bw.clz());
        });

        //
        //
        // 无DimModule、在ComponentScan范围内、在EnableHasor范围内、有Component
        assert appContext.getBindInfo(TestDimModuleC.class) == null; // 不是一个有效的 Module
        assert hasType.contains(TestDimModuleC.class);// 是Spring Bean
        TestDimModuleC dimModuleC_1 = appContext.getInstance(TestDimModuleC.class);
        TestDimModuleC dimModuleC_2 = Aop.get(TestDimModuleC.class);
        //assert dimModuleC_1.getApplicationContext() == null;// Hasor 当成普通 Bean 创建
    }
}
