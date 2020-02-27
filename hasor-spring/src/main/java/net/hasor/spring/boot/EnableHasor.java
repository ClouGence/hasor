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

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = BasicHasorConfiguration.class)
public @interface EnableHasor {
    /** 是否自动扫描 @DimModule 注解的Module 并加载它们。 */
    public boolean autoScan() default false;

    /** 确定扫描范围，默认为空将会按照 Hasor 的默认扫描范围来扫描。这有可能会超出 Spring 的范围。 */
    public String[] autoScanPackages() default {};

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
