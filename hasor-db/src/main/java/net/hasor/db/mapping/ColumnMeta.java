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
import java.sql.JDBCType;

/**
 * （可选）标记在字段上可以用于脱离元信息依赖作为元信息补充。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnMeta {
    /** 指定使用的 jdbcType */
    public JDBCType jdbcType() default JDBCType.OTHER;

    /** 是否为主键 */
    public boolean primary() default false;

    /** 唯一键 key */
    public String uniqueKey() default "";

    /** 外键 key */
    public String foreignKey() default "";
}
