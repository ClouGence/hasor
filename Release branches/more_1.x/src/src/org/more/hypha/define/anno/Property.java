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
package org.more.hypha.define.anno;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 标记当前字段是一个需要注入的属性，如果属性没有指定任何属性值描述则“菌丝”不会理会这个属性的注入要求。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Property {
    /**表明该属性是否延迟注入，如果延迟注入当只有试图访问该属性时才会对属性进行初始化。*/
    public boolean lazyInit() default true;
    /**属性的注释*/
    public String desc() default "";
    /**文本形式的属性值描述，通常这种类型的描述可以表示常用的基本类型。复杂的描述注入注入一个bean则需要使用el注入。*/
    public String value() default "";
    /**对el进行解析然后将解析结果注入到该字段上。*/
    public String el() default "";
    /**携带的附加信息描述*/
    public MetaData[] metaData() default {};
}