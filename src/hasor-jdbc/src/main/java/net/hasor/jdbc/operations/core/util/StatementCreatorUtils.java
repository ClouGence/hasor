/*
 * Copyright 2002-2009 the original author or authors.
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
package net.hasor.jdbc.operations.core.util;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import net.hasor.jdbc.operations.core.parameter.SqlParameter;
import net.hasor.jdbc.operations.core.value.DisposableSqlTypeValue;
import net.hasor.jdbc.operations.core.value.SqlTypeValue;
import net.hasor.jdbc.operations.core.value.SqlValue;
/**
 * Utility methods for PreparedStatementSetter/Creator and CallableStatementCreator
 * implementations, providing sophisticated parameter management (including support
 * for LOB values).
 *
 * <p>Used by PreparedStatementCreatorFactory and CallableStatementCreatorFactory,
 * but also available for direct use in custom setter/creator implementations.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 1.1
 * @see SqlParameter
 * @see SqlTypeValue
 * @see org.noe.lib.jdbcorm.jdbc.core.support.SqlLobValue
 */
public abstract class StatementCreatorUtils {
    /**
     * Set the value for a parameter. The method used is based on the SQL type
     * of the parameter and we can handle complex types like arrays and LOBs.
     * @param ps the prepared statement or callable statement
     * @param paramIndex index of the parameter we are setting
     * @param sqlType the SQL type of the parameter
     * @param inValue the value to set (plain value or a SqlTypeValue)
     * @throws SQLException if thrown by PreparedStatement methods
     * @see SqlTypeValue
     */
    public static void setParameterValue(PreparedStatement ps, int paramIndex, int sqlType, Object inValue) throws SQLException {
        setParameterValueInternal(ps, paramIndex, sqlType, null, null, inValue);
    }
    /**
     * Set the value for a parameter. The method used is based on the SQL type
     * of the parameter and we can handle complex types like arrays and LOBs.
     * @param ps the prepared statement or callable statement
     * @param paramIndex index of the parameter we are setting
     * @param sqlType the SQL type of the parameter
     * @param typeName the type name of the parameter
     * (optional, only used for SQL NULL and SqlTypeValue)
     * @param inValue the value to set (plain value or a SqlTypeValue)
     * @throws SQLException if thrown by PreparedStatement methods
     * @see SqlTypeValue
     */
    public static void setParameterValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName, Object inValue) throws SQLException {
        setParameterValueInternal(ps, paramIndex, sqlType, typeName, null, inValue);
    }
    /**
     * Set the value for a parameter. The method used is based on the SQL type
     * of the parameter and we can handle complex types like arrays and LOBs.
     * @param ps the prepared statement or callable statement
     * @param paramIndex index of the parameter we are setting
     * @param sqlType the SQL type of the parameter
     * @param typeName the type name of the parameter
     * (optional, only used for SQL NULL and SqlTypeValue)
     * @param scale the number of digits after the decimal point
     * (for DECIMAL and NUMERIC types)
     * @param inValue the value to set (plain value or a SqlTypeValue)
     * @throws SQLException if thrown by PreparedStatement methods
     * @see SqlTypeValue
     */
    private static void setParameterValueInternal(PreparedStatement ps, int paramIndex, int sqlType, String typeName, Integer scale, Object inValue) throws SQLException {
        String typeNameToUse = typeName;
        int sqlTypeToUse = sqlType;
        Object inValueToUse = inValue;
        // override type info?
        if (inValue instanceof SqlParameterValue) {
            SqlParameterValue parameterValue = (SqlParameterValue) inValue;
            if (logger.isDebugEnabled()) {
                logger.debug("Overriding typeinfo with runtime info from SqlParameterValue: column index " + paramIndex + ", SQL type " + parameterValue.getSqlType() + ", Type name " + parameterValue.getTypeName());
            }
            if (parameterValue.getSqlType() != SqlTypeValue.TYPE_UNKNOWN) {
                sqlTypeToUse = parameterValue.getSqlType();
            }
            if (parameterValue.getTypeName() != null) {
                typeNameToUse = parameterValue.getTypeName();
            }
            inValueToUse = parameterValue.getValue();
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Setting SQL statement parameter value: column index " + paramIndex + ", parameter value [" + inValueToUse + "], value class [" + (inValueToUse != null ? inValueToUse.getClass().getName() : "null") + "], SQL type " + (sqlTypeToUse == SqlTypeValue.TYPE_UNKNOWN ? "unknown" : Integer.toString(sqlTypeToUse)));
        }
        if (inValueToUse == null) {
            setNull(ps, paramIndex, sqlTypeToUse, typeNameToUse);
        } else {
            setValue(ps, paramIndex, sqlTypeToUse, typeNameToUse, scale, inValueToUse);
        }
    }
    /**
     * Set the specified PreparedStatement parameter to null,
     * respecting database-specific peculiarities.
     */
    private static void setNull(PreparedStatement ps, int paramIndex, int sqlType, String typeName) throws SQLException {
        if (sqlType == SqlTypeValue.TYPE_UNKNOWN) {
            boolean useSetObject = false;
            sqlType = Types.NULL;
            try {
                DatabaseMetaData dbmd = ps.getConnection().getMetaData();
                String databaseProductName = dbmd.getDatabaseProductName();
                String jdbcDriverName = dbmd.getDriverName();
                if (databaseProductName.startsWith("Informix") || jdbcDriverName.startsWith("Microsoft SQL Server")) {
                    useSetObject = true;
                } else if (databaseProductName.startsWith("DB2") || jdbcDriverName.startsWith("jConnect") || jdbcDriverName.startsWith("SQLServer") || jdbcDriverName.startsWith("Apache Derby")) {
                    sqlType = Types.VARCHAR;
                }
            } catch (Throwable ex) {
                logger.debug("Could not check database or driver name", ex);
            }
            if (useSetObject) {
                ps.setObject(paramIndex, null);
            } else {
                ps.setNull(paramIndex, sqlType);
            }
        } else if (typeName != null) {
            ps.setNull(paramIndex, sqlType, typeName);
        } else {
            ps.setNull(paramIndex, sqlType);
        }
    }
    /**
     * Clean up all resources held by parameter values which were passed to an
     * execute method. This is for example important for closing LOB values.
     * @param paramValues parameter values supplied. May be <code>null</code>.
     * @see DisposableSqlTypeValue#cleanup()
     * @see org.noe.lib.jdbcorm.jdbc.core.support.SqlLobValue#cleanup()
     */
    public static void cleanupParameters(Object[] paramValues) {
        if (paramValues != null) {
            cleanupParameters(Arrays.asList(paramValues));
        }
    }
    /**
     * Clean up all resources held by parameter values which were passed to an
     * execute method. This is for example important for closing LOB values.
     * @param paramValues parameter values supplied. May be <code>null</code>.
     * @see DisposableSqlTypeValue#cleanup()
     * @see org.noe.lib.jdbcorm.jdbc.core.support.SqlLobValue#cleanup()
     */
    public static void cleanupParameters(Collection paramValues) {
        if (paramValues != null) {
            for (Object inValue : paramValues) {
                if (inValue instanceof DisposableSqlTypeValue) {
                    ((DisposableSqlTypeValue) inValue).cleanup();
                } else if (inValue instanceof SqlValue) {
                    ((SqlValue) inValue).cleanup();
                }
            }
        }
    }
}