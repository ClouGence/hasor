/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.orm.ar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
/**
 * 用来表示数据库
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
final class InnerArUtils {
    private static Map<Integer, Class<?>> sqlTypeToJavaTypeMap = new HashMap<Integer, Class<?>>(32);
    static {
        /* JDBC 3.0 only - not compatible with e.g. MySQL at present*/
        sqlTypeToJavaTypeMap.put(Types.BOOLEAN, Boolean.class);
        sqlTypeToJavaTypeMap.put(Types.TINYINT, Byte.class);
        sqlTypeToJavaTypeMap.put(Types.SMALLINT, Short.class);
        sqlTypeToJavaTypeMap.put(Types.INTEGER, Integer.class);
        sqlTypeToJavaTypeMap.put(Types.BIGINT, Long.class);//BigInteger
        sqlTypeToJavaTypeMap.put(Types.FLOAT, Float.class);
        sqlTypeToJavaTypeMap.put(Types.DOUBLE, Double.class);
        sqlTypeToJavaTypeMap.put(Types.DECIMAL, BigDecimal.class);
        sqlTypeToJavaTypeMap.put(Types.DATE, java.sql.Date.class);
        sqlTypeToJavaTypeMap.put(Types.TIME, java.sql.Time.class);
        sqlTypeToJavaTypeMap.put(Types.TIMESTAMP, java.sql.Timestamp.class);
        sqlTypeToJavaTypeMap.put(Types.BLOB, Blob.class);
        sqlTypeToJavaTypeMap.put(Types.CLOB, Clob.class);
    }
    /**根据 SqlType 返回 Java类型.*/
    public static Class<?> sqlTypeToJavaType(int sqlType) {
        return sqlTypeToJavaTypeMap.get(sqlType);
    }
    //
    //
    //
    private static Map<Class<?>, Integer> javaTypeToSqlTypeMap = new HashMap<Class<?>, Integer>(32);
    static {
        /* JDBC 3.0 only - not compatible with e.g. MySQL at present*/
        javaTypeToSqlTypeMap.put(Boolean.class, Types.BOOLEAN);
        javaTypeToSqlTypeMap.put(Boolean.TYPE, Types.BOOLEAN);
        javaTypeToSqlTypeMap.put(Byte.class, Types.TINYINT);
        javaTypeToSqlTypeMap.put(Byte.TYPE, Types.TINYINT);
        javaTypeToSqlTypeMap.put(Short.class, Types.SMALLINT);
        javaTypeToSqlTypeMap.put(Short.TYPE, Types.SMALLINT);
        javaTypeToSqlTypeMap.put(Integer.class, Types.INTEGER);
        javaTypeToSqlTypeMap.put(Integer.TYPE, Types.INTEGER);
        javaTypeToSqlTypeMap.put(BigInteger.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(Long.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(Long.TYPE, Types.BIGINT);
        javaTypeToSqlTypeMap.put(Float.class, Types.FLOAT);
        javaTypeToSqlTypeMap.put(Float.TYPE, Types.FLOAT);
        javaTypeToSqlTypeMap.put(Double.class, Types.DOUBLE);
        javaTypeToSqlTypeMap.put(Double.TYPE, Types.DOUBLE);
        javaTypeToSqlTypeMap.put(BigDecimal.class, Types.DECIMAL);
        javaTypeToSqlTypeMap.put(java.sql.Date.class, Types.DATE);
        javaTypeToSqlTypeMap.put(java.sql.Time.class, Types.TIME);
        javaTypeToSqlTypeMap.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        javaTypeToSqlTypeMap.put(java.util.Date.class, Types.TIMESTAMP);
        javaTypeToSqlTypeMap.put(String.class, Types.VARCHAR);
    }
    /**根据 SqlType 返回 Java类型.*/
    public static int javaTypeToSqlType(Class<?> sqlType) {
        if (sqlType == null || sqlType == Void.class) {
            return Types.NULL;
        }
        Integer toSqlType = javaTypeToSqlTypeMap.get(sqlType);
        return toSqlType == null ? Types.JAVA_OBJECT : toSqlType;
    }
}