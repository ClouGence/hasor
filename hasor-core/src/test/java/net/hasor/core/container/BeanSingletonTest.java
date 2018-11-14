package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.SingletonMode;
import net.hasor.core.container.anno.*;
import net.hasor.core.container.beans.SimpleBean;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.scope.SingletonScope;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
public class BeanSingletonTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment();
    }
    //
    private void singletonTest(StandardEnvironment environment, BeanContainer container,//
            AppContext appContext, BindInfo<?> adapter1) {
        // . default is Prototype (config from Config)
        Object testBean1 = container.getInstance(adapter1, appContext);
        Object testBean2 = container.getInstance(adapter1, appContext);
        assert testBean1 == testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getInstance(adapter1, appContext);
        Object testBean4 = container.getInstance(adapter1, appContext);
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getInstance(adapter1, appContext);
        Object testBean6 = container.getInstance(adapter1, appContext);
        assert testBean5 == testBean6;
    }
    private void prototypeTest(StandardEnvironment environment, BeanContainer container,//
            AppContext appContext, BindInfo<?> adapter1) {
        // . default is Prototype (config from Config)
        Object testBean1 = container.getInstance(adapter1, appContext);
        Object testBean2 = container.getInstance(adapter1, appContext);
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getInstance(adapter1, appContext);
        Object testBean4 = container.getInstance(adapter1, appContext);
        assert testBean3 != testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getInstance(adapter1, appContext);
        Object testBean6 = container.getInstance(adapter1, appContext);
        assert testBean5 != testBean6;
    }
    private void defaultTest(StandardEnvironment environment, BeanContainer container,//
            AppContext appContext, BindInfo<?> adapter1) {
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getInstance(adapter1, appContext);
        Object testBean2 = container.getInstance(adapter1, appContext);
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getInstance(adapter1, appContext);
        Object testBean4 = container.getInstance(adapter1, appContext);
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getInstance(adapter1, appContext);
        Object testBean6 = container.getInstance(adapter1, appContext);
        assert testBean5 != testBean6;
    }
    //
    //
    @Test
    public void builderTest1() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getInstance(SimpleBean.class, appContext);
        Object testBean2 = container.getInstance(SimpleBean.class, appContext);
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getInstance(SimpleBean.class, appContext);
        Object testBean4 = container.getInstance(SimpleBean.class, appContext);
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getInstance(SimpleBean.class, appContext);
        Object testBean6 = container.getInstance(SimpleBean.class, appContext);
        assert testBean5 != testBean6;
    }
    @Test
    public void builderTest2() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getInstance(AnnoSingletonBean.class, appContext);
        Object testBean2 = container.getInstance(AnnoSingletonBean.class, appContext);
        assert testBean1 == testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getInstance(AnnoSingletonBean.class, appContext);
        Object testBean4 = container.getInstance(AnnoSingletonBean.class, appContext);
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getInstance(AnnoSingletonBean.class, appContext);
        Object testBean6 = container.getInstance(AnnoSingletonBean.class, appContext);
        assert testBean5 == testBean6;
    }
    @Test
    public void builderTest3() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getInstance(AnnoPrototypeBean.class, appContext);
        Object testBean2 = container.getInstance(AnnoPrototypeBean.class, appContext);
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getInstance(AnnoPrototypeBean.class, appContext);
        Object testBean4 = container.getInstance(AnnoPrototypeBean.class, appContext);
        assert testBean3 != testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getInstance(AnnoPrototypeBean.class, appContext);
        Object testBean6 = container.getInstance(AnnoPrototypeBean.class, appContext);
        assert testBean5 != testBean6;
        //
    }
    @Test
    public void builderTest4() {
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        try {
            container.getInstance(AnnoConflictBean.class, appContext);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().endsWith(" , @Prototype and @Singleton appears only one.");
        }
    }
    //
    //
    @Test
    public void builderTest5() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . Override to clear
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(AnnoSingletonBean.class);
        adapter1.setBindID("1111");
        adapter1.setBindName("1111");
        adapter1.setSingletonMode(SingletonMode.Clear);
        this.defaultTest(environment, container, appContext, adapter1);
        //
        // . Override to Singleton
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(AnnoSingletonBean.class);
        adapter2.setBindID("2222");
        adapter2.setBindName("2222");
        adapter2.setSingletonMode(SingletonMode.Singleton);
        this.singletonTest(environment, container, appContext, adapter2);
        //
        // . Override to Singleton
        AbstractBindInfoProviderAdapter<?> adapter3 = container.createInfoAdapter(AnnoSingletonBean.class);
        adapter3.setBindID("3333");
        adapter3.setBindName("3333");
        adapter3.setSingletonMode(SingletonMode.Prototype);
        this.prototypeTest(environment, container, appContext, adapter3);
    }
    @Test
    public void builderTest6() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . Override to clear
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(AnnoPrototypeBean.class);
        adapter1.setBindID("1111");
        adapter1.setBindName("1111");
        adapter1.setSingletonMode(SingletonMode.Clear);
        this.defaultTest(environment, container, appContext, adapter1);
        //
        // . Override to Singleton
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(AnnoPrototypeBean.class);
        adapter2.setBindID("2222");
        adapter2.setBindName("2222");
        adapter2.setSingletonMode(SingletonMode.Singleton);
        this.singletonTest(environment, container, appContext, adapter2);
        //
        // . Override to Singleton
        AbstractBindInfoProviderAdapter<?> adapter3 = container.createInfoAdapter(AnnoPrototypeBean.class);
        adapter3.setBindID("3333");
        adapter3.setBindName("3333");
        adapter3.setSingletonMode(SingletonMode.Prototype);
        this.prototypeTest(environment, container, appContext, adapter3);
    }
    @Test
    public void builderTest7() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . Override to clear
        AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(AnnoConflictBean.class);
        adapter1.setBindID("1111");
        adapter1.setBindName("1111");
        adapter1.setSingletonMode(SingletonMode.Clear);
        this.defaultTest(environment, container, appContext, adapter1);
        //
        // . Override to Singleton
        AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(AnnoConflictBean.class);
        adapter2.setBindID("2222");
        adapter2.setBindName("2222");
        adapter2.setSingletonMode(SingletonMode.Singleton);
        this.singletonTest(environment, container, appContext, adapter2);
        //
        // . Override to Singleton
        AbstractBindInfoProviderAdapter<?> adapter3 = container.createInfoAdapter(AnnoConflictBean.class);
        adapter3.setBindID("3333");
        adapter3.setBindName("3333");
        adapter3.setSingletonMode(SingletonMode.Prototype);
        this.prototypeTest(environment, container, appContext, adapter3);
    }
    //
    //
    @Test
    public void builderTest8() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Provider<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // .
        Object testBean1 = container.getInstance(AnnoSingletonMyInteface.class, appContext);
        Object testBean2 = container.getInstance(AnnoSingletonMyInteface.class, appContext);
        assert testBean1 == testBean2;
        // .
        Object testBean3 = container.getInstance(AnnoPrototypeMyInteface.class, appContext);
        Object testBean4 = container.getInstance(AnnoPrototypeMyInteface.class, appContext);
        assert testBean3 != testBean4;
        //
        Object testBean5 = container.getInstance(AnnoPrototypeMyIntefaceCross2Singleton.class, appContext);
        Object testBean6 = container.getInstance(AnnoPrototypeMyIntefaceCross2Singleton.class, appContext);
        assert testBean5 != testBean6;
        // .
        Object testBean7 = container.getInstance(AnnoSingletonMyIntefaceCross2Prototype.class, appContext);
        Object testBean8 = container.getInstance(AnnoSingletonMyIntefaceCross2Prototype.class, appContext);
        assert testBean7 == testBean8;
    }
}
