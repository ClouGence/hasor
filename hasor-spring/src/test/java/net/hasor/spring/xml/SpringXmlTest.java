/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.xml;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.test.spring.HasorBean;
import net.hasor.test.spring.SpringBean;
import net.hasor.test.spring.mod1.*;
import net.hasor.utils.StringUtils;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @version : 2016年2月15日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpringXmlTest {
    @Test
    public void oneSpringModule() {
        TestModuleA.reset();
        TestModuleB.reset();
        TestModuleC.reset();
        TestModuleD.reset();
        //
        String settingFile = "net_hasor_spring/spring-one-module.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile);) {
            AppContext appContext = context.getBean(AppContext.class);
            //
            assert TestModuleA.isInit();
            assert TestModuleB.isInit();
            assert TestModuleC.isInit();
            assert !TestModuleD.isInit();
            //
            assert appContext.getBindInfo(TestModuleA.class) != null;
            assert appContext.getBindInfo(TestModuleB.class) != null;
            assert appContext.getBindInfo(TestModuleC.class) != null;
            assert appContext.getBindInfo(TestModuleD.class) == null;
            //
            TestModuleA moduleA = appContext.getInstance(TestModuleA.class);
            TestModuleB moduleB = appContext.getInstance(TestModuleB.class);
            TestModuleC moduleC = appContext.getInstance(TestModuleC.class);
            TestModuleD moduleD = appContext.getInstance(TestModuleD.class);// Hasor 支持直接创建一个类型，即便它没有被注册
            //
            assert moduleA.getApplicationContext() == context;
            assert moduleB.getApplicationContext() == context;
            assert moduleC.getApplicationContext() == context;
            assert moduleD.getApplicationContext() == null;//由于是 通过 Hasor 创建的，因此Spring的 Autowired 并未生效
        }
    }

    @Test
    public void multipleSpringModule() {
        TestModuleA.reset();
        TestModuleB.reset();
        TestModuleC.reset();
        TestModuleD.reset();
        //
        String settingFile = "net_hasor_spring/spring-multiple-module.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile);) {
            AppContext appContext1 = (AppContext) context.getBean("hasor_1");
            AppContext appContext2 = (AppContext) context.getBean("hasor_2");
            //
            assert TestModuleA.isInit();
            assert TestModuleB.isInit();
            assert TestModuleC.isInit();
            assert TestModuleD.isInit();
            //
            {
                assert appContext1.getBindInfo(TestModuleA.class) != null;
                assert appContext1.getBindInfo(TestModuleB.class) != null;
                assert appContext1.getBindInfo(TestModuleC.class) != null;
                assert appContext1.getBindInfo(TestModuleD.class) == null;
                //
                TestModuleA moduleA = appContext1.getInstance(TestModuleA.class);
                TestModuleB moduleB = appContext1.getInstance(TestModuleB.class);
                TestModuleC moduleC = appContext1.getInstance(TestModuleC.class);
                TestModuleD moduleD = appContext1.getInstance(TestModuleD.class);// Hasor 支持直接创建一个类型，即便它没有被注册
                //
                assert moduleA.getApplicationContext() == context;
                assert moduleB.getApplicationContext() == context;
                assert moduleC.getApplicationContext() == context;
                assert moduleD.getApplicationContext() == null;//由于是 通过 Hasor 创建的，因此Spring的 Autowired 并未生效
            }
            //
            {
                assert appContext2.getBindInfo(TestModuleA.class) == null;
                assert appContext2.getBindInfo(TestModuleB.class) == null;
                assert appContext2.getBindInfo(TestModuleC.class) == null;
                assert appContext2.getBindInfo(TestModuleD.class) != null;
                //
                TestModuleA moduleA = appContext2.getInstance(TestModuleA.class);
                TestModuleB moduleB = appContext2.getInstance(TestModuleB.class);
                TestModuleC moduleC = appContext2.getInstance(TestModuleC.class);
                TestModuleD moduleD = appContext2.getInstance(TestModuleD.class);
                //
                assert moduleA.getApplicationContext() == null;//由于是 通过 Hasor 创建的，因此Spring的 Autowired 并未生效
                assert moduleB.getApplicationContext() == null;//由于是 通过 Hasor 创建的，因此Spring的 Autowired 并未生效
                assert moduleC.getApplicationContext() == null;//由于是 通过 Hasor 创建的，因此Spring的 Autowired 并未生效
                assert moduleD.getApplicationContext() == context;
            }
        }
    }

    @Test
    public void scanSpringModule() {
        String settingFile = "net_hasor_spring/spring-scan-module.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile)) {
            AppContext appContext = context.getBean(AppContext.class);
            assert appContext.getBindInfo(TestModuleA.class) == null;
            assert appContext.getBindInfo(TestModuleB.class) == null;
            assert appContext.getBindInfo(TestModuleC.class) == null;
            assert appContext.getBindInfo(TestModuleD.class) == null;
            assert appContext.getBindInfo(TestDimModuleA.class) != null;
            assert appContext.getBindInfo(TestDimModuleB.class) != null;
            //
            TestDimModuleA moduleA = appContext.getInstance(TestDimModuleA.class);
            TestDimModuleB moduleB = appContext.getInstance(TestDimModuleB.class);
            // TestDimModuleA 并没有在 Spring 中加载，因此采用最基础的 new 过程创建
            assert moduleA.getApplicationContext() == null;
            // TestDimModuleB 是通过 Spring 自动扫描加载的，因此会通过Spring 创建
            assert moduleB.getApplicationContext() == context;
        }
    }

    @Test
    public void envSpringModule() {
        String settingFile = "net_hasor_spring/spring-load-properties.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile)) {
            AppContext appContext = context.getBean(AppContext.class);
            Environment environment = appContext.getInstance(Environment.class);
            Settings settings = appContext.getInstance(Settings.class);
            //
            assert environment.getVariable("msg_a").equalsIgnoreCase("Message1");
            assert environment.getVariable("msg_b").equalsIgnoreCase("ccc");
            // 通过 util:properties 加载然后同步给 Hasor
            assert environment.getVariable("env2").equalsIgnoreCase("Message2");
            //
            // 由于没有使用 useProperties=true，因此属性文件只存在 Hasor 环境变量中。并未导入到 Setting
            assert settings.getString("msg_a") == null;
            assert settings.getString("msg_b") == null;
            assert settings.getString("env2") == null;
            //
            // context:property-placeholder 是不会被加载的。
            assert StringUtils.isBlank(environment.getVariable("env1"));
            assert StringUtils.isBlank(settings.getString("env1"));
        }
    }

    @Test
    public void settingSpringModule() {
        String settingFile = "net_hasor_spring/spring-load-settings.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile)) {
            AppContext appContext = context.getBean(AppContext.class);
            Environment environment = appContext.getInstance(Environment.class);
            Settings settings = appContext.getInstance(Settings.class);
            //
            assert environment.getVariable("msg_a").equalsIgnoreCase("Message1");
            assert environment.getVariable("msg_b").equalsIgnoreCase("ccc");
            // 通过 util:properties 加载然后同步给 Hasor
            assert environment.getVariable("env2").equalsIgnoreCase("Message2");
            //
            // 使用了 useProperties=true，属性文件被导入到 Setting
            assert settings.getString("msg_a").equals("Message1");
            assert settings.getString("msg_b").equals("ccc");
            assert settings.getString("env2").equals("Message2");
            //
            // context:property-placeholder 是不会被加载的
            assert StringUtils.isBlank(environment.getVariable("env1"));
            assert StringUtils.isBlank(settings.getString("env1"));
        }
    }

    @Test
    public void hconfigSpringModule() {
        String settingFile = "net_hasor_spring/spring-load-hconfig.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile)) {
            AppContext appContext = context.getBean(AppContext.class);
            Settings settings = appContext.getInstance(Settings.class);
            assert settings.getString("msg_hallo").equals("Hello Word");
        }
    }

    @Test
    public void hasorSpringBean() {
        String settingFile = "net_hasor_spring/spring-bean.xml";
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(settingFile)) {
            Object bean1 = context.getBean("hasorBean1");
            Object bean2 = context.getBean("hasorBean2");
            Object bean3 = context.getBean("springBean");
            //
            assert bean1.equals("Hello Word");
            assert bean2 instanceof HasorBean;
            assert ((HasorBean) bean2).getMsgValue1().equals("msg_1");
            assert ((HasorBean) bean2).getMsgValue2().equals("msg_2");
            assert bean3 instanceof SpringBean;
            assert ((SpringBean) bean3).getHasorBean() == bean2;
        }
    }
}
