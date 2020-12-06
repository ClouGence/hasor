/*
 * Copyright 2002-2007 the original author or authors.
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
import java.util.Objects;

/**
 * Object to represent an SQL parameter definition.
 *
 * <p>Parameters may be anonymous, in which case "name" is {@code null}.
 * However, all parameters must define an SQL type according to {@link java.sql.Types}.
 *
 * @author Rod Johnson
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @see java.sql.Types
 */
public class SqlParameter {
    // SQL type constant from {@code java.sql.Types}
    private final int     sqlType;
    // Used for types that are user-named like: STRUCT, DISTINCT, JAVA_OBJECT, named array types
    private       String  typeName;
    // The scale to apply in case of a NUMERIC or DECIMAL type, if any
    private       Integer scale;

    /**
     * Create a new anonymous SqlParameter, supplying the SQL type.
     * @param sqlType the SQL type of the parameter according to {@code java.sql.Types}
     */
    public SqlParameter(int sqlType) {
        this.sqlType = sqlType;
    }

    /**
     * Create a new anonymous SqlParameter, supplying the SQL type.
     * @param sqlType the SQL type of the parameter according to {@code java.sql.Types}
     * @param typeName the type name of the parameter (optional)
     */
    public SqlParameter(int sqlType, String typeName) {
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    /**
     * Create a new anonymous SqlParameter, supplying the SQL type.
     * @param sqlType the SQL type of the parameter according to {@code java.sql.Types}
     * @param scale the number of digits after the decimal point
     * (for DECIMAL and NUMERIC types)
     */
    public SqlParameter(int sqlType, int scale) {
        this.sqlType = sqlType;
        this.scale = scale;
    }

    /**
     * Copy constructor.
     * @param otherParam the SqlParameter object to copy from
     */
    public SqlParameter(SqlParameter otherParam) {
        Objects.requireNonNull(otherParam, "SqlParameter object must not be null");
        this.sqlType = otherParam.sqlType;
        this.typeName = otherParam.typeName;
        this.scale = otherParam.scale;
    }

    /** Return the SQL type of the parameter. */
    public int getSqlType() {
        return this.sqlType;
    }

    /** Return the type name of the parameter, if any. */
    public String getTypeName() {
        return this.typeName;
    }

    /** Return the scale of the parameter, if any. */
    public Integer getScale() {
        return this.scale;
    }

    /**
     * Return whether this parameter holds input values that should be set
     * before execution even if they are {@code null}.
     * <p>This implementation always returns {@code true}.
     */
    public boolean isInputParameter() {
        return true;
    }

    /**
     * Return whether this parameter is an implicit return parameter used during the
     * results processing of {@code CallableStatement.getMoreResults/getUpdateCount}.
     * <p>This implementation always returns {@code false}.
     */
    public boolean isOutputParameter() {
        return false;
    }
}
