package net.hasor.core.bean;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.test.core.basic.factory.FaceFactory;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.test.core.basic.pojo.SampleFace;
import org.junit.Test;

public class BeanTest {
    @Test
    public void beanProvider() throws Exception {
        FaceFactory factory = new FaceFactory();
        //
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SampleBean.class).toTypeSupplier(factory);
            apiBinder.bindType(SampleFace.class).toTypeSupplier(factory);
        });
        // .工厂方式创建 Bean
        SampleBean sampleBean1 = appContext.getInstance(SampleBean.class);
        SampleFace sampleBean2 = appContext.getInstance(SampleFace.class);
        //
        assert sampleBean1 != sampleBean2;
        assert factory.getTarget1() == sampleBean1;
        assert factory.getTarget2() == sampleBean2;
    }
}
