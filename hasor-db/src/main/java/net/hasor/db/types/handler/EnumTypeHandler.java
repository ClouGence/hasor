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
package net.hasor.db.types.handler;
import net.hasor.db.types.EnumOfCode;
import net.hasor.db.types.EnumOfValue;

import java.sql.*;

/**
 * @version : 2020-11-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class EnumTypeHandler<E extends Enum<E>> extends AbstractTypeHandler<E> {
    private final Class<E>       type;
    private final EnumOfCode<E>  ofCode;
    private final EnumOfValue<E> ofValue;

    public EnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        Enum<E>[] enums = ((Class<Enum<E>>) type).getEnumConstants();
        if (enums.length > 0 && EnumOfCode.class.isAssignableFrom(type)) {
            this.ofCode = (EnumOfCode<E>) enums[0];
        } else {
            this.ofCode = null;
        }
        if (enums.length > 0 && EnumOfValue.class.isAssignableFrom(type)) {
            this.ofValue = (EnumOfValue<E>) enums[0];
        } else {
            this.ofValue = null;
        }
        if (this.ofCode != null && this.ofValue != null) {
            throw new IllegalArgumentException(type.getName() + " Type EnumOfCode and EnumOfValue cannot exist at the same time.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JDBCType jdbcType) throws SQLException {
        if (parameter instanceof EnumOfCode && this.ofCode != null) {
            ps.setString(i, ((EnumOfCode<?>) parameter).codeName());
            return;
        }
        if (parameter instanceof EnumOfValue && this.ofValue != null) {
            ps.setInt(i, ((EnumOfValue<?>) parameter).codeValue());
            return;
        }
        //
        if (jdbcType == null) {
            ps.setString(i, parameter.name());
        } else {
            ps.setObject(i, parameter.name(), jdbcType); // see r3589
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (this.ofCode != null) {
            String s = rs.getString(columnName);
            return s == null ? null : valueByOfCode(s);
        }
        if (this.ofValue != null) {
            int s = rs.getInt(columnName);
            return rs.wasNull() ? null : valueByOfValue(s);
        }
        //
        String s = rs.getString(columnName);
        return s == null ? null : valueOf(s);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (this.ofCode != null) {
            String s = rs.getString(columnIndex);
            return s == null ? null : valueByOfCode(s);
        }
        if (this.ofValue != null) {
            int s = rs.getInt(columnIndex);
            return rs.wasNull() ? null : valueByOfValue(s);
        }
        //
        String s = rs.getString(columnIndex);
        return s == null ? null : valueOf(s);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (this.ofCode != null) {
            String s = cs.getString(columnIndex);
            return s == null ? null : valueByOfCode(s);
        }
        if (this.ofValue != null) {
            int s = cs.getInt(columnIndex);
            return cs.wasNull() ? null : valueByOfValue(s);
        }
        //
        String s = cs.getString(columnIndex);
        return s == null ? null : valueOf(s);
    }

    private E valueByOfValue(int s) {
        return this.ofValue.valueOfCode(s);
    }

    private E valueByOfCode(String s) {
        return this.ofCode.valueOfCode(s);
    }

    private E valueOf(String enumDat) {
        if (this.ofCode != null) {
            return this.ofCode.valueOfCode(enumDat);
        } else {
            return Enum.valueOf(type, enumDat);
        }
    }
}