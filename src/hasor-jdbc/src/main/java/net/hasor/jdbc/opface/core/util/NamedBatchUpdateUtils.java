package net.hasor.jdbc.opface.core.util;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import net.hasor.jdbc.opface.BatchPreparedStatementSetter;
import net.hasor.jdbc.opface.JdbcOperations;
import net.hasor.jdbc.opface.SqlParameterSource;
import net.hasor.jdbc.opface.core.value.SqlTypeValue;
import net.hasor.jdbc.opface.parameter.SqlVarParameter;
/**
 * Generic utility methods for working with JDBC batch statements using named parameters. Mainly for internal use
 * within the framework.
 *
 * @author Thomas Risberg
 */
public class NamedBatchUpdateUtils {
    public static int[] executeBatchUpdate(String sql, final List<Object[]> batchValues, final int[] columnTypes, JdbcOperations jdbcOperations) {
        return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] values = batchValues.get(i);
                setStatementParameters(values, ps, columnTypes);
            }
            public int getBatchSize() {
                return batchValues.size();
            }
        });
    }
    protected static void setStatementParameters(Object[] values, PreparedStatement ps, int[] columnTypes) throws SQLException {
        int colIndex = 0;
        for (Object value : values) {
            colIndex++;
            if (value instanceof SqlVarParameter) {
                SqlVarParameter paramValue = (SqlVarParameter) value;
                StatementSetterUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
            } else {
                int colType;
                if (columnTypes == null || columnTypes.length < colIndex) {
                    colType = SqlTypeValue.TYPE_UNKNOWN;
                } else {
                    colType = columnTypes[colIndex - 1];
                }
                StatementSetterUtils.setParameterValue(ps, colIndex, colType, value);
            }
        }
    }
    public static int[] executeBatchUpdateWithNamedParameters(final ParsedSql parsedSql, final SqlParameterSource[] batchArgs, JdbcOperations jdbcOperations) {
        if (batchArgs.length <= 0) {
            return new int[] { 0 };
        }
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, batchArgs[0]);
        return jdbcOperations.batchUpdate(sqlToUse, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] values = NamedParameterUtils.buildValueArray(parsedSql, batchArgs[i], null);
                int[] columnTypes = NamedParameterUtils.buildSqlTypeArray(parsedSql, batchArgs[i]);
                setStatementParameters(values, ps, columnTypes);
            }
            public int getBatchSize() {
                return batchArgs.length;
            }
        });
    }
}
