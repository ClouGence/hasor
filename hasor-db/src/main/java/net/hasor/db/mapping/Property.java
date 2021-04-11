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
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.UnknownTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在字段上表示映射到的列
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    /** 列名，为空的话表示采用字段名为列名 see: {@link #name()} */
    public String value() default "";

    /** 列名，为空的话表示采用类名为表名 see: {@link #value()} */
    public String name() default "";

    /** 使用的 typeHandler 功效和 Mybatis 的 TypeHandler 相同 */
    public Class<? extends TypeHandler<?>> typeHandler() default UnknownTypeHandler.class;

    /** 参与更新 */
    public boolean update() default true;

    /** 参与新增 */
    public boolean insert() default true;

    /**
     * 是否使用限定符(默认不使用)，通常无需配置 hasor-db 会自动识别。
     * 如遇到如下两个情况，hasor-db 可能强制启用标识符限定(相当设置为 true)：
     *  - 1.表名是关键字(强制启动)
     *  - 2。 autoFiled 配置为 true 的情况下，根据元信息匹配如遇到名称无法匹配，例如 Oracle 的名称默认都是大写。
     */
    public boolean useQualifier() default false;
}
