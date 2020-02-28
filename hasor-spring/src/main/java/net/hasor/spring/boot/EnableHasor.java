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
package net.hasor.spring.boot;
import net.hasor.core.Module;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Hasor
 * 关于 ComponentScan，首先 spring 的 ComponentScan 会扫描所有 Spring 的Bean。在此基础上如果这些 Bean 标记了 @DimModule 注解并且实现了 Module 接口。那么它们会被作为 Hasor 的初始化 Module。
 * 如果 scanPackages 配置的扫描范围超出了 ComponentScan，那么这些标记了 @DimModule 的 Module 接口实现类将会以 new 的形式进行创建。
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = BasicHasorConfiguration.class)
public @interface EnableHasor {
    /** 扫描范围，如果 scanPackages 配置的扫描范围超出了 ComponentScan，那么这些标记了 @DimModule 的 Module 接口实现类将会以 new 的形式进行创建。*/
    public String[] scanPackages() default {};

    /** Hasor 的主配置文件，可以是 Xml 或者 属性文件 */
    public String mainConfig() default "";

    /** 是否将 Hasor 环境变量用作 Settings
     * （Hasor 会自动将Spring 的属性文件导入到环境变量中若想要进一步在 Settings 中使用 Spring 的属性文件就要设置为 true） */
    public boolean useProperties() default false;

    /** 启动入口 */
    public Class<? extends Module>[] startWith() default {};

    /** Hasor 使用的特殊属性，这些属性会设置到 Hasor 的环境变量上 */
    public Property[] customProperties() default {};
}
