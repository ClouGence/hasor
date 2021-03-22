import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.aop.SimplePropertyDelegate;
import net.hasor.utils.BeanUtils;
import org.junit.Test;

/**
 * @Description 没有包声明情况下的委托动态属性
 * @Author HOUYIHONG
 * @Date 22/3/2021
 **/
public class NonePackagePropertyDelegateTest {
    @Test
    public void test() {
        // 注册两个 Bean 并且共享同一个 name 属性。
        AppContext appContext = Hasor.create().build(apiBinder -> {
            SimplePropertyDelegate delegate = new SimplePropertyDelegate("helloWord");
            apiBinder.bindType(PojoBean1.class).dynamicProperty("name", String.class, delegate);
            apiBinder.bindType(PojoBean2.class).dynamicProperty("name", String.class, delegate);
        });
        // 创建两个 Bean
        PojoBean1 pojoBean1 = appContext.getInstance(PojoBean1.class);
        PojoBean2 pojoBean2 = appContext.getInstance(PojoBean2.class);
        //
        assert BeanUtils.readProperty(pojoBean1, "name").equals("helloWord");
        assert BeanUtils.readProperty(pojoBean2, "name").equals("helloWord");
        BeanUtils.writeProperty(pojoBean1, "name", "newValue");
        assert BeanUtils.readProperty(pojoBean1, "name").equals("newValue");
        assert BeanUtils.readProperty(pojoBean2, "name").equals("newValue");
    }

    public static class PojoBean1 {
        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    public static class PojoBean2 {
        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}


