package net.hasor.jdbc.template.core;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @version : 2014-3-29
 * @author 赵永春 (zyc@byshell.org)
 */
class InnerStatementSetterUtils {
    public static final int               TYPE_UNKNOWN         = Integer.MIN_VALUE;
    private static Map<Class<?>, Integer> javaTypeToSqlTypeMap = new HashMap<Class<?>, Integer>(32);
    static {
        /* JDBC 3.0 only - not compatible with e.g. MySQL at present*/
        javaTypeToSqlTypeMap.put(boolean.class, new Integer(Types.BOOLEAN));
        javaTypeToSqlTypeMap.put(Boolean.class, new Integer(Types.BOOLEAN));
        //
        javaTypeToSqlTypeMap.put(byte.class, Types.TINYINT);
        javaTypeToSqlTypeMap.put(Byte.class, Types.TINYINT);
        javaTypeToSqlTypeMap.put(short.class, Types.SMALLINT);
        javaTypeToSqlTypeMap.put(Short.class, Types.SMALLINT);
        javaTypeToSqlTypeMap.put(int.class, Types.INTEGER);
        javaTypeToSqlTypeMap.put(Integer.class, Types.INTEGER);
        javaTypeToSqlTypeMap.put(long.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(Long.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(BigInteger.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(float.class, Types.FLOAT);
        javaTypeToSqlTypeMap.put(Float.class, Types.FLOAT);
        javaTypeToSqlTypeMap.put(double.class, Types.DOUBLE);
        javaTypeToSqlTypeMap.put(Double.class, Types.DOUBLE);
        javaTypeToSqlTypeMap.put(BigDecimal.class, Types.DECIMAL);
        javaTypeToSqlTypeMap.put(java.sql.Date.class, Types.DATE);
        javaTypeToSqlTypeMap.put(java.sql.Time.class, Types.TIME);
        javaTypeToSqlTypeMap.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        javaTypeToSqlTypeMap.put(Blob.class, Types.BLOB);
        javaTypeToSqlTypeMap.put(Clob.class, Types.CLOB);
    }
    //
    /**根据 Java 类型Derive a default SQL type from the given Java type.*/
    public static int javaTypeToSqlParameterType(Class<?> javaType) {
        Integer sqlType = javaTypeToSqlTypeMap.get(javaType);
        if (sqlType != null)
            return sqlType;
        if (Number.class.isAssignableFrom(javaType))
            return Types.NUMERIC;
        if (isStringValue(javaType))
            return Types.VARCHAR;
        if (isDateValue(javaType) || Calendar.class.isAssignableFrom(javaType))
            return Types.TIMESTAMP;
        return TYPE_UNKNOWN;
    }
    /***/
    public static void setParameterValue(PreparedStatement ps, int parameterPosition, Object inValue) throws SQLException {
        if (inValue == null)
            ps.setObject(parameterPosition, null);
        else if (inValue instanceof SqlValue) {
            ((SqlValue) inValue).setValue(ps, parameterPosition);
        } else
            setValue(ps, parameterPosition, inValue);
    }
    private static void setValue(PreparedStatement ps, int paramIndex, Object inValue) throws SQLException {
        int sqlType = javaTypeToSqlParameterType(inValue.getClass());
        if (sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR || (sqlType == Types.CLOB && isStringValue(inValue.getClass()))) {
            //字符
            ps.setString(paramIndex, inValue.toString());
        } else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
            //数字
            if (inValue instanceof BigDecimal)
                ps.setBigDecimal(paramIndex, (BigDecimal) inValue);
            else
                ps.setObject(paramIndex, inValue, sqlType);
        } else if (sqlType == Types.DATE) {
            //日期
            if (inValue instanceof java.util.Date) {
                /*时间*/
                if (inValue instanceof java.sql.Date)
                    ps.setDate(paramIndex, (java.sql.Date) inValue);
                else
                    ps.setDate(paramIndex, new java.sql.Date(((java.util.Date) inValue).getTime()));
            } else if (inValue instanceof Calendar) {
                /*日历*/
                Calendar cal = (Calendar) inValue;
                ps.setDate(paramIndex, new java.sql.Date(cal.getTime().getTime()), cal);
            } else {
                /*其他*/
                ps.setObject(paramIndex, inValue, Types.DATE);
            }
        } else if (sqlType == Types.TIME) {
            //时间
            if (inValue instanceof java.util.Date) {
                /*SQL时间*/
                if (inValue instanceof java.sql.Time)
                    ps.setTime(paramIndex, (java.sql.Time) inValue);
                else
                    ps.setTime(paramIndex, new java.sql.Time(((java.util.Date) inValue).getTime()));
            } else if (inValue instanceof Calendar) {
                /*日历*/
                Calendar cal = (Calendar) inValue;
                ps.setTime(paramIndex, new java.sql.Time(cal.getTime().getTime()), cal);
            } else {
                /*其他*/
                ps.setObject(paramIndex, inValue, Types.TIME);
            }
        } else if (sqlType == Types.TIMESTAMP) {
            //日期时间
            if (inValue instanceof java.util.Date) {
                /*SQL时间戳*/
                if (inValue instanceof java.sql.Timestamp)
                    ps.setTimestamp(paramIndex, (java.sql.Timestamp) inValue);
                else
                    ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
            } else if (inValue instanceof Calendar) {
                /*日历*/
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
            } else {
                /*其他*/
                ps.setObject(paramIndex, inValue, Types.TIMESTAMP);
            }
        } else if (sqlType == TYPE_UNKNOWN) {
            //不确定类型
            if (isStringValue(inValue.getClass()))
                ps.setString(paramIndex, inValue.toString());
            else if (isDateValue(inValue.getClass()))
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
            else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
            } else
                ps.setObject(paramIndex, inValue);//通用的参数设置方法
        } else {
            //确定类型
            ps.setObject(paramIndex, inValue, sqlType);//通用的参数设置方法
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
        if (paramValues != null)
            cleanupParameters(Arrays.asList(paramValues));
    }
    /**
     * Clean up all resources held by parameter values which were passed to an
     * execute method. This is for example important for closing LOB values.
     * @param paramValues parameter values supplied. May be <code>null</code>.
     * @see DisposableSqlTypeValue#cleanup()
     * @see org.noe.lib.jdbcorm.jdbc.core.support.SqlLobValue#cleanup()
     */
    public static void cleanupParameters(Collection<Object> paramValues) {
        if (paramValues != null) {
            for (Object inValue : paramValues) {
                if (inValue instanceof SqlValue)
                    ((SqlValue) inValue).cleanup();
            }
        }
    }
    /**Check whether the given value can be treated as a String value.*/
    private static boolean isStringValue(Class<?> inValueType) {
        // Consider any CharSequence (including StringBuffer and StringBuilder) as a String.
        return (CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class.isAssignableFrom(inValueType));
    }
    /**Check whether the given value is a <code>java.util.Date</code>(but not one of the JDBC-specific subclasses).*/
    private static boolean isDateValue(Class<?> inValueType) {
        return (java.util.Date.class.isAssignableFrom(inValueType) && !(java.sql.Date.class.isAssignableFrom(inValueType) || java.sql.Time.class.isAssignableFrom(inValueType) || java.sql.Timestamp.class.isAssignableFrom(inValueType)));
    }
}