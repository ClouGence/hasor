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

@SpringBootTest(classes = BootEnableHasor_2.class)
public class BootEnableHasor_2_Test {
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
        // 有DimModule、在ComponentScan范围内、在EnableHasor范围外、无Component
        assert appContext.getBindInfo(TestDimModuleA.class) == null; // 范围外不加载
        assert !hasType.contains(TestDimModuleA.class);// 无Component，Spring 中不存在它。
        //
        //
        // 有DimModule、在ComponentScan范围内、在EnableHasor范围外、有Component
        assert appContext.getBindInfo(TestDimModuleB.class) != null; // 虽然 Hasor 扫描范围外，但是Hasor 会加载 Spring Bean 中所有 DimModule 的 Module
        assert hasType.contains(TestDimModuleB.class);
        TestDimModuleB dimModuleB = appContext.getInstance(TestDimModuleB.class);
        assert dimModuleB.getApplicationContext() == applicationContext;
        //
        //
        // 无DimModule、在ComponentScan范围内、在EnableHasor范围外、有Component
        assert appContext.getBindInfo(TestDimModuleC.class) == null; // 不是一个有效的 Module
        assert hasType.contains(TestDimModuleC.class);// 是Spring Bean
        TestDimModuleC dimModuleC_1 = appContext.getInstance(TestDimModuleC.class);
        TestDimModuleC dimModuleC_2 = applicationContext.getBean(TestDimModuleC.class);
        assert dimModuleC_1.getApplicationContext() == null;// Hasor 当成普通 Bean 创建
        assert dimModuleC_2.getApplicationContext() == applicationContext;// Spring 会创建它
    }
}
