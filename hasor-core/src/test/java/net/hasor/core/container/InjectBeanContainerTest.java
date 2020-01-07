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
import net.hasor.core.context.StatusAppContext;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.test.core.basic.inject.PropertyPojoBeanRef;
import net.hasor.test.core.basic.inject.constructor.BasicConstructorBean;
import net.hasor.test.core.basic.inject.constructor.ConstructorBean;
import net.hasor.test.core.basic.inject.constructor.ConstructorBeanByInjectSettingConfValue;
import net.hasor.test.core.basic.inject.jsr330.Jsr330ConstructorRef;
import net.hasor.test.core.basic.inject.jsr330.Jsr330MethodRef;
import net.hasor.test.core.basic.inject.members.*;
import net.hasor.test.core.basic.inject.property.PropertyBean;
import net.hasor.test.core.basic.inject.property.PropertyBeanByByInjectSettingConfValue;
import net.hasor.test.core.basic.pojo.PojoBean;
import net.hasor.test.core.enums.SelectEnum;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Constructor;

public class InjectBeanContainerTest {
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
    public void settingTest1() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        Settings settings = new InputStreamSettings();
        //
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        PowerMockito.when(appContext.getEnvironment()).thenReturn(mockEnv);
        PowerMockito.when(mockEnv.getSettings()).thenReturn(settings);
        //
        settings.addSetting("byteValue", 1);
        settings.addSetting("shortValue", 2);
        settings.addSetting("intValue", 3);
        settings.addSetting("longValue", 4);
        settings.addSetting("floatValue", 5.5);
        settings.addSetting("booleanValue", true);
        settings.addSetting("doubleValue", 6.6);
        settings.addSetting("charValue", " ");
        settings.addSetting("dateValue", 8);
        settings.addSetting("stringValue", 9);
        settings.addSetting("enumValue", SelectEnum.Three);
        //
        // 使用系统默认构造方法
        Constructor<?> constructor = ConstructorBeanByInjectSettingConfValue.class.getConstructors()[0];
        ConstructorBeanByInjectSettingConfValue injectConstructor = (ConstructorBeanByInjectSettingConfValue) beanContainer.providerOnlyConstructor(constructor, appContext, null).get();
        //
        assert injectConstructor.getByteValue() == 1;
        assert injectConstructor.getByteValue2() == 1;
        assert injectConstructor.getShortValue() == 2;
        assert injectConstructor.getShortValue2() == 2;
        assert injectConstructor.getIntValue() == 3;
        assert injectConstructor.getIntValue2() == 3;
        assert injectConstructor.getLongValue() == 4;
        assert injectConstructor.getLongValue2() == 4;
        assert injectConstructor.getFloatValue() == 5.5;
        assert injectConstructor.getFloatValue2() == 5.5;
        assert injectConstructor.getDoubleValue() == 6.6;
        assert injectConstructor.getDoubleValue2() == 6.6;
        assert injectConstructor.isBooleanValue();
        assert injectConstructor.getBooleanValue2();
        //
        assert injectConstructor.getDateValue1().getTime() == 8;
        assert injectConstructor.getDateValue2().getTime() == 8;
        assert injectConstructor.getDateValue3().getTime() == 8;
        assert injectConstructor.getDateValue4().getTime() == 8;
        assert injectConstructor.getStringValue().equals("9");
        assert injectConstructor.getEnumValue() == SelectEnum.Three;
        assert injectConstructor.getCharValue() == 32;
        assert injectConstructor.getCharValue2() == ' ';
    }

    @Test
    public void settingTest2() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        Settings settings = new InputStreamSettings();
        //
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        PowerMockito.when(appContext.getEnvironment()).thenReturn(mockEnv);
        PowerMockito.when(mockEnv.getSettings()).thenReturn(settings);
        //
        //
        // 使用系统默认构造方法
        Constructor<?> constructor = ConstructorBeanByInjectSettingConfValue.class.getConstructors()[0];
        ConstructorBeanByInjectSettingConfValue injectConstructor = (ConstructorBeanByInjectSettingConfValue) beanContainer.providerOnlyConstructor(constructor, appContext, null).get();
        //
        assert injectConstructor.getByteValue() == 0;
        assert injectConstructor.getByteValue2() == null;
        assert injectConstructor.getShortValue() == 0;
        assert injectConstructor.getShortValue2() == null;
        assert injectConstructor.getIntValue() == 0;
        assert injectConstructor.getIntValue2() == null;
        assert injectConstructor.getLongValue() == 0;
        assert injectConstructor.getLongValue2() == null;
        assert injectConstructor.getFloatValue() == 0;
        assert injectConstructor.getFloatValue2() == null;
        assert injectConstructor.getDoubleValue() == 0;
        assert injectConstructor.getDoubleValue2() == null;
        assert !injectConstructor.isBooleanValue();
        assert injectConstructor.getBooleanValue2() == null;
        //
        assert injectConstructor.getDateValue1() == null;
        assert injectConstructor.getDateValue2() == null;
        assert injectConstructor.getDateValue3() == null;
        assert injectConstructor.getDateValue4() == null;
        assert injectConstructor.getStringValue() == null;
        assert injectConstructor.getEnumValue() == null;
    }

    @Test
    public void settingTest3() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        Settings settings = new InputStreamSettings();
        //
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        PowerMockito.when(appContext.getEnvironment()).thenReturn(mockEnv);
        PowerMockito.when(mockEnv.getSettings()).thenReturn(settings);
        //
        //
        // 使用系统默认构造方法
        Constructor<?> constructor = BasicConstructorBean.class.getConstructors()[0];
        BasicConstructorBean injectConstructor = (BasicConstructorBean) beanContainer.providerOnlyConstructor(constructor, appContext, null).get();
        //
        assert injectConstructor.getByteValue() == 0;
        assert injectConstructor.getByteValue2() == null;
        assert injectConstructor.getShortValue() == 0;
        assert injectConstructor.getShortValue2() == null;
        assert injectConstructor.getIntValue() == 0;
        assert injectConstructor.getIntValue2() == null;
        assert injectConstructor.getLongValue() == 0;
        assert injectConstructor.getLongValue2() == null;
        assert injectConstructor.getFloatValue() == 0;
        assert injectConstructor.getFloatValue2() == null;
        assert injectConstructor.getDoubleValue() == 0;
        assert injectConstructor.getDoubleValue2() == null;
        assert !injectConstructor.isBooleanValue();
        assert injectConstructor.getBooleanValue2() == null;
        //
        //
        try {
            Constructor<?> c = ConstructorBean.class.getConstructors()[0];
            beanContainer.providerOnlyConstructor(c, appContext, null).get();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith("No default constructor found.");
        }
    }

    @Test
    public void injectTest1() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        Settings settings = new InputStreamSettings();
        //
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        PowerMockito.when(appContext.getEnvironment()).thenReturn(mockEnv);
        PowerMockito.when(mockEnv.getSettings()).thenReturn(settings);
        //
        settings.addSetting("byteValue", 1);
        settings.addSetting("shortValue", 2);
        settings.addSetting("intValue", 3);
        settings.addSetting("longValue", 4);
        settings.addSetting("floatValue", 5.5);
        settings.addSetting("booleanValue", true);
        settings.addSetting("doubleValue", 6.6);
        settings.addSetting("charValue", " ");
        settings.addSetting("dateValue", 8);
        settings.addSetting("stringValue", 9);
        settings.addSetting("enumValue", SelectEnum.Three);
        //
        PropertyBeanByByInjectSettingConfValue confValue = new PropertyBeanByByInjectSettingConfValue();
        PropertyBeanByByInjectSettingConfValue value = beanContainer.justInject(confValue, PropertyBeanByByInjectSettingConfValue.class, appContext);
        assert confValue == value;
        //
        assert confValue.getByteValue() == 1;
        assert confValue.getByteValue2() == 1;
        assert confValue.getShortValue() == 2;
        assert confValue.getShortValue2() == 2;
        assert confValue.getIntValue() == 3;
        assert confValue.getIntValue2() == 3;
        assert confValue.getLongValue() == 4;
        assert confValue.getLongValue2() == 4;
        assert confValue.getFloatValue() == 5.5;
        assert confValue.getFloatValue2() == 5.5;
        assert confValue.getDoubleValue() == 6.6;
        assert confValue.getDoubleValue2() == 6.6;
        assert confValue.isBooleanValue();
        assert confValue.getBooleanValue2();
        //
        assert confValue.getDateValue1().getTime() == 8;
        assert confValue.getDateValue2().getTime() == 8;
        assert confValue.getDateValue3().getTime() == 8;
        assert confValue.getDateValue4().getTime() == 8;
        assert confValue.getStringValue().equals("9");
        assert confValue.getEnumValue() == SelectEnum.Three;
        assert confValue.getCharValue() == 32;
        assert confValue.getCharValue2() == ' ';
    }

    @Test
    public void injectTest2() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        //
        BeanContainer container = new BeanContainer(mockEnv);
        DefaultBindInfoProviderAdapter<PropertyBean> adapter = container.getBindInfoContainer().createInfoAdapter(PropertyBean.class, null);
        adapter.addInject("byteValue", InstanceProvider.of(123));
        PropertyBean confValue = new PropertyBean();
        PropertyBean propertyBean = container.justInject(confValue, adapter, appContext);
        //
        assert propertyBean == confValue;
        assert propertyBean.getByteValue() == 123;
    }

    @Test
    public void injectTest3() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BeanContainer container = new BeanContainer(mockEnv);
        //
        InjectAppContextOk bean = container.providerOnlyType(InjectAppContextOk.class, appContext, null).get();
        assert bean.getPojoBean().getUuid().equals("create by AppContextAware");
        //
        try {
            container.providerOnlyType(InjectAppContextFailed.class, appContext, null).get();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("test Inject Error");
            assert e.getCause() == null;
        }
    }

    @Test
    public void injectTest4() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        //
        BeanContainer container = new BeanContainer(mockEnv);
        DefaultBindInfoProviderAdapter<InjectBindInfoOk> adapter1 = container.getBindInfoContainer().createInfoAdapter(InjectBindInfoOk.class, null);
        adapter1.setBindID("aaa");
        DefaultBindInfoProviderAdapter<InjectBindInfoFailed> adapter2 = container.getBindInfoContainer().createInfoAdapter(InjectBindInfoFailed.class, null);
        adapter2.setBindID("bbb");
        //
        InjectBindInfoOk bean = container.providerOnlyBindInfo(adapter1, appContext).get();
        assert bean.getPojoBean().getUuid().equals("create by BindInfoAware ,bindID is aaa");
        //
        try {
            container.providerOnlyBindInfo(adapter2, appContext).get();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("test Inject Error");
            assert e.getCause() == null;
        }
    }

    @Test
    public void injectTest5() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BeanContainer container = new BeanContainer(mockEnv);
        //
        InjectMembersOk bean = container.providerOnlyType(InjectMembersOk.class, appContext, null).get();
        assert bean.getPojoBean().getUuid().equals("create by InjectMembers");
        //
        try {
            container.providerOnlyType(InjectMembersFailed.class, appContext, null).get();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("test Inject Error");
            assert e.getCause() == null;
        }
    }

    @Test
    public void injectTest6() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BeanContainer container = new BeanContainer(mockEnv);
        //
        Jsr330MethodRef bean1 = container.providerOnlyType(Jsr330MethodRef.class, appContext, null).get();
        Jsr330ConstructorRef bean2 = container.providerOnlyType(Jsr330ConstructorRef.class, appContext, null).get();
        assert bean1.getPojoBean() != null;
        assert bean2.getPojoBean() != null;
    }

    @Test
    public void injectTest7() throws Throwable {
        PojoBean mockBean1 = new PojoBean();
        PojoBean mockBean2 = new PojoBean();
        Environment env = new StandardEnvironment();
        AppContext appContext3 = new AppContextWarp(new StatusAppContext(env));
        appContext3.start((Module) apiBinder -> {
            apiBinder.bindType(PojoBean.class).toInstance(mockBean1);
            apiBinder.bindType(PropertyPojoBeanRef.class)//
                    .injectValue("pojoBean", mockBean2);
        });
        PropertyPojoBeanRef ref = new PropertyPojoBeanRef();
        try {
            appContext3.justInject(ref);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" property 'pojoBean' duplicate.");
        }
    }
}