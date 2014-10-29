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
package net.hasor.db.ar;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
/**
 * 用来表示数据库s
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
    //
    /**根据 SqlType 返回 Java类型.*/
    public static Class<?> sqlTypeToJavaType(int sqlType) {
        return sqlTypeToJavaTypeMap.get(sqlType);
    }
}