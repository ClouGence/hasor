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
import net.hasor.utils.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class SqlXmlForInputStreamTypeHandler extends AbstractTypeHandler<InputStream> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, InputStream parameter, JDBCType jdbcType) throws SQLException {
        SQLXML sqlxml = ps.getConnection().createSQLXML();
        try {
            OutputStream writer = sqlxml.setBinaryStream();
            IOUtils.copy(parameter, writer);
            ps.setSQLXML(i, sqlxml);
        } catch (IOException e) {
            throw new SQLException("Error copy xml data to SQLXML for parameter #" + i + " with JdbcType " + jdbcType + ", Cause: " + e.getMessage(), e);
        } finally {
            sqlxml.free();
        }
    }

    @Override
    public InputStream getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return sqlXmlToStream(rs.getSQLXML(columnName));
    }

    @Override
    public InputStream getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return sqlXmlToStream(rs.getSQLXML(columnIndex));
    }

    @Override
    public InputStream getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return sqlXmlToStream(cs.getSQLXML(columnIndex));
    }

    protected InputStream sqlXmlToStream(SQLXML sqlxml) throws SQLException {
        if (sqlxml == null) {
            return null;
        }
        try {
            return sqlxml.getBinaryStream();
        } finally {
            sqlxml.free();
        }
    }
}