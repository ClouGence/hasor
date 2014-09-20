/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.client;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.rsf.metadata.ServiceMetaData;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
/**
 * Wrapper
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RemoteTicket {
    private static AtomicLong                        WRAPPER_CLASS_COUNTER = new AtomicLong(0);
    private static final Map<Class<?>, RemoteTicket> WRAPPER_MAP           = new ConcurrentHashMap<Class<?>, RemoteTicket>();              // class
    // wrapper map
    private static final String[]                    EMPTY_STRING_ARRAY    = new String[0];
    private static final String[]                    OBJECT_METHODS        = new String[] { "getClass", "hashCode", "toString", "equals" };
    /** get wrapper. */
    public static RemoteTicket getWrapper(Class<?> c) {
        ClassLoader appClassLoader = c.getClassLoader();
        ProtectionDomain domain = c.getProtectionDomain();
        while (ClassGenerator.isDynamicClass(c))
            // can not wrapper on dynamic class.
            c = c.getSuperclass();
        if (c == Object.class)
            return OBJECT_WRAPPER;
        RemoteTicket ret = WRAPPER_MAP.get(c);
        if (ret == null) {
            ret = makeWrapper(c, appClassLoader, domain);
            WRAPPER_MAP.put(c, ret);
        }
        return ret;
    }
    /** get property name array. */
    public abstract String[] getPropertyNames();
    /** get property type.*/
    public abstract Class<?> getPropertyType(String pn);
    /** has property.*/
    public abstract boolean hasProperty(String name);
    /** get property value. */
    public abstract Object getPropertyValue(Object instance, String pn) throws NoSuchPropertyException, IllegalArgumentException;
    /** set property value. */
    public abstract void setPropertyValue(Object instance, String pn, Object pv) throws NoSuchPropertyException, IllegalArgumentException;
    /** get property value. */
    public Object[] getPropertyValues(Object instance, String[] pns) throws NoSuchPropertyException, IllegalArgumentException {
        Object[] ret = new Object[pns.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = getPropertyValue(instance, pns[i]);
        return ret;
    }
    /** set property value. */
    public void setPropertyValues(Object instance, String[] pns, Object[] pvs) throws NoSuchPropertyException, IllegalArgumentException {
        if (pns.length != pvs.length)
            throw new IllegalArgumentException("pns.length != pvs.length");
        for (int i = 0; i < pns.length; i++)
            setPropertyValue(instance, pns[i], pvs[i]);
    }
    /** get method name array. */
    public abstract String[] getMethodNames();
    /** get method name array. */
    public abstract String[] getDeclaredMethodNames();
    /** has method. */
    public boolean hasMethod(String name) {
        for (String mn : getMethodNames())
            if (mn.equals(name))
                return true;
        return false;
    }
    /** invoke method. */
    public abstract Object invokeMethod(Object instance, String mn, Class<?>[] types, Object[] args) throws NoSuchMethodException, InvocationTargetException;
    private static RemoteTicket makeWrapper(Class<?> c, ClassLoader appClassLoader, ProtectionDomain domain) {}
    public void initTicket(ServiceMetaData metaData) {
        // TODO Auto-generated method stub
    }
}