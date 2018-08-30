/**********************************************************\
 |                                                          |
 |                          hprose                          |
 |                                                          |
 | Official WebSite: http://www.hprose.com/                 |
 |                   http://www.hprose.org/                 |
 |                                                          |
 \**********************************************************/
/**********************************************************\
 *                                                        *
 * ConstructorAccessor.java                               *
 *                                                        *
 * ConstructorAccessor class for Java.                    *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.access;
import net.hasor.rsf.libs.com.hprose.io.HproseException;

import java.io.ObjectStreamClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
public class ConstructorAccessor {
    private final static ConcurrentHashMap<Class<?>, Constructor<?>> ctorCache  = new ConcurrentHashMap<Class<?>, Constructor<?>>();
    private final static ConcurrentHashMap<Constructor<?>, Object[]> argsCache  = new ConcurrentHashMap<Constructor<?>, Object[]>();
    private final static Long                                        longZero   = (long) 0;
    private final static Object[]                                    nullArgs   = new Object[0];
    private final static Short                                       shortZero  = (short) 0;
    private final static Double                                      doubleZero = (double) 0;
    private final static Byte                                        byteZero   = (byte) 0;
    private final static Integer                                     intZero    = 0;
    private final static Float                                       floatZero  = (float) 0;
    private final static Character                                   charZero   = (char) 0;
    private final static Constructor<Object> nullCtor;
    private final static Method              newInstance;

    static {
        Constructor<Object> _nullCtor;
        try {
            _nullCtor = Object.class.getConstructor((Class<?>[]) null);
        } catch (Exception e) {
            _nullCtor = null;
        }
        assert (_nullCtor != null);
        nullCtor = _nullCtor;
        Method _newInstance;
        try {
            _newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[0]);
            _newInstance.setAccessible(true);
        } catch (Exception e) {
            _newInstance = null;
        }
        assert (_newInstance != null);
        newInstance = _newInstance;
    }
    private static class ConstructorComparator implements Comparator<Constructor<?>> {
        public int compare(Constructor<?> o1, Constructor<?> o2) {
            return o1.getParameterTypes().length - o2.getParameterTypes().length;
        }
    }
    private static Object[] getArgs(Constructor<?> ctor) {
        Object[] args = argsCache.get(ctor);
        if (args == null) {
            Class<?>[] params = ctor.getParameterTypes();
            args = new Object[params.length];
            for (int i = 0; i < params.length; ++i) {
                Class<?> type = params[i];
                if (int.class.equals(type) || Integer.class.equals(type)) {
                    args[i] = intZero;
                } else if (long.class.equals(type) || Long.class.equals(type)) {
                    args[i] = longZero;
                } else if (byte.class.equals(type) || Byte.class.equals(type)) {
                    args[i] = byteZero;
                } else if (short.class.equals(type) || Short.class.equals(type)) {
                    args[i] = shortZero;
                } else if (float.class.equals(type) || Float.class.equals(type)) {
                    args[i] = floatZero;
                } else if (double.class.equals(type) || Double.class.equals(type)) {
                    args[i] = doubleZero;
                } else if (char.class.equals(type) || Character.class.equals(type)) {
                    args[i] = charZero;
                } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
                    args[i] = Boolean.FALSE;
                } else {
                    args[i] = null;
                }
            }
            argsCache.put(ctor, args);
        }
        return args;
    }
    @SuppressWarnings({ "unchecked" })
    public final static <T> T newInstance(Class<T> type) throws HproseException {
        Constructor<?> ctor = ctorCache.get(type);
        if (ctor == null) {
            Constructor<T>[] ctors = (Constructor<T>[]) type.getDeclaredConstructors();
            Arrays.sort(ctors, new ConstructorComparator());
            for (Constructor<T> c : ctors) {
                try {
                    c.setAccessible(true);
                    T obj = c.newInstance(getArgs(c));
                    ctorCache.put(type, c);
                    return obj;
                } catch (Exception e) {
                }
            }
            ctor = nullCtor;
            ctorCache.put(type, ctor);
        }
        try {
            if (ctor == nullCtor) {
                return (T) newInstance.invoke(ObjectStreamClass.lookup(type), nullArgs);
            }
            return (T) ctor.newInstance(getArgs(ctor));
        } catch (Exception ex) {
            throw new HproseException(ex.getMessage());
        }
    }
}
