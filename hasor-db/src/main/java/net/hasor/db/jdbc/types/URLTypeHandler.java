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
package net.hasor.db.jdbc.types;
import java.net.URL;
import java.sql.*;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class URLTypeHandler extends AbstractTypeHandler<URL> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, URL parameter, JDBCType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public URL getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName, URL.class);
    }

    @Override
    public URL getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex, URL.class);
    }

    @Override
    public URL getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getObject(columnIndex, URL.class);
    }
}