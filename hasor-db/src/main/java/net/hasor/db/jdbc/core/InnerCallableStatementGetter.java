package net.hasor.db.jdbc.core;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * @version : 2014-3-31
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerCallableStatementGetter {
    /** Maps primitive <code>Class</code>es to their corresponding wrapper <code>Class</code>. */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
    /** Maps wrapper <code>Class</code>es to their corresponding primitive types. */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
        //
        for (Iterator<?> it = primitiveWrapperMap.keySet().iterator(); it.hasNext(); ) {
            Class<?> primitiveClass = (Class<?>) it.next();
            Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }
    //
    public static Object getValue(final CallableStatement cs, final int index, Class<?> requiredType) throws SQLException {
        Object value = null;
        boolean wasNullCheck = false;
        if (requiredType == null) {
            return cs.getObject(index);
        }
        requiredType = primitiveToWrapper(requiredType);
        // Explicitly extract typed value, as far as possible.
        if (String.class.equals(requiredType)) {
            value = cs.getString(index);
        } else if (Integer.class.equals(requiredType)) {
            value = cs.getInt(index);
            wasNullCheck = true;
        } else if (Double.class.equals(requiredType)) {
            value = cs.getDouble(index);
            wasNullCheck = true;
        } else if (Boolean.class.equals(requiredType)) {
            value = cs.getBoolean(index) ? Boolean.TRUE : Boolean.FALSE;
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
            value = cs.getByte(index);
            wasNullCheck = true;
        } else if (Short.class.equals(requiredType)) {
            value = cs.getShort(index);
            wasNullCheck = true;
        } else if (Long.class.equals(requiredType)) {
            value = cs.getLong(index);
            wasNullCheck = true;
        } else if (Float.class.equals(requiredType)) {
            value = cs.getFloat(index);
            wasNullCheck = true;
        } else if (Number.class.equals(requiredType)) {
            value = cs.getDouble(index);
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
        // Perform was-null check if demanded (for results that the JDBC driver returns as primitives).
        if (wasNullCheck && value != null && cs.wasNull()) {
            value = null;
        }
        return value;
    }
}
