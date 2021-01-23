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
package net.hasor.db.types.mapping;
import java.sql.JDBCType;

/**
 * 字段信息
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface FieldInfo {
    /** 列名 */
    public String getColumnName();

    /** 使用的 jdbcType,如果没有配置那么会通过 javaType 来自动推断 */
    public JDBCType getJdbcType();

    /** 对应的 javaType */
    public Class<?> getJavaType();
}