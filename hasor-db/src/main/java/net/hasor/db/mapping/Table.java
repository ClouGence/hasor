/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.mapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在类型上表示映射到的表
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /** 分类(对于mysql 是 db，对于 pg 是 schema，对于 oracle 是 owner) */
    public String schema() default "";

    /** 表名，为空的话表示采用类名为表名 see: {@link #name()} */
    public String value() default "";

    /** 表名，为空的话表示采用类名为表名 see: {@link #value()} */
    public String name() default "";

    /** 通过注解配置的表名获取数据库的元信息，自动匹配字段和属性字段的之间的映射关系。从而无需成员字段标记 @Field 注解。 */
    public boolean autoFiled() default true;

    /**
     * 是否使用限定符(默认不使用)，通常无需配置 hasor-db 会自动识别。
     * 如遇到如下两个情况，hasor-db 可能强制启用标识符限定(相当设置为 true)：
     *  - 1.表名是关键字(强制启动)
     *  - 2。 autoFiled 配置为 true 的情况下，根据元信息匹配如遇到名称无法匹配，例如 Oracle 的名称默认都是大写。
     */
    public boolean useQualifier() default false;
}
