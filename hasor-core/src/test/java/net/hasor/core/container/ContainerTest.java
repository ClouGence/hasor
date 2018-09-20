package net.hasor.core.container;
import net.hasor.core.BindInfo;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.info.NotifyData;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class ContainerTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
    }
    @Test
    public void containerTest1() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<TestBean> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("ID:123456");
        adapter.setBindName("Name:dddd");
        BindInfo<Object> bindInfo = container.findBindInfo("ID:123456");
        assert "Name:dddd".equals(bindInfo.getBindName());
        assert bindInfo.getBindType().equals(TestBean.class);
    }
    @Test
    public void containerTest2() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<TestBean> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("aaa");
        //
        AbstractBindInfoProviderAdapter<TestBean> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("ID:2");
        adapter2.setBindName("ddd");
        //
        AbstractBindInfoProviderAdapter<TestBean> adapter3 = container.createInfoAdapter(TestBean.class);
        adapter3.setBindID("ID:3");
        adapter3.setBindName(null);
        //
        BindInfo<?> bindInfo1 = container.findBindInfo("ddd", TestBean.class);
        assert "ddd".equals(bindInfo1.getBindName());
        assert bindInfo1.getBindID().equals("ID:2");
        assert bindInfo1.getBindType().equals(TestBean.class);
        //
        BindInfo<?> bindInfo2 = container.findBindInfo(null, TestBean.class);
        assert bindInfo2.getBindName() == null;
        assert bindInfo2.getBindID().equals("ID:3");
        assert bindInfo2.getBindType().equals(TestBean.class);
        //
        List<BindInfo<TestBean>> bindInfoList = container.findBindInfoList(TestBean.class);
        Set<String> infoSet = new HashSet<String>();
        for (BindInfo<TestBean> info : bindInfoList) {
            infoSet.add(info.getBindID());
        }
        assert infoSet.contains("ID:1");
        assert infoSet.contains("ID:2");
        assert infoSet.contains("ID:3");
        //
        Collection<String> bindInfoIDs = container.getBindInfoIDs();
        assert bindInfoIDs.contains("ID:1");
        assert bindInfoIDs.contains("ID:2");
        assert bindInfoIDs.contains("ID:3");
    }
    @Test
    public void containerTest3() {
        final BeanContainer container = new BeanContainer();
        //
        assert container.findBindInfo("ID:123456") == null;
        assert container.findBindInfo(null, TestBean.class) == null;
        assert container.findBindInfoList(TestBean.class).isEmpty();
    }
    @Test
    public void containerTest4() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("ID:2");
        adapter2.setBindName("b");
        //
        AbstractBindInfoProviderAdapter<?> adapter3 = container.createInfoAdapter(TestBean.class);
        adapter3.setBindID("ID:3");
        adapter3.setBindName(null);
        //
        AbstractBindInfoProviderAdapter<?> adapter4 = container.createInfoAdapter(MyBean.class);
        adapter4.setBindID("ID:4");
        adapter4.setBindName("c");
        //
        AbstractBindInfoProviderAdapter<?> adapter5 = container.createInfoAdapter(MyBean.class);
        adapter5.setBindID("ID:5");
        adapter5.setBindName("d");
        //
        AbstractBindInfoProviderAdapter<?> adapter6 = container.createInfoAdapter(MyBean.class);
        adapter6.setBindID("ID:6");
        adapter6.setBindName(null);
        //
        //
        Collection<String> namesByType = container.getBindInfoNamesByType(MyBean.class);
        assert !namesByType.contains("a");
        assert !namesByType.contains("b");
        assert namesByType.contains("c");
        assert namesByType.contains("d");
        assert namesByType.size() == 2;
    }
    @Test
    public void containerTest5() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        assert !container.isInit();
        //
        container.doInitializeCompleted(env);
        assert container.isInit();
        //
        try {
            AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
            adapter2.setBindID("ID:2");
            adapter2.setBindName("b");
            assert false;
        } catch (IllegalStateException e) {
            assert "container has been started.".equals(e.getMessage());
        }
    }
    @Test
    public void containerTest6() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        container.doInitializeCompleted(env);
        assert container.findBindInfo("ID:1") != null;
        //
        container.doShutdownCompleted();
        assert container.findBindInfo("ID:1") == null;
        //
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("ID:1");
        adapter2.setBindName("a");
        assert container.findBindInfo("ID:1") != null;
    }
    @Test
    public void containerTest7() {
        final BeanContainer container = new BeanContainer();
        //
        container.doInitializeCompleted(env);
        container.doInitializeCompleted(env);
        assert container.isInit();
        //
        container.doShutdownCompleted();
        container.doShutdownCompleted();
        assert !container.isInit();
    }
    @Test
    public void containerTest8() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        //
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        try {
            adapter2.setBindID("ID:1");
            adapter2.setBindName("b");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("duplicate bind -> id value is ID:1");
        }
        //
    }
    @Test
    public void containerTest9() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("ID:1");
        adapter.setBindName("a");
        //
        adapter.setBindID("ID:1");
        assert true;
        //
    }
    @Test
    public void containerTest10() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        //
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        try {
            adapter2.setBindID("ID:2");
            adapter2.setBindName("a");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("duplicate bind -> bindName 'a'");
        }
        //
    }
    @Test
    public void containerTest11() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        try {
            ((AbstractBindInfoProviderAdapter<MyBean>) adapter1).setBindType(MyBean.class);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("'bindType' are not allowed to be changed");
        }
    }
    @Test
    public void containerTest12() {
        final BeanContainer container = new BeanContainer();
        //
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("ID:1");
        adapter1.setBindName("a");
        //
        try {
            container.update(null, null);
            container.update(adapter1, PowerMockito.mock(NotifyData.class));
            //
            container.update(null, PowerMockito.mock(NotifyData.class));
            container.update(adapter1, null);
            assert true;
        } catch (Exception e) {
            assert false;
        }
    }
}
