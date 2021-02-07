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
package net.hasor.db.jdbc;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.StringUtils;

import java.sql.JDBCType;
import java.util.Objects;

/**
 * Object to represent an SQL parameter definition.
 *
 * <p>Parameters may be anonymous, in which case "name" is {@code null}.
 * However, all parameters must define an SQL type according to {@link JDBCType}.
 *
 * @author 赵永春 (zyc@hasor.net)
 * @see JDBCType
 */
public class SqlParameterUtils implements SqlParameter {
    // The name of the parameter, if any
    private final String name;

    protected SqlParameterUtils(String name) {
        this.name = name;
    }

    /** Return the name of the parameter, or {@code null} if anonymous. */
    public String getName() {
        return this.name;
    }

    private static abstract class ValueSqlParameterUtilsImpl extends SqlParameterUtils implements ValueSqlParameter {
        // SQL type constant from {@code java.sql.JDBCType}
        private final JDBCType jdbcType;
        // Used for types that are user-named like: STRUCT, DISTINCT, JAVA_OBJECT, named array types
        private final String   typeName;
        // The scale to apply in case of a NUMERIC or DECIMAL type, if any
        private final Integer  scale;

        public ValueSqlParameterUtilsImpl(String name, JDBCType jdbcType, String typeName, Integer scale) {
            super(name);
            this.jdbcType = Objects.requireNonNull(jdbcType, "jdbcType can not be null.");
            this.typeName = typeName;
            this.scale = scale;
        }

        /** Return the SQL type of the parameter. */
        public JDBCType getJdbcType() {
            return this.jdbcType;
        }

        /** Return the type name of the parameter, if any. */
        public String getTypeName() {
            return this.typeName;
        }

        /** Return the scale of the parameter, if any. */
        public Integer getScale() {
            return this.scale;
        }
    }

    private static final class InSqlParameterUtilsImpl extends ValueSqlParameterUtilsImpl implements InSqlParameter {
        private final Object         value;
        private final TypeHandler<?> typeHandler;

        public InSqlParameterUtilsImpl(String name, JDBCType jdbcType, String typeName, Integer scale, TypeHandler<?> typeHandler, Object value) {
            super(name, jdbcType, typeName, scale);
            this.typeHandler = typeHandler;
            this.value = value;
        }

        public TypeHandler<?> getTypeHandler() {
            return this.typeHandler;
        }

        public Object getValue() {
            return this.value;
        }
    }

    private static final class OutSqlParameterUtilsImpl extends ValueSqlParameterUtilsImpl implements OutSqlParameter {
        private final TypeHandler<?> typeHandler;

        public OutSqlParameterUtilsImpl(String name, JDBCType jdbcType, String typeName, Integer scale, TypeHandler<?> typeHandler) {
            super(name, jdbcType, typeName, scale);
            this.typeHandler = typeHandler;
        }

        public TypeHandler<?> getTypeHandler() {
            return this.typeHandler;
        }
    }

    private static final class InOutSqlParameterUtilsImpl extends ValueSqlParameterUtilsImpl implements InSqlParameter, OutSqlParameter {
        private final Object         value;
        private final TypeHandler<?> typeHandler;

        public InOutSqlParameterUtilsImpl(String name, JDBCType jdbcType, String typeName, Integer scale, TypeHandler<?> typeHandler, Object value) {
            super(name, jdbcType, typeName, scale);
            this.typeHandler = typeHandler;
            this.value = value;
        }

        public TypeHandler<?> getTypeHandler() {
            return this.typeHandler;
        }

        @Override
        public Object getValue() {
            return this.value;
        }
    }

    private static final class ReturnSqlParameterUtilsImpl extends SqlParameterUtils implements ReturnSqlParameter {
        private final ResultSetExtractor<?> resultSetExtractor;
        private final RowCallbackHandler    rowCallbackHandler;
        private final RowMapper<?>          rowMapper;

        public ReturnSqlParameterUtilsImpl(String name, ResultSetExtractor<?> resultSetExtractor, RowCallbackHandler rowCallbackHandler, RowMapper<?> rowMapper) {
            super(name);
            this.resultSetExtractor = resultSetExtractor;
            this.rowCallbackHandler = rowCallbackHandler;
            this.rowMapper = rowMapper;
        }

        /** Return the ResultSetExtractor held by this parameter, if any. */
        public ResultSetExtractor<?> getResultSetExtractor() {
            return this.resultSetExtractor;
        }

        /** Return the RowCallbackHandler held by this parameter, if any. */
        public RowCallbackHandler getRowCallbackHandler() {
            return this.rowCallbackHandler;
        }

        /** Return the RowMapper held by this parameter, if any. */
        public RowMapper<?> getRowMapper() {
            return this.rowMapper;
        }
    }

    public static OutSqlParameter withOutput(JDBCType jdbcType) {
        return new OutSqlParameterUtilsImpl(null, jdbcType, null, null, null);
    }

    public static OutSqlParameter withOutput(JDBCType jdbcType, Integer scale) {
        return new OutSqlParameterUtilsImpl(null, jdbcType, null, scale, null);
    }

    public static OutSqlParameter withOutput(JDBCType jdbcType, String typeName) {
        return new OutSqlParameterUtilsImpl(null, jdbcType, typeName, null, null);
    }

    public static OutSqlParameter withOutput(JDBCType jdbcType, TypeHandler<?> typeHandler) {
        return new OutSqlParameterUtilsImpl(null, jdbcType, null, null, typeHandler);
    }

    public static OutSqlParameter withOutput(JDBCType jdbcType, Integer scale, TypeHandler<?> typeHandler) {
        return new OutSqlParameterUtilsImpl(null, jdbcType, null, scale, typeHandler);
    }

    public static OutSqlParameter withOutput(JDBCType jdbcType, String typeName, TypeHandler<?> typeHandler) {
        return new OutSqlParameterUtilsImpl(null, jdbcType, typeName, null, typeHandler);
    }

    public static OutSqlParameter withOutput(String paramName, JDBCType jdbcType) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new OutSqlParameterUtilsImpl(paramName, jdbcType, null, null, null);
    }

    public static OutSqlParameter withOutput(String paramName, JDBCType jdbcType, Integer scale) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new OutSqlParameterUtilsImpl(paramName, jdbcType, null, scale, null);
    }

    public static OutSqlParameter withOutput(String paramName, JDBCType jdbcType, String typeName) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new OutSqlParameterUtilsImpl(paramName, jdbcType, typeName, null, null);
    }

    public static OutSqlParameter withOutput(String paramName, JDBCType jdbcType, TypeHandler<?> typeHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new OutSqlParameterUtilsImpl(paramName, jdbcType, null, null, typeHandler);
    }

    public static OutSqlParameter withOutput(String paramName, JDBCType jdbcType, Integer scale, TypeHandler<?> typeHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new OutSqlParameterUtilsImpl(paramName, jdbcType, null, scale, typeHandler);
    }

    public static OutSqlParameter withOutput(String paramName, JDBCType jdbcType, String typeName, TypeHandler<?> typeHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new OutSqlParameterUtilsImpl(paramName, jdbcType, typeName, null, typeHandler);
    }

    public static InSqlParameter withInput(Object value) {
        JDBCType jdbcType = TypeHandlerRegistry.toSqlType(Objects.requireNonNull(value, "only value, can not be null.").getClass());
        return new InSqlParameterUtilsImpl(null, jdbcType, null, null, null, value);
    }

    public static InSqlParameter withInput(Object value, JDBCType jdbcType) {
        return new InSqlParameterUtilsImpl(null, jdbcType, null, null, null, value);
    }

    public static InSqlParameter withInput(Object value, JDBCType jdbcType, TypeHandler<?> typeHandler) {
        return new InSqlParameterUtilsImpl(null, jdbcType, null, null, typeHandler, value);
    }

    public static SqlParameter withInOut(Object value, JDBCType jdbcType) {
        return new InOutSqlParameterUtilsImpl(null, jdbcType, null, null, null, value);
    }

    public static SqlParameter withInOut(Object value, JDBCType jdbcType, Integer scale) {
        return new InOutSqlParameterUtilsImpl(null, jdbcType, null, scale, null, value);
    }

    public static SqlParameter withInOut(Object value, JDBCType jdbcType, String typeName) {
        return new InOutSqlParameterUtilsImpl(null, jdbcType, typeName, null, null, value);
    }

    public static SqlParameter withInOut(Object value, JDBCType jdbcType, TypeHandler<?> typeHandler) {
        return new InOutSqlParameterUtilsImpl(null, jdbcType, null, null, typeHandler, value);
    }

    public static SqlParameter withInOut(Object value, JDBCType jdbcType, Integer scale, TypeHandler<?> typeHandler) {
        return new InOutSqlParameterUtilsImpl(null, jdbcType, null, scale, typeHandler, value);
    }

    public static SqlParameter withInOut(Object value, JDBCType jdbcType, String typeName, TypeHandler<?> typeHandler) {
        return new InOutSqlParameterUtilsImpl(null, jdbcType, typeName, null, typeHandler, value);
    }

    public static SqlParameter withInOut(String paramName, Object value, JDBCType jdbcType) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new InOutSqlParameterUtilsImpl(paramName, jdbcType, null, null, null, value);
    }

    public static SqlParameter withInOut(String paramName, Object value, JDBCType jdbcType, Integer scale) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new InOutSqlParameterUtilsImpl(paramName, jdbcType, null, scale, null, value);
    }

    public static SqlParameter withInOut(String paramName, Object value, JDBCType jdbcType, String typeName) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new InOutSqlParameterUtilsImpl(paramName, jdbcType, typeName, null, null, value);
    }

    public static SqlParameter withInOut(String paramName, Object value, JDBCType jdbcType, TypeHandler<?> typeHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new InOutSqlParameterUtilsImpl(paramName, jdbcType, null, null, typeHandler, value);
    }

    public static SqlParameter withInOut(String paramName, Object value, JDBCType jdbcType, Integer scale, TypeHandler<?> typeHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new InOutSqlParameterUtilsImpl(paramName, jdbcType, null, scale, typeHandler, value);
    }

    public static SqlParameter withInOut(String paramName, Object value, JDBCType jdbcType, String typeName, TypeHandler<?> typeHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new InOutSqlParameterUtilsImpl(paramName, jdbcType, typeName, null, typeHandler, value);
    }

    public static ReturnSqlParameter withReturnValue(String paramName) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new ReturnSqlParameterUtilsImpl(paramName, null, null, null);
    }

    public static ReturnSqlParameter withReturnResult(String paramName, ResultSetExtractor<?> resultSetExtractor) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new ReturnSqlParameterUtilsImpl(paramName, resultSetExtractor, null, null);
    }

    public static ReturnSqlParameter withReturnResult(String paramName, RowCallbackHandler rowCallbackHandler) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new ReturnSqlParameterUtilsImpl(paramName, null, rowCallbackHandler, null);
    }

    public static ReturnSqlParameter withReturnResult(String paramName, RowMapper<?> rowMapper) {
        if (StringUtils.isBlank(paramName)) {
            throw new IllegalStateException("paramName can not be empty or null.");
        }
        return new ReturnSqlParameterUtilsImpl(paramName, null, null, rowMapper);
    }
}
