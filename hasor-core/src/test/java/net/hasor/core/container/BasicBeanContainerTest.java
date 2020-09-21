/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core.container;
import net.hasor.core.*;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.test.core.basic.implby.endsingleton.EsImplSampleBean;
import net.hasor.test.core.basic.implby.endsingleton.EsSampleBeanFaceByFace;
import net.hasor.test.core.basic.implby.firstsingleton.FsImplSampleBean;
import net.hasor.test.core.basic.implby.firstsingleton.FsSampleBeanFaceByFace;
import net.hasor.test.core.basic.implby.middlesingleton.MsImplSampleBean;
import net.hasor.test.core.basic.implby.middlesingleton.MsSampleBeanFaceByFace;
import net.hasor.test.core.basic.inject.constructor.*;
import net.hasor.test.core.basic.pojo.PojoBean;
import net.hasor.test.core.basic.pojo.SingletonSampleBean;
import net.hasor.test.core.enums.SelectEnum;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class BasicBeanContainerTest {
    private BeanContainer beanContainer = null;
    private AppContext    appContext    = null;

    @Before
    public void beforeTest() {
        Environment env = PowerMockito.mock(Environment.class);
        this.beanContainer = new BeanContainer(env);
        this.appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        this.beanContainer.init();
    }

    @Test
    public void basicTest1() {
        // 使用系统默认构造方法
        assert beanContainer.providerOnlyType(PojoBean.class, appContext, null).get() instanceof PojoBean;
        // 没有类型返回空
        assert beanContainer.providerOnlyType(null, appContext, null) == null;
    }

    @Test
    public void basicTest2() {
        try {
            // 类有一个带参数的构造方法，同时没有加注注解，创建时会抛错
            beanContainer.providerOnlyType(NativeConstructorPojoBeanRef.class, appContext, new Object[] { new PojoBean() }).get();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("No default constructor found.");
        }
    }

    @Test
    public void basicTest3() {
        try {
            // 匹配到一个构造方法
            PojoBean pojoBean = new PojoBean();
            SingleConstructorPojoBeanRef ref = beanContainer.providerOnlyType(SingleConstructorPojoBeanRef.class, appContext, new Object[] { pojoBean }).get();
            assert ref.getPojoBean() == pojoBean;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        try {
            PojoBean pojoBean1 = new PojoBean();
            PojoBean pojoBean2 = new PojoBean();
            // 匹配到了多个构造方法当时只选择第一个
            MultipleConstructorPojoBeanRef ref = beanContainer.providerOnlyType(MultipleConstructorPojoBeanRef.class, appContext, new Object[] { pojoBean1, pojoBean2 }).get();
            assert ref.getIndex() == 1;
            assert ref.getPojoBean() == pojoBean1;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        //
        NativeConstructorPojoBeanRef2 ref = beanContainer.providerOnlyType(NativeConstructorPojoBeanRef2.class, appContext, null).get();
        assert "default".equals(ref.getPojoBean().getUuid());
    }

    @Test
    public void basicTest4() {
        assert beanContainer.providerOnlyType(Integer.TYPE, appContext, null).get() == 0;
        //
        try {
            assert beanContainer.providerOnlyType(Integer.class, appContext, null).get() == null;
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("No default constructor found.");
        }
        //
        assert beanContainer.providerOnlyType(int[].class, appContext, null).get().length == 0;
        //
        assert beanContainer.providerOnlyType(List.class, appContext, null).get() == null;
        assert beanContainer.providerOnlyType(SelectEnum.class, appContext, null).get() == null;
        assert beanContainer.providerOnlyType(AbstractMap.class, appContext, null).get() == null;
        //
        try {
            beanContainer.providerOnlyType(ConstructorError1Bean.class, appContext, null).get();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
            assert e.getMessage().equals("create Error.");
        }
        //
        try {
            beanContainer.providerOnlyType(ConstructorError2Bean.class, appContext, null).get();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
            assert e.getMessage().equals("java.lang.Exception: create Error.");
            assert e.getCause() instanceof Exception;
            assert e.getCause().getMessage().equals("create Error.");
        }
    }

    @Test
    public void basicTest5() {
        BeanContainer container = new BeanContainer(PowerMockito.mock(Environment.class));
        container.init();
        container.close();
        //
        try {
            container.close();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("the Container has been closed.");
        }
    }

    @Test
    public void singletonTest() {
        {
            // 单例
            SingletonSampleBean sampleBean1 = beanContainer.providerOnlyType(SingletonSampleBean.class, appContext, null).get();
            SingletonSampleBean sampleBean2 = beanContainer.providerOnlyType(SingletonSampleBean.class, appContext, null).get();
            assert sampleBean1 == sampleBean2;
        }
        //
        {
            // 单例（ImplBy 的接口生命周期是不生效的）
            FsSampleBeanFaceByFace sampleBean1 = beanContainer.providerOnlyType(FsSampleBeanFaceByFace.class, appContext, null).get();
            FsSampleBeanFaceByFace sampleBean2 = beanContainer.providerOnlyType(FsSampleBeanFaceByFace.class, appContext, null).get();
            assert sampleBean1 != sampleBean2;
            assert sampleBean1 instanceof FsImplSampleBean;
            assert sampleBean2 instanceof FsImplSampleBean;
        }
        //
        {
            // 单例（ImplBy 的接口生命周期是不生效的）
            MsSampleBeanFaceByFace sampleBean1 = beanContainer.providerOnlyType(MsSampleBeanFaceByFace.class, appContext, null).get();
            MsSampleBeanFaceByFace sampleBean2 = beanContainer.providerOnlyType(MsSampleBeanFaceByFace.class, appContext, null).get();
            assert sampleBean1 != sampleBean2;
            assert sampleBean1 instanceof MsImplSampleBean;
            assert sampleBean2 instanceof MsImplSampleBean;
        }
        //
        {
            // 单例（ImplBy 的接口生命周期是不生效的，单例生效是因为在最终实现类上有 Singleton）
            EsSampleBeanFaceByFace sampleBean1 = beanContainer.providerOnlyType(EsSampleBeanFaceByFace.class, appContext, null).get();
            EsSampleBeanFaceByFace sampleBean2 = beanContainer.providerOnlyType(EsSampleBeanFaceByFace.class, appContext, null).get();
            assert sampleBean1 == sampleBean2;
            assert sampleBean1 instanceof EsImplSampleBean;
            assert sampleBean2 instanceof EsImplSampleBean;
        }
    }

    @Test
    public void constructorTest() {
        {
            // 单例
            Constructor<?> constructor = SingletonSampleBean.class.getConstructors()[0];
            SingletonSampleBean sampleBean1 = (SingletonSampleBean) beanContainer.providerOnlyConstructor(constructor, appContext, null).get();
            SingletonSampleBean sampleBean2 = (SingletonSampleBean) beanContainer.providerOnlyConstructor(constructor, appContext, null).get();
            assert sampleBean1 == sampleBean2;
        }
        //
        {
            // 单例（ImplBy 的接口生命周期是不生效的）
            PojoBean pojoBean = new PojoBean();
            Constructor<?> constructor = SingleConstructorPojoBeanRef.class.getConstructors()[0];
            SingleConstructorPojoBeanRef sampleBean1 = (SingleConstructorPojoBeanRef) beanContainer.providerOnlyConstructor(constructor, appContext, new Object[] { pojoBean }).get();
            SingleConstructorPojoBeanRef sampleBean2 = (SingleConstructorPojoBeanRef) beanContainer.providerOnlyConstructor(constructor, appContext, new Object[] { pojoBean }).get();
            assert sampleBean1 != sampleBean2;
            assert sampleBean1 instanceof SingleConstructorPojoBeanRef;
            assert sampleBean2 instanceof SingleConstructorPojoBeanRef;
            assert sampleBean1.getPojoBean() == pojoBean;
            assert sampleBean2.getPojoBean() == pojoBean;
        }
        //
        assert beanContainer.providerOnlyConstructor(null, appContext, null) == null;
    }

    @Test
    public void bindinfoTest1() {
        BeanContainer container = new BeanContainer(PowerMockito.mock(Environment.class));
        ScopeContainer scopeContainer = container.getScopeContainer();
        scopeContainer.init();
        //
        DefaultBindInfoProviderAdapter<PojoBean> adapter1 = container.getBindInfoContainer().createInfoAdapter(PojoBean.class, null);
        adapter1.addScopeProvider(scopeContainer.findScope(Singleton.class.getName()));
        //
        PojoBean pojoBean1 = container.providerOnlyBindInfo(adapter1, appContext).get();
        PojoBean pojoBean2 = container.providerOnlyBindInfo(adapter1, appContext).get();
        assert pojoBean1 == pojoBean2;
        //
        //
        PojoBean outside1 = new PojoBean();
        PojoBean outside2 = new PojoBean();
        DefaultBindInfoProviderAdapter<NativeConstructorPojoBeanRef2> adapter2 = container.getBindInfoContainer().createInfoAdapter(NativeConstructorPojoBeanRef2.class, null);
        adapter2.setConstructor(0, PojoBean.class, InstanceProvider.of(outside1));
        adapter2.setConstructor(1, PojoBean.class, InstanceProvider.of(outside2));
        NativeConstructorPojoBeanRef2 refBean1 = container.providerOnlyBindInfo(adapter2, appContext).get();
        NativeConstructorPojoBeanRef2 refBean2 = container.providerOnlyBindInfo(adapter2, appContext).get();
        assert refBean1.getPojoBean() == outside1;
        assert refBean1.getPojoBean2() == outside2;
        assert refBean1 != refBean2;
    }

    @Test
    public void bindinfoTest2() {
        BeanContainer container = new BeanContainer(PowerMockito.mock(Environment.class));
        ScopeContainer scopeContainer = container.getScopeContainer();
        scopeContainer.init();
        //
        assert container.providerOnlyBindInfo(null, appContext) == null;
    }

    @Test
    public void bindinfoTest3() {
        BeanContainer container = new BeanContainer(PowerMockito.mock(Environment.class));
        ScopeContainer scopeContainer = container.getScopeContainer();
        scopeContainer.init();
        //
        assert container.providerOnlyBindInfo(null, appContext) == null;
    }

    @Test
    public void annotationTest1() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        //
        BeanContainer container = new BeanContainer(mockEnv);
        PowerMockito.when(appContext.getInstance(anyString())).then(invoker -> {
            BindInfo<Object> bindInfo = container.getBindInfoContainer().findBindInfo(invoker.getArguments()[0].toString());
            return container.providerOnlyBindInfo(bindInfo, appContext).get();
        });
        //
        PowerMockito.when(appContext.findBindingBean(anyString(), any())).then(invoker -> {
            String bindName = invoker.getArguments()[0].toString();
            Class<?> bindType = (Class<?>) invoker.getArguments()[1];
            BindInfo<?> bindInfo = container.getBindInfoContainer().findBindInfo(bindName, bindType);
            return container.providerOnlyBindInfo(bindInfo, appContext).get();
        });
        //
        // .使用 container API 注册Bean
        DefaultBindInfoProviderAdapter<PojoBean> adapter1 = container.getBindInfoContainer().createInfoAdapter(PojoBean.class, null);
        adapter1.setBindID("id_a");
        adapter1.setBindName("aaa");
        adapter1.addInject("uuid", InstanceProvider.of("id_a_uuid"));
        DefaultBindInfoProviderAdapter<PojoBean> adapter2 = container.getBindInfoContainer().createInfoAdapter(PojoBean.class, null);
        adapter2.setBindID("id_b");
        adapter2.setBindName("bbb");
        adapter2.addInject("uuid", InstanceProvider.of("id_b_uuid"));
        container.init();
        //
        {
            ID annoID1 = new IDImpl("id_a");
            ID annoID2 = new IDImpl("id_b");
            PojoBean supplierA = container.providerOnlyAnnotation(PojoBean.class, annoID1, appContext).get();
            PojoBean supplierB = container.providerOnlyAnnotation(PojoBean.class, annoID2, appContext).get();
            //
            assert "id_a_uuid".equals(supplierA.getUuid());
            assert "id_b_uuid".equals(supplierB.getUuid());
        }
        //
        {
            Named annoID1 = new NamedImpl("aaa");
            Named annoID2 = new NamedImpl("bbb");
            PojoBean supplierA = container.providerOnlyAnnotation(PojoBean.class, annoID1, appContext).get();
            PojoBean supplierB = container.providerOnlyAnnotation(PojoBean.class, annoID2, appContext).get();
            //
            assert "id_a_uuid".equals(supplierA.getUuid());
            assert "id_b_uuid".equals(supplierB.getUuid());
        }
    }

    @Test
    public void annotationTest2() {
        BeanContainer container = new BeanContainer(PowerMockito.mock(Environment.class));
        ScopeContainer scopeContainer = container.getScopeContainer();
        scopeContainer.init();
        //
        assert container.providerOnlyAnnotation(PojoBean.class, null, appContext) == null;
        //
        try {
            Inject inject = PowerMockito.mock(Inject.class);
            container.providerOnlyAnnotation(PojoBean.class, inject, appContext);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" Annotation is not support.");
        }
    }

    @Test
    public void annotationTest3() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        PowerMockito.when(appContext.getEnvironment()).thenReturn(mockEnv);
        PowerMockito.when(mockEnv.evalString(anyString())).then(invocationOnMock -> invocationOnMock.getArguments()[0]);
        //
        //
        BeanContainer container = new BeanContainer(mockEnv);
        //
        InjectSettings injectSettings = PowerMockito.mock(InjectSettings.class);
        PowerMockito.when(injectSettings.value()).thenReturn("${MY_HOME}");
        PowerMockito.when(injectSettings.defaultValue()).thenReturn("{ABC}");
        //
        String envValue = container.providerOnlyAnnotation(String.class, injectSettings, appContext).get();
        assert "%MY_HOME%".equals(envValue);
    }
}