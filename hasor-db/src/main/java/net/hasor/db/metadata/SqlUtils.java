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
package net.hasor.db.metadata;
import net.hasor.db.metadata.mysql.MySqlTypes;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;

import java.util.Collection;
import java.util.Date;

/**
 * 元信息获取，辅助工具
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
class SqlUtils {
    protected static String safeToString(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    protected static Integer safeToInteger(Object obj) {
        return (obj == null) ? null : (Integer) ConverterUtils.convert(Integer.class, obj);
    }

    protected static Long safeToLong(Object obj) {
        return (obj == null) ? null : (Long) ConverterUtils.convert(Long.class, obj);
    }

    protected static Boolean safeToBoolean(Object obj) {
        return (obj == null) ? null : (Boolean) ConverterUtils.convert(Boolean.class, obj);
    }

    protected static Date safeToDate(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof Number) {
            return new Date(((Number) obj).longValue());
        } else {
            throw new ClassCastException(obj.getClass() + " Type cannot be converted to Date");
        }
    }

    protected static String buildWhereIn(Collection<?> paramMap) {
        StringBuilder whereIn = new StringBuilder();
        whereIn.append("(");
        whereIn.append(StringUtils.repeat("?,", paramMap.size()));
        whereIn.deleteCharAt(whereIn.length() - 1);
        whereIn.append(")");
        return whereIn.toString();
    }

    protected static MySqlTypes safeToMySqlTypes(Object obj) {
        String dat = (obj == null) ? null : obj.toString();
        for (MySqlTypes type : MySqlTypes.values()) {
            if (type.getCodeKey().equalsIgnoreCase(dat)) {
                return type;
            }
        }
        return null;
    }
}
