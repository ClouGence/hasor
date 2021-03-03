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
import net.hasor.utils.NumberUtils;
import net.hasor.utils.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;

/**
 * @version : 2020-11-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class NumberTypeHandler extends AbstractTypeHandler<Number> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Number parameter, JDBCType jdbcType) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Number getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getResultSetValue(new NumberResultValue() {
            @Override
            public String getColumnClassName() throws SQLException {
                return getObject().getClass().getName();
            }

            @Override
            public Timestamp getTimestamp() throws SQLException {
                return rs.getTimestamp(columnName);
            }

            @Override
            public Date getDate() throws SQLException {
                return rs.getDate(columnName);
            }

            @Override
            public Object getObject() throws SQLException {
                return rs.getObject(columnName);
            }
        });
    }

    @Override
    public Number getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getResultSetValue(new NumberResultValue() {
            @Override
            public String getColumnClassName() throws SQLException {
                return rs.getMetaData().getColumnClassName(columnIndex);
            }

            @Override
            public Timestamp getTimestamp() throws SQLException {
                return rs.getTimestamp(columnIndex);
            }

            @Override
            public Date getDate() throws SQLException {
                return rs.getDate(columnIndex);
            }

            @Override
            public Object getObject() throws SQLException {
                return rs.getObject(columnIndex);
            }
        });
    }

    @Override
    public Number getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getResultSetValue(new NumberResultValue() {
            @Override
            public String getColumnClassName() throws SQLException {
                return cs.getMetaData().getColumnClassName(columnIndex);
            }

            @Override
            public Timestamp getTimestamp() throws SQLException {
                return cs.getTimestamp(columnIndex);
            }

            @Override
            public Date getDate() throws SQLException {
                return cs.getDate(columnIndex);
            }

            @Override
            public Object getObject() throws SQLException {
                return cs.getObject(columnIndex);
            }
        });
    }

    protected static interface NumberResultValue {
        public String getColumnClassName() throws SQLException;

        public java.sql.Timestamp getTimestamp() throws SQLException;

        public java.sql.Date getDate() throws SQLException;

        public Object getObject() throws SQLException;
    }

    /**获取列的值*/
    protected Number getResultSetValue(NumberResultValue rs) throws SQLException {
        Object obj = rs.getObject();
        if (obj == null) {
            return null;
        }
        //
        String className = obj.getClass().getName();
        if (obj instanceof Number) {
            return (Number) obj;
        } else if (obj instanceof java.sql.Timestamp) {
            return ((Timestamp) obj).getTime();
        } else if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).getTime();
        } else if (obj instanceof java.sql.Time) {
            return ((java.sql.Time) obj).getTime();
        } else if (("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))) {
            /*oracle TIMESTAMP 转换为 Timestamp*/
            return rs.getTimestamp().getTime();
        } else if (className.startsWith("oracle.sql.DATE")) {
            /*oracle DATE 转换为 Date*/
            String metaDataClassName = rs.getColumnClassName();
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                return rs.getTimestamp().getTime();
            } else {
                return rs.getDate().getTime();
            }
        } else if (obj instanceof java.util.Date) {
            return ((java.util.Date) obj).getTime();
        }
        //
        String stringValue = null;
        if (obj instanceof String) {
            stringValue = (String) obj;
        } else if (obj instanceof Clob) {
            /*Clob 转换为 String*/
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(((Clob) obj).getCharacterStream(), writer);
                stringValue = writer.toString();
            } catch (IOException e) {
                throw new SQLException(e);
            }
        } else {
            stringValue = obj.toString();
        }
        stringValue = stringValue.trim();
        //
        if (!NumberUtils.isNumber(stringValue)) {
            throw new SQLException("Cannot convert String to Number.");
        }
        return NumberUtils.createNumber(stringValue);
    }
}
