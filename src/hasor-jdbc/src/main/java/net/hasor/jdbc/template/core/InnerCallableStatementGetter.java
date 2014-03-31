package net.hasor.jdbc.template.core;
import java.sql.CallableStatement;
import java.sql.SQLException;
import org.more.util.ClassUtils;
/**
 * 
 * @version : 2014-3-31
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
class InnerCallableStatementGetter {
    public static Object getValue(CallableStatement cs, int index, Class<?> requiredType) throws SQLException {
        Object value = null;
        boolean wasNullCheck = false;
        if (requiredType == null) {
            return cs.getObject(index);
        }
        requiredType = ClassUtils.primitiveToWrapper(requiredType);
        // Explicitly extract typed value, as far as possible.
        if (String.class.equals(requiredType)) {
            value = cs.getString(index);
        } else if (Integer.class.equals(requiredType)) {
            value = Integer.valueOf(cs.getInt(index));
            wasNullCheck = true;
        } else if (Double.class.equals(requiredType)) {
            value = new Double(cs.getDouble(index));
            wasNullCheck = true;
        } else if (Boolean.class.equals(requiredType)) {
            value = (cs.getBoolean(index) ? Boolean.TRUE : Boolean.FALSE);
            wasNullCheck = true;
        } else if (java.sql.Date.class.equals(requiredType)) {
            value = cs.getDate(index);
        } else if (java.sql.Time.class.equals(requiredType)) {
            value = cs.getTime(index);
        } else if (java.sql.Timestamp.class.equals(requiredType)) {
            value = cs.getTimestamp(index);
        } else if (java.util.Date.class.equals(requiredType)) {
            value = new java.util.Date(cs.getTimestamp(index).getTime());
        } else if (Byte.class.equals(requiredType)) {
            value = Byte.valueOf(cs.getByte(index));
            wasNullCheck = true;
        } else if (Short.class.equals(requiredType)) {
            value = Short.valueOf(cs.getShort(index));
            wasNullCheck = true;
        } else if (Long.class.equals(requiredType)) {
            value = Long.valueOf(cs.getLong(index));
            wasNullCheck = true;
        } else if (Float.class.equals(requiredType)) {
            value = new Float(cs.getFloat(index));
            wasNullCheck = true;
        } else if (Number.class.equals(requiredType)) {
            value = new Double(cs.getDouble(index));
            wasNullCheck = true;
        } else if (byte[].class.equals(requiredType)) {
            value = cs.getBytes(index);
        } else if (java.math.BigDecimal.class.equals(requiredType)) {
            value = cs.getBigDecimal(index);
        } else if (java.sql.Blob.class.equals(requiredType)) {
            value = cs.getBlob(index);
        } else if (java.sql.Clob.class.equals(requiredType)) {
            value = cs.getClob(index);
        } else if (java.net.URL.class.equals(requiredType)) {
            value = cs.getURL(index);
        } else {
            // Some unknown type desired -> rely on getObject.
            value = cs.getObject(index);
        }
        // Perform was-null check if demanded (for results that the
        // JDBC driver returns as primitives).
        if (wasNullCheck && value != null && cs.wasNull()) {
            value = null;
        }
        return value;
    }
}
