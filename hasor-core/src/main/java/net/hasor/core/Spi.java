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
package net.hasor.core;
import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * 在一个实现类上标记该注解，用来表示实现了哪些 SPI
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Spi {
    /** 指出实现了哪些 SPI，如果不设置值。那么会自动抽取实现的接口并将符合 SPI 规范的接口注册为 Hasor SPI。*/
    public Class<?>[] value() default {};
}