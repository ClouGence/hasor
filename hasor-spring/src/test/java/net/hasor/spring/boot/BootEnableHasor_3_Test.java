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
package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.test.spring.mod1.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = BootEnableHasor_3.class)
public class BootEnableHasor_3_Test {
    @Autowired
    private AppContext         appContext;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
        Set<Class<?>> hasType = new HashSet<>();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            hasType.add(applicationContext.getType(name));
        }
        //
        //
        assert appContext.getBindInfo(TestModuleA.class) == null;
        assert !hasType.contains(TestModuleA.class);
        //
        //
        assert appContext.getBindInfo(TestModuleB.class) == null;
        assert !hasType.contains(TestModuleB.class);
        //
        //
        assert appContext.getBindInfo(TestModuleC.class) == null;
        assert !hasType.contains(TestModuleC.class);
        //
        //
        assert appContext.getBindInfo(TestModuleD.class) == null;
        assert !hasType.contains(TestModuleD.class);
        //
        //
        // 有DimModule、在ComponentScan范围外、在EnableHasor范围内、无Component
        assert appContext.getBindInfo(TestDimModuleA.class) != null; // Hasor 加载了
        assert !hasType.contains(TestDimModuleA.class);// 无Component，Spring 中不存在它。
        TestDimModuleA dimModuleA = appContext.getInstance(TestDimModuleA.class);
        assert dimModuleA.getApplicationContext() == null;
        //
        //
        // 有DimModule、在ComponentScan范围外、在EnableHasor范围内、有Component
        assert appContext.getBindInfo(TestDimModuleB.class) != null; // Hasor 加载了
        assert hasType.contains(TestDimModuleB.class);// 虽然 没有标注ComponentScan范围，但是启动的主类和它在一个包里面， Spring 仍然会处理 Component。
        TestDimModuleB dimModuleB_1 = appContext.getInstance(TestDimModuleB.class);
        TestDimModuleB dimModuleB_2 = applicationContext.getBean(TestDimModuleB.class);
        assert dimModuleB_1.getApplicationContext() == applicationContext;// Hasor 当成普通 Bean 创建
        assert dimModuleB_2.getApplicationContext() == applicationContext;// Spring 会创建它
        //
        //
        // 无DimModule、在ComponentScan范围外、在EnableHasor范围内、有Component
        assert appContext.getBindInfo(TestDimModuleC.class) == null; // 不是一个有效的 Module
        assert hasType.contains(TestDimModuleC.class);// 虽然 没有标注ComponentScan范围，但是启动的主类和它在一个包里面， Spring 仍然会处理 Component。
        TestDimModuleC dimModuleC_1 = appContext.getInstance(TestDimModuleC.class);
        TestDimModuleC dimModuleC_2 = applicationContext.getBean(TestDimModuleC.class);
        assert dimModuleC_1.getApplicationContext() == null;// Hasor 当成普通 Bean 创建
        assert dimModuleC_2.getApplicationContext() == applicationContext;// Spring 会创建它
    }
}
