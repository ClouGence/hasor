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
package net.hasor.plugins.valid.api;
import net.hasor.plugins.valid.custom.CharacterSetValidation;
import java.lang.annotation.*;
/**
 * 字符集校验
 * @version : 2016-7-20
 * @author 赵永春 (zyc@hasor.net)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@CustomValid(CharacterSetValidation.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CharacterSet {
    /**参数名称。*/
    public String value();
}