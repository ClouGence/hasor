package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.test.spring.mod1.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = BootEnableHasor_1.class)
public class BootEnableHasor_1_Test {
    @Autowired
    private AppContext         appContext;
    @Autowired
    private ApplicationContext applicationContext;

    public void clear() {
        TestModuleA.reset();
        TestModuleB.reset();
        TestModuleC.reset();
        TestModuleD.reset();
        TestDimModuleA.reset();
        TestDimModuleB.reset();
    }

    @Test
    public void contextLoads() {
        assert appContext != null;
        //
        assert !TestModuleA.isInit();
        assert !TestModuleB.isInit();
        assert !TestModuleC.isInit();
        assert !TestModuleD.isInit();
        assert TestDimModuleA.isInit();//Module 被执行了
        assert TestDimModuleB.isInit();
        //
        assert appContext.getBindInfo(TestModuleA.class) == null;
        assert appContext.getBindInfo(TestModuleB.class) == null;
        assert appContext.getBindInfo(TestModuleC.class) == null;
        assert appContext.getBindInfo(TestModuleD.class) == null;
        //当 TestDimModuleA 被加载的时候，他会把自己注册到 Hasor 中，以此来验证 ApiBinder 是可用的
        assert appContext.getBindInfo(TestDimModuleA.class) != null;//
        //当 TestDimModuleB 被加载的时候，他会把自己注册到 Hasor 中，以此来验证 ApiBinder 是可用的
        assert appContext.getBindInfo(TestDimModuleB.class) != null;
        //
        TestModuleA moduleA = appContext.getInstance(TestModuleA.class);
        TestModuleB moduleB = appContext.getInstance(TestModuleB.class);
        TestModuleC moduleC = appContext.getInstance(TestModuleC.class);
        TestModuleD moduleD = appContext.getInstance(TestModuleD.class);
        TestDimModuleA dimModuleA = appContext.getInstance(TestDimModuleA.class);
        TestDimModuleB dimModuleB = appContext.getInstance(TestDimModuleB.class);
        //
        assert moduleA.getApplicationContext() == null;
        assert moduleB.getApplicationContext() == null;
        assert moduleC.getApplicationContext() == null;
        assert moduleD.getApplicationContext() == null;
        assert dimModuleA.getApplicationContext() == null;
        // 只有 TestDimModuleB 标注了 Component，因此是通过 Spring 创建的
        assert dimModuleB.getApplicationContext() == applicationContext;
        //
        clear();
    }
}
