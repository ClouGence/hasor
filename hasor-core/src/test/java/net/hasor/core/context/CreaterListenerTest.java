package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.core.binder.TestBean;
import net.hasor.core.context.beans.ContextInjectBean;
import net.hasor.core.context.beans.ContextShutdownListenerBean;
import net.hasor.core.context.beans.ContextStartListenerBean;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
public class CreaterListenerTest {
    //
    @Test
    public void builderTest1() {
        //
        final Map<String, BindInfo> dataMap = new HashMap<String, BindInfo>();
        final BeanCreaterListener createrListener = new BeanCreaterListener() {
            @Override
            public void beanCreated(Object newObject, BindInfo bindInfo) throws Throwable {
                dataMap.put(newObject.getClass().getName(), bindInfo);
            }
        };
        //
        AppContext appContext = Hasor.create().asSmaller().build(new Module() {
            @Override
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(TestBean.class).toInstance(new TestBean()).whenCreate(createrListener);
                apiBinder.bindType(ContextInjectBean.class).whenCreate(createrListener);
                apiBinder.bindType(ContextShutdownListenerBean.class).whenCreate(createrListener);
            }
        });
        //
        assert dataMap.isEmpty();
        appContext.getInstance(TestBean.class);
        appContext.getInstance(ContextInjectBean.class);
        appContext.getInstance(ContextShutdownListenerBean.class);
        appContext.getInstance(ContextStartListenerBean.class);
        //
        assert !dataMap.containsKey(TestBean.class.getName());
        assert dataMap.containsKey(ContextInjectBean.class.getName());
        assert dataMap.get(ContextInjectBean.class.getName()) != null;
        assert dataMap.containsKey(ContextShutdownListenerBean.class.getName());
        assert dataMap.get(ContextShutdownListenerBean.class.getName()) != null;
        assert !dataMap.containsKey(ContextStartListenerBean.class.getName());
        assert dataMap.get(ContextStartListenerBean.class.getName()) == null;
        //
    }
}