package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
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
import java.util.function.Supplier;
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
        Object testBean1 = container.getProvider(adapter1, appContext, null).get();
        Object testBean2 = container.getProvider(adapter1, appContext, null).get();
        assert testBean1 == testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getProvider(adapter1, appContext, null).get();
        Object testBean4 = container.getProvider(adapter1, appContext, null).get();
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getProvider(adapter1, appContext, null).get();
        Object testBean6 = container.getProvider(adapter1, appContext, null).get();
        assert testBean5 == testBean6;
    }
    private void prototypeTest(StandardEnvironment environment, BeanContainer container,//
            AppContext appContext, BindInfo<?> adapter1) {
        // . default is Prototype (config from Config)
        Object testBean1 = container.getProvider(adapter1, appContext, null).get();
        Object testBean2 = container.getProvider(adapter1, appContext, null).get();
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getProvider(adapter1, appContext, null).get();
        Object testBean4 = container.getProvider(adapter1, appContext, null).get();
        assert testBean3 != testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getProvider(adapter1, appContext, null).get();
        Object testBean6 = container.getProvider(adapter1, appContext, null).get();
        assert testBean5 != testBean6;
    }
    private void defaultTest(StandardEnvironment environment, BeanContainer container,//
            AppContext appContext, BindInfo<?> adapter1) {
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getProvider(adapter1, appContext, null).get();
        Object testBean2 = container.getProvider(adapter1, appContext, null).get();
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getProvider(adapter1, appContext, null).get();
        Object testBean4 = container.getProvider(adapter1, appContext, null).get();
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getProvider(adapter1, appContext, null).get();
        Object testBean6 = container.getProvider(adapter1, appContext, null).get();
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getProvider(SimpleBean.class, appContext, null).get();
        Object testBean2 = container.getProvider(SimpleBean.class, appContext, null).get();
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getProvider(SimpleBean.class, appContext, null).get();
        Object testBean4 = container.getProvider(SimpleBean.class, appContext, null).get();
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getProvider(SimpleBean.class, appContext, null).get();
        Object testBean6 = container.getProvider(SimpleBean.class, appContext, null).get();
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getProvider(AnnoSingletonBean.class, appContext, null).get();
        Object testBean2 = container.getProvider(AnnoSingletonBean.class, appContext, null).get();
        assert testBean1 == testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getProvider(AnnoSingletonBean.class, appContext, null).get();
        Object testBean4 = container.getProvider(AnnoSingletonBean.class, appContext, null).get();
        assert testBean3 == testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getProvider(AnnoSingletonBean.class, appContext, null).get();
        Object testBean6 = container.getProvider(AnnoSingletonBean.class, appContext, null).get();
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // . default is Prototype (config from Config)
        Object testBean1 = container.getProvider(AnnoPrototypeBean.class, appContext, null).get();
        Object testBean2 = container.getProvider(AnnoPrototypeBean.class, appContext, null).get();
        assert testBean1 != testBean2;
        //
        // . default is Singleton
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "true");
        Object testBean3 = container.getProvider(AnnoPrototypeBean.class, appContext, null).get();
        Object testBean4 = container.getProvider(AnnoPrototypeBean.class, appContext, null).get();
        assert testBean3 != testBean4;
        //
        // . default is Prototype
        environment.getSettings().setSetting("hasor.default.asEagerSingleton", "false");
        Object testBean5 = container.getProvider(AnnoPrototypeBean.class, appContext, null).get();
        Object testBean6 = container.getProvider(AnnoPrototypeBean.class, appContext, null).get();
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
            container.getProvider(AnnoConflictBean.class, appContext, null).get();
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
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
        Supplier<SingletonScope> singletonScope = InstanceProvider.of(new SingletonScope());
        container.registerScope(ScopManager.SINGLETON_SCOPE, singletonScope);
        //
        // .
        Object testBean1 = container.getProvider(AnnoSingletonMyInteface.class, appContext, null).get();
        Object testBean2 = container.getProvider(AnnoSingletonMyInteface.class, appContext, null).get();
        assert testBean1 == testBean2;
        // .
        Object testBean3 = container.getProvider(AnnoPrototypeMyInteface.class, appContext, null).get();
        Object testBean4 = container.getProvider(AnnoPrototypeMyInteface.class, appContext, null).get();
        assert testBean3 != testBean4;
        //
        Object testBean5 = container.getProvider(AnnoPrototypeMyIntefaceCross2Singleton.class, appContext, null).get();
        Object testBean6 = container.getProvider(AnnoPrototypeMyIntefaceCross2Singleton.class, appContext, null).get();
        assert testBean5 != testBean6;
        // .
        Object testBean7 = container.getProvider(AnnoSingletonMyIntefaceCross2Prototype.class, appContext, null).get();
        Object testBean8 = container.getProvider(AnnoSingletonMyIntefaceCross2Prototype.class, appContext, null).get();
        assert testBean7 == testBean8;
    }
}