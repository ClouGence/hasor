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
package net.hasor.db.types;
import net.hasor.db.types.handler.AbstractTypeHandler;
import net.hasor.db.types.handler.ObjectTypeHandler;
import net.hasor.utils.ResourcesUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Clinton Begin
 */
public class UnknownTypeHandler extends AbstractTypeHandler<Object> {
    private static final ObjectTypeHandler   OBJECT_TYPE_HANDLER = new ObjectTypeHandler();
    private final        TypeHandlerRegistry typeHandlerRegistry;

    public UnknownTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
        this.typeHandlerRegistry = typeHandlerRegistry;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JDBCType jdbcType) throws SQLException {
        TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
        handler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        TypeHandler<?> handler = resolveTypeHandler(rs, columnName);
        return tryFixHandler(handler).getResult(rs, columnName);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        TypeHandler<?> handler = resolveTypeHandler(rs.getMetaData(), columnIndex);
        return tryFixHandler(handler).getResult(rs, columnIndex);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        TypeHandler<?> handler = resolveTypeHandler(cs.getMetaData(), columnIndex);
        return tryFixHandler(handler).getResult(cs, columnIndex);
    }

    private static TypeHandler<?> tryFixHandler(TypeHandler<?> handler) {
        if (handler == null || handler instanceof UnknownTypeHandler) {
            handler = OBJECT_TYPE_HANDLER;
        }
        return handler;
    }

    private TypeHandler<?> resolveTypeHandler(Object parameter, JDBCType jdbcType) {
        TypeHandler<?> handler = null;
        if (parameter == null) {
            handler = OBJECT_TYPE_HANDLER;
        } else {
            handler = this.typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
            // check if handler is null (issue #270) <- mybatis
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = OBJECT_TYPE_HANDLER;
            }
        }
        return handler;
    }

    private TypeHandler<?> resolveTypeHandler(ResultSet rs, String column) {
        try {
            Map<String, Integer> columnIndexLookup = new HashMap<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String name = rsmd.getColumnName(i);
                columnIndexLookup.put(name, i);
            }
            Integer columnIndex = columnIndexLookup.get(column);
            TypeHandler<?> handler = null;
            if (columnIndex != null) {
                handler = resolveTypeHandler(rsmd, columnIndex);
            }
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = OBJECT_TYPE_HANDLER;
            }
            return handler;
        } catch (SQLException e) {
            throw new RuntimeException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
        }
    }

    private TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) {
        TypeHandler<?> handler = null;
        JDBCType jdbcType = safeGetJdbcTypeForColumn(rsmd, columnIndex);
        Class<?> javaType = safeGetClassForColumn(rsmd, columnIndex);
        if (javaType != null && jdbcType != null) {
            handler = this.typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
        } else if (javaType != null) {
            handler = this.typeHandlerRegistry.getTypeHandler(javaType);
        } else if (jdbcType != null) {
            handler = this.typeHandlerRegistry.getTypeHandler(jdbcType);
        }
        return handler;
    }

    private static JDBCType safeGetJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
        try {
            return JDBCType.valueOf(rsmd.getColumnType(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }

    private static Class<?> safeGetClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
        try {
            return ResourcesUtils.classForName(rsmd.getColumnClassName(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }
}
