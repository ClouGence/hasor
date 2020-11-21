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
package net.hasor.db.jdbc.types.handler;
import net.hasor.utils.NumberUtils;
import net.hasor.utils.StringUtils;

import java.sql.*;
import java.time.Month;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MonthOfStringTypeHandler extends AbstractTypeHandler<Month> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Month month, JDBCType jdbcType) throws SQLException {
        ps.setString(i, month.name().toUpperCase());
    }

    @Override
    public Month getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String month = rs.getString(columnName);
        if (StringUtils.isBlank(month)) {
            return null;
        }
        month = month.trim().toUpperCase();
        return NumberUtils.isNumber(month) ? Month.of(Integer.parseInt(month)) : Month.valueOf(month);
    }

    @Override
    public Month getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String month = rs.getString(columnIndex);
        if (StringUtils.isBlank(month)) {
            return null;
        }
        month = month.trim().toUpperCase();
        return NumberUtils.isNumber(month) ? Month.of(Integer.parseInt(month)) : Month.valueOf(month);
    }

    @Override
    public Month getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String month = cs.getString(columnIndex);
        if (StringUtils.isBlank(month)) {
            return null;
        }
        month = month.trim().toUpperCase();
        return NumberUtils.isNumber(month) ? Month.of(Integer.parseInt(month)) : Month.valueOf(month);
    }
}