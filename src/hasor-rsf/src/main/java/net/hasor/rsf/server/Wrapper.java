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
package net.hasor.rsf.server;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
/**
 * Wrapper
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class Wrapper {
    private static AtomicLong                   WRAPPER_CLASS_COUNTER = new AtomicLong(0);
    private static final Map<Class<?>, Wrapper> WRAPPER_MAP           = new ConcurrentHashMap<Class<?>, Wrapper>();                   // class
                                                                                                                                       // wrapper map
    private static final String[]               EMPTY_STRING_ARRAY    = new String[0];
    private static final String[]               OBJECT_METHODS        = new String[] { "getClass", "hashCode", "toString", "equals" };
    //    private static final Wrapper                OBJECT_WRAPPER        = new Wrapper() {
    //                                                                          public String[] getMethodNames() {
    //                                                                              return OBJECT_METHODS;
    //                                                                          }
    //                                                                          public String[] getDeclaredMethodNames() {
    //                                                                              return OBJECT_METHODS;
    //                                                                          }
    //                                                                          public String[] getPropertyNames() {
    //                                                                              return EMPTY_STRING_ARRAY;
    //                                                                          }
    //                                                                          public Class<?> getPropertyType(String pn) {
    //                                                                              return null;
    //                                                                          }
    //                                                                          public Object getPropertyValue(Object instance, String pn) throws NoSuchPropertyException {
    //                                                                              throw new NoSuchPropertyException("Property [" + pn + "] not found.");
    //                                                                          }
    //                                                                          public void setPropertyValue(Object instance, String pn, Object pv) throws NoSuchPropertyException {
    //                                                                              throw new NoSuchPropertyException("Property [" + pn + "] not found.");
    //                                                                          }
    //                                                                          public boolean hasProperty(String name) {
    //                                                                              return false;
    //                                                                          }
    //                                                                          public Object invokeMethod(Object instance, String mn, Class<?>[] types, Object[] args) throws NoSuchMethodException {
    //                                                                              if ("getClass".equals(mn))
    //                                                                                  return instance.getClass();
    //                                                                              if ("hashCode".equals(mn))
    //                                                                                  return instance.hashCode();
    //                                                                              if ("toString".equals(mn))
    //                                                                                  return instance.toString();
    //                                                                              if ("equals".equals(mn)) {
    //                                                                                  if (args.length == 1)
    //                                                                                      return instance.equals(args[0]);
    //                                                                                  throw new IllegalArgumentException("Invoke method [" + mn + "] argument number error.");
    //                                                                              }
    //                                                                              throw new NoSuchMethodException("Method [" + mn + "] not found.");
    //                                                                          }
    //                                                                      };
    /** get wrapper. */
    public static Wrapper getWrapper(Class<?> c) {
        ClassLoader appClassLoader = c.getClassLoader();
        ProtectionDomain domain = c.getProtectionDomain();
        while (ClassGenerator.isDynamicClass(c))
            // can not wrapper on dynamic class.
            c = c.getSuperclass();
        if (c == Object.class)
            return OBJECT_WRAPPER;
        Wrapper ret = WRAPPER_MAP.get(c);
        if (ret == null) {
            ret = makeWrapper(c, appClassLoader, domain);
            WRAPPER_MAP.put(c, ret);
        }
        return ret;
    }
    /** get property name array. */
    abstract public String[] getPropertyNames();
    /** get property type.*/
    abstract public Class<?> getPropertyType(String pn);
    /** has property.*/
    abstract public boolean hasProperty(String name);
    /** get property value. */
    abstract public Object getPropertyValue(Object instance, String pn) throws NoSuchPropertyException, IllegalArgumentException;
    /** set property value. */
    abstract public void setPropertyValue(Object instance, String pn, Object pv) throws NoSuchPropertyException, IllegalArgumentException;
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
    abstract public String[] getMethodNames();
    /** get method name array. */
    abstract public String[] getDeclaredMethodNames();
    /** has method. */
    public boolean hasMethod(String name) {
        for (String mn : getMethodNames())
            if (mn.equals(name))
                return true;
        return false;
    }
    /** invoke method. */
    abstract public Object invokeMethod(Object instance, String mn, Class<?>[] types, Object[] args) throws NoSuchMethodException, InvocationTargetException;
    private static Wrapper makeWrapper(Class<?> c, ClassLoader appClassLoader, ProtectionDomain domain) {}
}