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
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.test.core.aop.ignore.level.LevelFooFunction;
import net.hasor.test.core.aop.ignore.level.l2.L2FooFunction;
import net.hasor.test.core.aop.ignore.thread.ThreadFooFunction;
import net.hasor.test.core.aop.ignore.types.*;
import net.hasor.test.core.basic.destroy.NonePrivateCallDestroyBean;
import net.hasor.test.core.basic.destroy.NonePublicCallDestroyBean;
import net.hasor.test.core.basic.destroy.WithoutAnnoCallDestroyBean;
import net.hasor.test.core.basic.implby.ImplSampleBean;
import net.hasor.test.core.basic.implby.SampleBeanFace;
import net.hasor.test.core.basic.implby.SampleBeanFaceByFace;
import net.hasor.test.core.basic.init.NonePrivateCallInitBean;
import net.hasor.test.core.basic.init.NonePublicCallInitBean;
import net.hasor.test.core.basic.init.WithoutAnnoCallInitBean;
import net.hasor.test.core.basic.inject.ByIDPropertyPojoBeanRef;
import net.hasor.test.core.basic.inject.ByNamePropertyPojoBeanRef;
import net.hasor.test.core.basic.inject.PropertyPojoBeanRef;
import net.hasor.test.core.basic.inject.constructor.SingleConstructorPojoBeanRef;
import net.hasor.test.core.basic.inject.jsr330.ByOrder;
import net.hasor.test.core.basic.inject.jsr330.Jsr330BasicRef;
import net.hasor.test.core.basic.inject.jsr330.Jsr330ConstructorRef;
import net.hasor.test.core.basic.inject.property.PropertyBean;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

import static org.mockito.Matchers.anyString;

public class ContainerUtilsTest {
    private Method initMethod_1    = null;
    private Method initMethod_2    = null;
    private Method initMethod_3    = null;
    private Method destroyMethod_1 = null;
    private Method destroyMethod_2 = null;
    private Method destroyMethod_3 = null;

    @Before
    public void testBefore() {
        try {
            initMethod_1 = NonePrivateCallInitBean.class.getDeclaredMethod("init");
        } catch (Exception e) {
        }
        try {
            initMethod_2 = NonePublicCallInitBean.class.getDeclaredMethod("init");
        } catch (Exception e) {
        }
        try {
            initMethod_3 = WithoutAnnoCallInitBean.class.getDeclaredMethod("init");
        } catch (Exception e) {
        }
        //
        try {
            destroyMethod_1 = NonePrivateCallDestroyBean.class.getDeclaredMethod("destroy");
        } catch (Exception e) {
        }
        try {
            destroyMethod_2 = NonePublicCallDestroyBean.class.getDeclaredMethod("destroy");
        } catch (Exception e) {
        }
        try {
            destroyMethod_3 = WithoutAnnoCallDestroyBean.class.getDeclaredMethod("destroy");
        } catch (Exception e) {
        }
    }

    @Test
    public void test1() {
        assert ContainerUtils.findImplClass(ImplSampleBean.class).equals(ImplSampleBean.class);
        assert ContainerUtils.findImplClass(SampleBeanFace.class).equals(ImplSampleBean.class);
        assert ContainerUtils.findImplClass(SampleBeanFaceByFace.class).equals(ImplSampleBean.class);
    }

    @Test
    public void initMethodTest1() {
        assert ContainerUtils.findInitMethod(NonePrivateCallInitBean.class, null) == null;
        assert ContainerUtils.findInitMethod(NonePublicCallInitBean.class, null).equals(initMethod_2);
        assert ContainerUtils.findInitMethod(WithoutAnnoCallInitBean.class, null) == null;
    }

    @Test
    public void initMethodTest2() {
        BindInfoContainer infoContainer = new BindInfoContainer(new SpiCallerContainer());
        //
        DefaultBindInfoProviderAdapter<NonePrivateCallInitBean> adapter_1 = infoContainer.createInfoAdapter(NonePrivateCallInitBean.class, null);
        adapter_1.initMethod("init");
        DefaultBindInfoProviderAdapter<NonePublicCallInitBean> adapter_2 = infoContainer.createInfoAdapter(NonePublicCallInitBean.class, null);
        adapter_2.initMethod("init");
        DefaultBindInfoProviderAdapter<WithoutAnnoCallInitBean> adapter_3 = infoContainer.createInfoAdapter(WithoutAnnoCallInitBean.class, null);
        adapter_3.initMethod("init");
        //
        //
        assert ContainerUtils.findInitMethod(NonePrivateCallInitBean.class, adapter_1) == null;
        assert ContainerUtils.findInitMethod(NonePublicCallInitBean.class, adapter_2).equals(initMethod_2);
        assert ContainerUtils.findInitMethod(WithoutAnnoCallInitBean.class, adapter_3).equals(initMethod_3);
    }

    @Test
    public void initMethodTest3() throws NoSuchMethodException {
        BindInfoContainer infoContainer = new BindInfoContainer(new SpiCallerContainer());
        //
        DefaultBindInfoProviderAdapter<NonePublicCallInitBean> adapter = infoContainer.createInfoAdapter(NonePublicCallInitBean.class, null);
        adapter.initMethod("isInit");
        //
        Method initMethod = NonePublicCallInitBean.class.getDeclaredMethod("isInit");
        //
        assert !ContainerUtils.findInitMethod(NonePublicCallInitBean.class, adapter).equals(initMethod);    // 即便指定了 init 但是同时配置了注解，因此注解优先
        assert ContainerUtils.findInitMethod(NonePublicCallInitBean.class, adapter).equals(initMethod_2);   //
    }

    @Test
    public void destroyMethodTest1() {
        assert ContainerUtils.findDestroyMethod(NonePrivateCallDestroyBean.class, null) == null;
        assert ContainerUtils.findDestroyMethod(NonePublicCallDestroyBean.class, null).equals(destroyMethod_2);
        assert ContainerUtils.findDestroyMethod(WithoutAnnoCallDestroyBean.class, null) == null;
    }

    @Test
    public void destroyMethodTest2() {
        BindInfoContainer infoContainer = new BindInfoContainer(new SpiCallerContainer());
        //
        DefaultBindInfoProviderAdapter<NonePrivateCallDestroyBean> adapter_1 = infoContainer.createInfoAdapter(NonePrivateCallDestroyBean.class, null);
        adapter_1.destroyMethod("destroy");
        DefaultBindInfoProviderAdapter<NonePublicCallDestroyBean> adapter_2 = infoContainer.createInfoAdapter(NonePublicCallDestroyBean.class, null);
        adapter_2.destroyMethod("destroy");
        DefaultBindInfoProviderAdapter<WithoutAnnoCallDestroyBean> adapter_3 = infoContainer.createInfoAdapter(WithoutAnnoCallDestroyBean.class, null);
        adapter_3.destroyMethod("destroy");
        //
        //
        assert ContainerUtils.findDestroyMethod(NonePrivateCallDestroyBean.class, adapter_1) == null;
        assert ContainerUtils.findDestroyMethod(NonePublicCallDestroyBean.class, adapter_2).equals(destroyMethod_2);
        assert ContainerUtils.findDestroyMethod(WithoutAnnoCallDestroyBean.class, adapter_3).equals(destroyMethod_3);
    }

    @Test
    public void destroyMethodTest3() throws NoSuchMethodException {
        BindInfoContainer infoContainer = new BindInfoContainer(new SpiCallerContainer());
        //
        DefaultBindInfoProviderAdapter<NonePublicCallDestroyBean> adapter = infoContainer.createInfoAdapter(NonePublicCallDestroyBean.class, null);
        adapter.destroyMethod("isDestroy");
        //
        Method destroyMethod = NonePublicCallDestroyBean.class.getDeclaredMethod("isDestroy");
        //
        assert !ContainerUtils.findDestroyMethod(NonePublicCallDestroyBean.class, adapter).equals(destroyMethod);    // 即便指定了 destroy 但是同时配置了注解，因此注解优先
        assert ContainerUtils.findDestroyMethod(NonePublicCallDestroyBean.class, adapter).equals(destroyMethod_2);   //
    }

    @Test
    public void testInject1() throws NoSuchFieldException {
        assert ContainerUtils.findInject(false, null) == null;
        //
        Annotation injectAnno_1 = ContainerUtils.findInject(false, //
                PropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        Annotation injectAnno_2 = ContainerUtils.findInject(false, //
                PropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        injectAnno_1.toString();
        //
        assert injectAnno_1 instanceof Named;
        assert injectAnno_2 instanceof Named;
        assert injectAnno_1 != injectAnno_2;
        assert injectAnno_1.equals(injectAnno_2);
        assert injectAnno_1.hashCode() == injectAnno_2.hashCode();
        assert !injectAnno_1.equals(new Object());
    }

    @Test
    public void testInject2() throws NoSuchFieldException {
        Annotation injectAnno_1 = ContainerUtils.findInject(false, //
                ByIDPropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        Annotation injectAnno_2 = ContainerUtils.findInject(false, //
                ByIDPropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        injectAnno_1.toString();
        //
        assert injectAnno_1 instanceof ID;
        assert injectAnno_2 instanceof ID;
        assert injectAnno_1 != injectAnno_2;
        assert injectAnno_1.equals(injectAnno_2);
        assert injectAnno_1.hashCode() == injectAnno_2.hashCode();
        assert !injectAnno_1.equals(new Object());
    }

    @Test
    public void testInject3() throws NoSuchFieldException {
        Annotation injectAnno_1 = ContainerUtils.findInject(false, //
                PropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        assert injectAnno_1 instanceof Named;
        assert ((Named) injectAnno_1).value().equals("");
        assert injectAnno_1.annotationType() == Named.class;
        //
        //
        Annotation injectAnno_2 = ContainerUtils.findInject(false, //
                ByIDPropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        assert injectAnno_2 instanceof ID;
        assert injectAnno_2.annotationType() == ID.class;
        assert ((ID) injectAnno_2).value().equals("my_pojoBean");
        //
        //
        Annotation injectAnno_3 = ContainerUtils.findInject(false, //
                ByNamePropertyPojoBeanRef.class.getDeclaredField("pojoBean").getAnnotations());
        assert injectAnno_3 instanceof Named;
        assert ((Named) injectAnno_3).value().equals("my_pojoBean");
        assert injectAnno_3.annotationType() == Named.class;
    }

    @Test
    public void jsrInject1() throws NoSuchFieldException {
        Annotation injectAnno_1 = ContainerUtils.findInject(false, //
                Jsr330BasicRef.class.getDeclaredField("pojoBean1").getAnnotations());
        Annotation injectAnno_2 = ContainerUtils.findInject(false, //
                Jsr330BasicRef.class.getDeclaredField("pojoBean2").getAnnotations());
        Annotation injectAnno_3 = ContainerUtils.findInject(false, //
                Jsr330BasicRef.class.getDeclaredField("pojoBean3").getAnnotations());
        Annotation injectAnno_4 = ContainerUtils.findInject(false, //
                Jsr330BasicRef.class.getDeclaredField("pojoBean4").getAnnotations());
        Annotation injectAnno_5 = ContainerUtils.findInject(false, //
                Jsr330BasicRef.class.getDeclaredField("pojoBean5").getAnnotations());
        Annotation injectAnno_6 = ContainerUtils.findInject(false, //
                Jsr330BasicRef.class.getDeclaredField("pojoBean6").getAnnotations());
        //
        //
        assert injectAnno_1 == null;
        assert injectAnno_2 instanceof Named && ((Named) injectAnno_2).value().equals("");
        assert injectAnno_3 instanceof Named && ((Named) injectAnno_3).value().equals(ByOrder.class.getName());
        assert injectAnno_4 instanceof Named && ((Named) injectAnno_4).value().equals(ByOrder.class.getName());
        assert injectAnno_5 instanceof Named && ((Named) injectAnno_5).value().equals(ByOrder.class.getName());
        assert injectAnno_6 instanceof Named && ((Named) injectAnno_6).value().equals(ByOrder.class.getName());
    }

    @Test
    public void testInject4() {
        assert !ContainerUtils.isInjectConstructor(PropertyPojoBeanRef.class.getConstructors()[0]);
        assert ContainerUtils.isInjectConstructor(SingleConstructorPojoBeanRef.class.getConstructors()[0]);
        assert ContainerUtils.isInjectConstructor(Jsr330ConstructorRef.class.getConstructors()[0]);
    }

    @Test
    public void testInject5() throws IOException, URISyntaxException {
        AppContext appContext = PowerMockito.mock(AppContext.class);
        Environment environment = PowerMockito.mock(Environment.class);
        Settings settings = new StandardContextSettings();
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getEnvironment().getSettings()).thenReturn(settings);
        PowerMockito.when(appContext.getEnvironment().evalString(anyString())).thenReturn("EVAL_STRING");
        //
        //
        class InjectSettingsImpl implements InjectSettings {
            private String value;
            private String defaultValue;

            public InjectSettingsImpl(String value, String defaultValue) {
                this.value = value;
                this.defaultValue = defaultValue;
            }

            @Override
            public String value() {
                return value;
            }

            @Override
            public String defaultValue() {
                return defaultValue;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return InjectSettings.class;
            }
        }
        //
        settings.addSetting("mock.test1", "abc");
        //
        assert ContainerUtils.injSettings(appContext, new InjectSettingsImpl("mock.test1", ""), String.class).equals("abc");
        assert ContainerUtils.injSettings(appContext, new InjectSettingsImpl("mock.test2", "123"), String.class).equals("123");
        assert ContainerUtils.injSettings(appContext, new InjectSettingsImpl("${java_home}", "123"), String.class).equals("EVAL_STRING");
        assert ContainerUtils.injSettings(appContext, null, Integer.TYPE).equals(0);
    }

    @Test
    public void testInject6() {
        NonePublicCallInitBean initBean = new NonePublicCallInitBean();
        Method initMethod = ContainerUtils.findInitMethod(NonePublicCallInitBean.class, null);
        assert !initBean.isInit();
        ContainerUtils.invokeMethod(initBean, initMethod);
        assert initBean.isInit();
        //
        //
        NonePublicCallDestroyBean destroyBean = new NonePublicCallDestroyBean();
        Method destroyMethod = ContainerUtils.findDestroyMethod(NonePublicCallDestroyBean.class, null);
        assert !destroyBean.isDestroy();
        ContainerUtils.invokeMethod(destroyBean, destroyMethod);
        assert destroyBean.isDestroy();
    }

    @Test
    public void testInject7() throws NoSuchMethodException {
        NonePrivateCallInitBean initBean = new NonePrivateCallInitBean();
        Method initMethod = NonePrivateCallInitBean.class.getDeclaredMethod("init");
        assert !initBean.isInit();
        ContainerUtils.invokeMethod(initBean, initMethod);
        assert initBean.isInit();
        //
        //
        NonePrivateCallDestroyBean destroyBean = new NonePrivateCallDestroyBean();
        Method destroyMethod = NonePrivateCallDestroyBean.class.getDeclaredMethod("destroy");
        assert !destroyBean.isDestroy();
        ContainerUtils.invokeMethod(destroyBean, destroyMethod);
        assert destroyBean.isDestroy();
    }

    @Test
    public void testInject8() throws NoSuchFieldException, IllegalAccessException {
        PropertyBean initBean = new PropertyBean();
        Field field = PropertyBean.class.getDeclaredField("intValue");
        //        field.setAccessible(true);
        //
        //        assert Integer.valueOf(0).equals(field.get(initBean));
        ContainerUtils.invokeField(field, initBean, "123");
        assert Integer.valueOf(123).equals(field.get(initBean));
    }

    @Test
    public void testAop1() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assert !ContainerUtils.testAopIgnore(PropertyBean.class, loader);
        //
        assert !ContainerUtils.testAopIgnore(LevelFooFunction.class, loader);
        assert ContainerUtils.testAopIgnore(ThreadFooFunction.class, loader);
        assert ContainerUtils.testAopIgnore(L2FooFunction.class, loader);
        //
        //
        assert !ContainerUtils.testAopIgnore(GrandFatherBean.class, loader);
        assert ContainerUtils.testAopIgnore(JamesBean.class, loader);
        assert !ContainerUtils.testAopIgnore(JamesSonBean.class, loader);
        assert ContainerUtils.testAopIgnore(WilliamBean.class, loader);
        assert ContainerUtils.testAopIgnore(WilliamSonBean.class, loader);
    }
}