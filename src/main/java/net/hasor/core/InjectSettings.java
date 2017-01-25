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
import java.lang.annotation.*;
/**
 * 依赖注入，注入settings配置数据。请注意{@link InjectMembers}接口方式与注解方式互斥，且接口方式优先于注解方式。
 * @version : 2016年07月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface InjectSettings {
    /**配置Key*/
    public String value();

    /**默认值*/
    public String defaultValue() default "";
}