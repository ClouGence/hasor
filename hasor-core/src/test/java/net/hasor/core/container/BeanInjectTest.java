package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Settings;
import net.hasor.core.SingletonMode;
import net.hasor.core.container.beans.TestBean;
import net.hasor.core.container.inject.*;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.setting.AbstractSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
public class BeanInjectTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment();
    }
    @Test
    public void builderTest1() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.addInject("uuid", InstanceProvider.of("paramUUID"));
        adapter.addInject("name", InstanceProvider.of("paramName"));
        //
        TestBean instance1 = (TestBean) container.getInstance(adapter, appContext);
        assert "paramUUID".equals(instance1.getUuid());
        assert "paramName".equals(instance1.getName());
        //
        TestBean instance2 = new TestBean();
        assert !"paramUUID".equals(instance2.getUuid());
        assert !"paramName".equals(instance2.getName());
        container.justInject(instance2, adapter, appContext);
        assert "paramUUID".equals(instance2.getUuid());
        assert "paramName".equals(instance2.getName());
    }
    //
    @Test
    public void builderTest2() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.getInstance(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                BindInfo<Object> bindInfo = container.findBindInfo((String) invocationOnMock.getArguments()[0]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        PowerMockito.when(appContext.getInstance((Class<Object>) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                BindInfo<Object> bindInfo = container.findBindInfo(null, (Class<Object>) invocationOnMock.getArguments()[0]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        PowerMockito.when(appContext.findBindingBean(anyString(), (Class<Object>) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                BindInfo<Object> bindInfo = container.findBindInfo((String) arguments[0], (Class<Object>) arguments[1]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("12345");
        adapter.addInject("uuid", InstanceProvider.of("paramUUID"));
        adapter.addInject("name", InstanceProvider.of("paramName"));
        //
        TestBeanRef instance1 = container.getInstance(TestBeanRef.class, appContext);
        assert instance1.getTestBean() != null;
        assert "paramUUID".equals(instance1.getTestBean().getUuid());
        assert "paramName".equals(instance1.getTestBean().getName());
        //
        ConstructorTestBeanRef instance2 = container.getInstance(ConstructorTestBeanRef.class, appContext);
        assert instance2.getTestBean() != null;
        assert "paramUUID".equals(instance2.getTestBean().getUuid());
        assert "paramName".equals(instance2.getTestBean().getName());
    }
    //
    @Test
    public void builderTest3() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.findBindingBean(anyString(), (Class<Object>) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                BindInfo<?> bindInfo = container.findBindInfo((String) arguments[0], (Class<?>) arguments[1]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("11111");
        adapter1.setBindName("myBean");
        adapter1.addInject("uuid", InstanceProvider.of("paramUUID_11"));
        adapter1.addInject("name", InstanceProvider.of("paramName_11"));
        //
        //
        ByNameTestBeanRef instance1 = container.getInstance(ByNameTestBeanRef.class, appContext);
        assert instance1.getTestBean() == null;
        ByNameConstructorTestBeanRef instance2 = container.getInstance(ByNameConstructorTestBeanRef.class, appContext);
        assert instance2.getTestBean() == null;
        //
        //
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("22222");
        adapter2.setBindName("testBean");
        adapter2.addInject("uuid", InstanceProvider.of("paramUUID_22"));
        adapter2.addInject("name", InstanceProvider.of("paramName_22"));
        //
        //
        ByNameTestBeanRef instance3 = container.getInstance(ByNameTestBeanRef.class, appContext);
        assert instance3.getTestBean() != null;
        assert "paramUUID_22".equals(instance3.getTestBean().getUuid());
        assert "paramName_22".equals(instance3.getTestBean().getName());
        ByNameConstructorTestBeanRef instance4 = container.getInstance(ByNameConstructorTestBeanRef.class, appContext);
        assert instance4.getTestBean() != null;
        assert "paramUUID_22".equals(instance4.getTestBean().getUuid());
        assert "paramName_22".equals(instance4.getTestBean().getName());
    }
    //
    @Test
    public void builderTest4() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.getInstance(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                BindInfo<?> bindInfo = container.findBindInfo((String) arguments[0]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("11111");
        adapter1.setBindName("11111");
        adapter1.addInject("uuid", InstanceProvider.of("paramUUID_11"));
        adapter1.addInject("name", InstanceProvider.of("paramName_11"));
        //
        //
        ByIDTestBeanRef instance1 = container.getInstance(ByIDTestBeanRef.class, appContext);
        assert instance1.getTestBean() == null;
        ByIDConstructorTestBeanRef instance2 = container.getInstance(ByIDConstructorTestBeanRef.class, appContext);
        assert instance2.getTestBean() == null;
        //
        //
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("testBean");
        adapter2.setBindName("testBean");
        adapter2.addInject("uuid", InstanceProvider.of("paramUUID_22"));
        adapter2.addInject("name", InstanceProvider.of("paramName_22"));
        //
        //
        ByIDTestBeanRef instance3 = container.getInstance(ByIDTestBeanRef.class, appContext);
        assert instance3.getTestBean() != null;
        assert "paramUUID_22".equals(instance3.getTestBean().getUuid());
        assert "paramName_22".equals(instance3.getTestBean().getName());
        ByIDConstructorTestBeanRef instance4 = container.getInstance(ByIDConstructorTestBeanRef.class, appContext);
        assert instance4.getTestBean() != null;
        assert "paramUUID_22".equals(instance4.getTestBean().getUuid());
        assert "paramName_22".equals(instance4.getTestBean().getName());
    }
    //
    @Test
    public void builderTest5() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        InjectMembersBean.resetInit();
        container.getInstance(InjectMembersBean.class, appContext);
        assert InjectMembersBean.isStaticInit();
        //
        try {
            InjectMembersThrowBean.resetInit();
            container.getInstance(InjectMembersThrowBean.class, appContext);
            assert false;
        } catch (Exception e) {
            assert "testError".equals(e.getMessage());
        }
        //
        InjectAppContextAwareBean.resetInit();
        container.getInstance(InjectAppContextAwareBean.class, appContext);
        assert InjectAppContextAwareBean.isStaticInit();
    }
    //
    @Test
    public void builderTest6() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        InjectSettingDefaultValueBean valueBean = container.getInstance(InjectSettingDefaultValueBean.class, appContext);
        //
        assert valueBean.getByteValue() == 0;
        assert valueBean.getByteValue2() == null;
        assert valueBean.getShortValue() == 0;
        assert valueBean.getShortValue2() == null;
        assert valueBean.getIntValue() == 0;
        assert valueBean.getIntValue2() == null;
        assert valueBean.getLongValue() == 0;
        assert valueBean.getLongValue2() == null;
        //
        assert valueBean.getFloatValue() == 0.0f;
        assert valueBean.getFloatValue2() == null;
        assert valueBean.getDoubleValue() == 0.0d;
        assert valueBean.getDoubleValue2() == null;
        //
        assert !valueBean.isBooleanValue();
        assert valueBean.getBooleanValue2() == null;
        //
        assert valueBean.getCharValue() == '\u0000';
        assert valueBean.getCharValue2() == null;
        //
        assert valueBean.getDateValue1() == null;
        assert valueBean.getDateValue2() == null;
        assert valueBean.getDateValue3() == null;
        assert valueBean.getDateValue4() == null;
        //
        assert valueBean.getStringValue() == null;
        assert valueBean.getEnumValue() == null;
    }
    //
    @Test
    public void builderTest7() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        AbstractSettings settings = environment.getSettings();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        InjectSettingValueBean valueBean = container.getInstance(InjectSettingValueBean.class, appContext);
        assert valueBean.getByteValue() == 0;
        assert valueBean.getByteValue2() == null;
        assert valueBean.getShortValue() == 0;
        assert valueBean.getShortValue2() == null;
        assert valueBean.getIntValue() == 0;
        assert valueBean.getIntValue2() == null;
        assert valueBean.getLongValue() == 0;
        assert valueBean.getLongValue2() == null;
        assert valueBean.getFloatValue() == 0.0f;
        assert valueBean.getFloatValue2() == null;
        assert valueBean.getDoubleValue() == 0.0d;
        assert valueBean.getDoubleValue2() == null;
        assert !valueBean.isBooleanValue();
        assert valueBean.getBooleanValue2() == null;
        assert valueBean.getCharValue() == '\u0000';
        assert valueBean.getCharValue2() == null;
        assert valueBean.getDateValue1() == null;
        assert valueBean.getDateValue2() == null;
        assert valueBean.getDateValue3() == null;
        assert valueBean.getDateValue4() == null;
        assert valueBean.getStringValue() == null;
        assert valueBean.getEnumValue() == null;
        //
        String nameSpace = Settings.DefaultNameSpace;
        settings.addSetting("byteValue", "12", nameSpace);
        settings.addSetting("shortValue", "12", nameSpace);
        settings.addSetting("intValue", "12", nameSpace);
        settings.addSetting("longValue", "12", nameSpace);
        settings.addSetting("floatValue", "12.12", nameSpace);
        settings.addSetting("doubleValue", "12.12", nameSpace);
        settings.addSetting("booleanValue", "true", nameSpace);
        settings.addSetting("charValue", "12", nameSpace);
        settings.addSetting("dateValue", "12", nameSpace);
        settings.addSetting("stringValue", "stringValue", nameSpace);
        settings.addSetting("enumValue", "singleton", nameSpace);
        //
        InjectSettingValueBean valueBean2 = container.getInstance(InjectSettingValueBean.class, appContext);
        assert valueBean2.getByteValue() == 12;
        assert valueBean2.getByteValue2() == 12;
        assert valueBean2.getShortValue() == 12;
        assert valueBean2.getShortValue2() == 12;
        assert valueBean2.getIntValue() == 12;
        assert valueBean2.getIntValue2() == 12;
        assert valueBean2.getLongValue() == 12;
        assert valueBean2.getLongValue2() == 12;
        assert valueBean2.getFloatValue() == 12.12f;
        assert valueBean2.getFloatValue2() == 12.12f;
        assert valueBean2.getDoubleValue() == 12.12d;
        assert valueBean2.getDoubleValue2() == 12.12d;
        assert valueBean2.isBooleanValue();
        assert valueBean2.getBooleanValue2();
        assert valueBean2.getCharValue() == '1'; // 12  first char is '1'
        assert valueBean2.getCharValue2() == '1';
        assert valueBean2.getDateValue1().getTime() == 12;
        assert valueBean2.getDateValue2().getTime() == 12;
        assert valueBean2.getDateValue3().getTime() == 12;
        assert valueBean2.getDateValue4().getTime() == 12;
        assert valueBean2.getStringValue().equals("stringValue");
        assert valueBean2.getEnumValue() == SingletonMode.Singleton;
    }
    //
    @Test
    public void builderTest8() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        InjectSettingEnvValueBean valueBean = container.getInstance(InjectSettingEnvValueBean.class, appContext);
        assert valueBean.getByteValue() == 0;
        assert valueBean.getByteValue2() == null;
        assert valueBean.getShortValue() == 0;
        assert valueBean.getShortValue2() == null;
        assert valueBean.getIntValue() == 0;
        assert valueBean.getIntValue2() == null;
        assert valueBean.getLongValue() == 0;
        assert valueBean.getLongValue2() == null;
        assert valueBean.getFloatValue() == 0.0f;
        assert valueBean.getFloatValue2() == null;
        assert valueBean.getDoubleValue() == 0.0d;
        assert valueBean.getDoubleValue2() == null;
        assert !valueBean.isBooleanValue();
        assert valueBean.getBooleanValue2() == null;
        assert valueBean.getCharValue() == '\u0000';
        assert valueBean.getCharValue2() == null;
        assert valueBean.getDateValue1() == null;
        assert valueBean.getDateValue2() == null;
        assert valueBean.getDateValue3() == null;
        assert valueBean.getDateValue4() == null;
        assert valueBean.getStringValue().equals("");
        assert valueBean.getEnumValue() == null;
        //
        environment.addEnvVar("byteValue", "12");
        environment.addEnvVar("shortValue", "12");
        environment.addEnvVar("intValue", "12");
        environment.addEnvVar("longValue", "12");
        environment.addEnvVar("floatValue", "12.12");
        environment.addEnvVar("doubleValue", "12.12");
        environment.addEnvVar("booleanValue", "true");
        environment.addEnvVar("charValue", "12");
        environment.addEnvVar("dateValue", "12");
        environment.addEnvVar("stringValue", "stringValue");
        environment.addEnvVar("enumValue", "singleton");
        //
        InjectSettingEnvValueBean valueBean2 = container.getInstance(InjectSettingEnvValueBean.class, appContext);
        assert valueBean2.getByteValue() == 12;
        assert valueBean2.getByteValue2() == 12;
        assert valueBean2.getShortValue() == 12;
        assert valueBean2.getShortValue2() == 12;
        assert valueBean2.getIntValue() == 12;
        assert valueBean2.getIntValue2() == 12;
        assert valueBean2.getLongValue() == 12;
        assert valueBean2.getLongValue2() == 12;
        assert valueBean2.getFloatValue() == 12.12f;
        assert valueBean2.getFloatValue2() == 12.12f;
        assert valueBean2.getDoubleValue() == 12.12d;
        assert valueBean2.getDoubleValue2() == 12.12d;
        assert valueBean2.isBooleanValue();
        assert valueBean2.getBooleanValue2();
        assert valueBean2.getCharValue() == '1'; // 12  first char is '1'
        assert valueBean2.getCharValue2() == '1';
        assert valueBean2.getDateValue1().getTime() == 12;
        assert valueBean2.getDateValue2().getTime() == 12;
        assert valueBean2.getDateValue3().getTime() == 12;
        assert valueBean2.getDateValue4().getTime() == 12;
        assert valueBean2.getStringValue().equals("stringValue");
        assert valueBean2.getEnumValue() == SingletonMode.Singleton;
        //
        InjectSettingEnvValueBean valueBean3 = new InjectSettingEnvValueBean();
        container.justInject(valueBean3, InjectSettingEnvValueBean.class, appContext);
        assert valueBean3.getByteValue() == 12;
        assert valueBean3.getByteValue2() == 12;
        assert valueBean3.getShortValue() == 12;
        assert valueBean3.getShortValue2() == 12;
        assert valueBean3.getIntValue() == 12;
        assert valueBean3.getIntValue2() == 12;
        assert valueBean3.getLongValue() == 12;
        assert valueBean3.getLongValue2() == 12;
        assert valueBean3.getFloatValue() == 12.12f;
        assert valueBean3.getFloatValue2() == 12.12f;
        assert valueBean3.getDoubleValue() == 12.12d;
        assert valueBean3.getDoubleValue2() == 12.12d;
        assert valueBean3.isBooleanValue();
        assert valueBean3.getBooleanValue2();
        assert valueBean3.getCharValue() == '1'; // 12  first char is '1'
        assert valueBean3.getCharValue2() == '1';
        assert valueBean3.getDateValue1().getTime() == 12;
        assert valueBean3.getDateValue2().getTime() == 12;
        assert valueBean3.getDateValue3().getTime() == 12;
        assert valueBean3.getDateValue4().getTime() == 12;
        assert valueBean3.getStringValue().equals("stringValue");
        assert valueBean3.getEnumValue() == SingletonMode.Singleton;
    }
    //
    @Test
    public void builderTest9() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        try {
            container.getInstance(SimpleInjectBeanExt.class, appContext);
            assert false;
        } catch (IllegalStateException e) {
            assert e.getMessage().endsWith("property 'name' duplicate.");
        }
    }
    //
    //
    @Test
    public void builderTest10() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Constructor<?> constructor = ConstructorInjectSettingDefaultValueBean.class.getConstructors()[0];
        ConstructorInjectSettingDefaultValueBean valueBean = (ConstructorInjectSettingDefaultValueBean) container.getInstance(constructor, appContext);
        //
        assert valueBean.getByteValue() == 0;
        assert valueBean.getByteValue2() == null;
        assert valueBean.getShortValue() == 0;
        assert valueBean.getShortValue2() == null;
        assert valueBean.getIntValue() == 0;
        assert valueBean.getIntValue2() == null;
        assert valueBean.getLongValue() == 0;
        assert valueBean.getLongValue2() == null;
        //
        assert valueBean.getFloatValue() == 0.0f;
        assert valueBean.getFloatValue2() == null;
        assert valueBean.getDoubleValue() == 0.0d;
        assert valueBean.getDoubleValue2() == null;
        //
        assert !valueBean.isBooleanValue();
        assert valueBean.getBooleanValue2() == null;
        //
        assert valueBean.getCharValue() == '\u0000';
        assert valueBean.getCharValue2() == null;
        //
        assert valueBean.getDateValue1() == null;
        assert valueBean.getDateValue2() == null;
        assert valueBean.getDateValue3() == null;
        assert valueBean.getDateValue4() == null;
        //
        assert valueBean.getStringValue() == null;
        assert valueBean.getEnumValue() == null;
    }
    //
    @Test
    public void builderTest11() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        AbstractSettings settings = environment.getSettings();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Constructor<?> constructor = ConstructorInjectSettingValueBean.class.getConstructors()[0];
        ConstructorInjectSettingValueBean valueBean = (ConstructorInjectSettingValueBean) container.getInstance(constructor, appContext);
        assert valueBean.getByteValue() == 0;
        assert valueBean.getByteValue2() == null;
        assert valueBean.getShortValue() == 0;
        assert valueBean.getShortValue2() == null;
        assert valueBean.getIntValue() == 0;
        assert valueBean.getIntValue2() == null;
        assert valueBean.getLongValue() == 0;
        assert valueBean.getLongValue2() == null;
        assert valueBean.getFloatValue() == 0.0f;
        assert valueBean.getFloatValue2() == null;
        assert valueBean.getDoubleValue() == 0.0d;
        assert valueBean.getDoubleValue2() == null;
        assert !valueBean.isBooleanValue();
        assert valueBean.getBooleanValue2() == null;
        assert valueBean.getCharValue() == '\u0000';
        assert valueBean.getCharValue2() == null;
        assert valueBean.getDateValue1() == null;
        assert valueBean.getDateValue2() == null;
        assert valueBean.getDateValue3() == null;
        assert valueBean.getDateValue4() == null;
        assert valueBean.getStringValue() == null;
        assert valueBean.getEnumValue() == null;
        //
        String nameSpace = Settings.DefaultNameSpace;
        settings.addSetting("byteValue", "12", nameSpace);
        settings.addSetting("shortValue", "12", nameSpace);
        settings.addSetting("intValue", "12", nameSpace);
        settings.addSetting("longValue", "12", nameSpace);
        settings.addSetting("floatValue", "12.12", nameSpace);
        settings.addSetting("doubleValue", "12.12", nameSpace);
        settings.addSetting("booleanValue", "true", nameSpace);
        settings.addSetting("charValue", "12", nameSpace);
        settings.addSetting("dateValue", "12", nameSpace);
        settings.addSetting("stringValue", "stringValue", nameSpace);
        settings.addSetting("enumValue", "singleton", nameSpace);
        //
        ConstructorInjectSettingValueBean valueBean2 = (ConstructorInjectSettingValueBean) container.getInstance(constructor, appContext);
        assert valueBean2.getByteValue() == 12;
        assert valueBean2.getByteValue2() == 12;
        assert valueBean2.getShortValue() == 12;
        assert valueBean2.getShortValue2() == 12;
        assert valueBean2.getIntValue() == 12;
        assert valueBean2.getIntValue2() == 12;
        assert valueBean2.getLongValue() == 12;
        assert valueBean2.getLongValue2() == 12;
        assert valueBean2.getFloatValue() == 12.12f;
        assert valueBean2.getFloatValue2() == 12.12f;
        assert valueBean2.getDoubleValue() == 12.12d;
        assert valueBean2.getDoubleValue2() == 12.12d;
        assert valueBean2.isBooleanValue();
        assert valueBean2.getBooleanValue2();
        assert valueBean2.getCharValue() == '1'; // 12  first char is '1'
        assert valueBean2.getCharValue2() == '1';
        assert valueBean2.getDateValue1().getTime() == 12;
        assert valueBean2.getDateValue2().getTime() == 12;
        assert valueBean2.getDateValue3().getTime() == 12;
        assert valueBean2.getDateValue4().getTime() == 12;
        assert valueBean2.getStringValue().equals("stringValue");
        assert valueBean2.getEnumValue() == SingletonMode.Singleton;
    }
    //
    @Test
    public void builderTest12() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        //
        Constructor<?> constructor = ConstructorInjectSettingEnvValueBean.class.getConstructors()[0];
        ConstructorInjectSettingEnvValueBean valueBean = (ConstructorInjectSettingEnvValueBean) container.getInstance(constructor, appContext);
        assert valueBean.getByteValue() == 0;
        assert valueBean.getByteValue2() == null;
        assert valueBean.getShortValue() == 0;
        assert valueBean.getShortValue2() == null;
        assert valueBean.getIntValue() == 0;
        assert valueBean.getIntValue2() == null;
        assert valueBean.getLongValue() == 0;
        assert valueBean.getLongValue2() == null;
        assert valueBean.getFloatValue() == 0.0f;
        assert valueBean.getFloatValue2() == null;
        assert valueBean.getDoubleValue() == 0.0d;
        assert valueBean.getDoubleValue2() == null;
        assert !valueBean.isBooleanValue();
        assert valueBean.getBooleanValue2() == null;
        assert valueBean.getCharValue() == '\u0000';
        assert valueBean.getCharValue2() == null;
        assert valueBean.getDateValue1() == null;
        assert valueBean.getDateValue2() == null;
        assert valueBean.getDateValue3() == null;
        assert valueBean.getDateValue4() == null;
        assert valueBean.getStringValue().equals("");
        assert valueBean.getEnumValue() == null;
        //
        environment.addEnvVar("byteValue", "12");
        environment.addEnvVar("shortValue", "12");
        environment.addEnvVar("intValue", "12");
        environment.addEnvVar("longValue", "12");
        environment.addEnvVar("floatValue", "12.12");
        environment.addEnvVar("doubleValue", "12.12");
        environment.addEnvVar("booleanValue", "true");
        environment.addEnvVar("charValue", "12");
        environment.addEnvVar("dateValue", "12");
        environment.addEnvVar("stringValue", "stringValue");
        environment.addEnvVar("enumValue", "singleton");
        //
        ConstructorInjectSettingEnvValueBean valueBean2 = (ConstructorInjectSettingEnvValueBean) container.getInstance(constructor, appContext);
        assert valueBean2.getByteValue() == 12;
        assert valueBean2.getByteValue2() == 12;
        assert valueBean2.getShortValue() == 12;
        assert valueBean2.getShortValue2() == 12;
        assert valueBean2.getIntValue() == 12;
        assert valueBean2.getIntValue2() == 12;
        assert valueBean2.getLongValue() == 12;
        assert valueBean2.getLongValue2() == 12;
        assert valueBean2.getFloatValue() == 12.12f;
        assert valueBean2.getFloatValue2() == 12.12f;
        assert valueBean2.getDoubleValue() == 12.12d;
        assert valueBean2.getDoubleValue2() == 12.12d;
        assert valueBean2.isBooleanValue();
        assert valueBean2.getBooleanValue2();
        assert valueBean2.getCharValue() == '1'; // 12  first char is '1'
        assert valueBean2.getCharValue2() == '1';
        assert valueBean2.getDateValue1().getTime() == 12;
        assert valueBean2.getDateValue2().getTime() == 12;
        assert valueBean2.getDateValue3().getTime() == 12;
        assert valueBean2.getDateValue4().getTime() == 12;
        assert valueBean2.getStringValue().equals("stringValue");
        assert valueBean2.getEnumValue() == SingletonMode.Singleton;
    }
}
