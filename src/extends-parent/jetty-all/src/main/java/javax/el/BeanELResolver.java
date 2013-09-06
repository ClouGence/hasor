/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.el;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.ref.SoftReference;
import java.beans.FeatureDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines property resolution behavior on objects using the JavaBeans
 * component architecture.
 *
 * <p>This resolver handles base objects of any type, as long as the
 * base is not <code>null</code>. It accepts any object as a property
 * or method, and coerces it to a string.
 *
 * <p>For property resolution, the
 * property string is used to find a JavaBeans compliant property on
 * the base object. The value is accessed using JavaBeans getters and setters.
 * </p>
 *
 * <p>For method resolution, the method string is the name
 * of the method in the bean.  The parameter types can be optionally
 * specified to identify the method.  If the parameter types are not
 * specified, the parameter objects are used in the method resolution.
 * </p>
 * 
 * <p>This resolver can be constructed in read-only mode, which means that
 * {@link #isReadOnly} will always return <code>true</code> and 
 * {@link #setValue} will always throw
 * <code>PropertyNotWritableException</code>.</p>
 *
 * <p><code>ELResolver</code>s are combined together using 
 * {@link CompositeELResolver}s, to define rich semantics for evaluating 
 * an expression. See the javadocs for {@link ELResolver} for details.</p>
 *
 * <p>Because this resolver handles base objects of any type, it should
 * be placed near the end of a composite resolver. Otherwise, it will
 * claim to have resolved a property before any resolvers that come after
 * it get a chance to test if they can do so as well.</p>
 *
 * @see CompositeELResolver
 * @see ELResolver
 * @since JSP 2.1
 */
public class BeanELResolver extends ELResolver {

    private boolean isReadOnly;

     private static final int CACHE_SIZE = 1024;
     private static final ConcurrentHashMap<Class, BeanProperties> properties =
         new ConcurrentHashMap<Class, BeanProperties>(CACHE_SIZE);

    /*
     * Defines a property for a bean.
     */
    protected final static class BeanProperty {

        private Method readMethod;
        private Method writeMethod;
        private PropertyDescriptor descriptor;
                                                                                
        public BeanProperty(Class<?> baseClass,
                            PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
            readMethod = getMethod(baseClass, descriptor.getReadMethod());
            writeMethod = getMethod(baseClass, descriptor.getWriteMethod());
        }
                                                                                
        public Class getPropertyType() {
            return descriptor.getPropertyType();
        }
                                                                                
        public boolean isReadOnly() {
            return getWriteMethod() == null;
        }
                                                                                
        public Method getReadMethod() {
            return readMethod;
        }
                                                                                
        public Method getWriteMethod() {
            return writeMethod;
        }
    }
                                                                                
    /*
     * Defines the properties for a bean.
     */
    protected final static class BeanProperties {

        private final Map<String, BeanProperty> propertyMap =
            new HashMap<String, BeanProperty>();
                                                                                
        public BeanProperties(Class<?> baseClass) {
            PropertyDescriptor[] descriptors;
            try {
                BeanInfo info = Introspector.getBeanInfo(baseClass);
                descriptors = info.getPropertyDescriptors();
            } catch (IntrospectionException ie) {
                throw new ELException(ie);
            }
            for (PropertyDescriptor pd: descriptors) {
                propertyMap.put(pd.getName(),
                                new BeanProperty(baseClass, pd));
            }
        }
                                                                                
        public BeanProperty getBeanProperty(String property) {
            return propertyMap.get(property);
        }
    }

    /**
     * Creates a new read/write <code>BeanELResolver</code>.
     */
    public BeanELResolver() {
        this.isReadOnly = false;
    }

    /**
     * Creates a new <code>BeanELResolver</code> whose read-only status is
     * determined by the given parameter.
     *
     * @param isReadOnly <code>true</code> if this resolver cannot modify
     *     beans; <code>false</code> otherwise.
     */
    public BeanELResolver(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    /**
     * If the base object is not <code>null</code>, returns the most 
     * general acceptable type that can be set on this bean property.
     *
     * <p>If the base is not <code>null</code>, the 
     * <code>propertyResolved</code> property of the <code>ELContext</code>
     * object must be set to <code>true</code> by this resolver, before
     * returning. If this property is not <code>true</code> after this 
     * method is called, the caller should ignore the return value.</p>
     *
     * <p>The provided property will first be coerced to a <code>String</code>.
     * If there is a <code>BeanInfoProperty</code> for this property and
     * there were no errors retrieving it, the <code>propertyType</code> of
     * the <code>propertyDescriptor</code> is returned. Otherwise, a
     * <code>PropertyNotFoundException</code> is thrown.</p>
     *
     * @param context The context of this evaluation.
     * @param base The bean to analyze.
     * @param property The name of the property to analyze. Will be coerced to
     *     a <code>String</code>.
     * @return If the <code>propertyResolved</code> property of 
     *     <code>ELContext</code> was set to <code>true</code>, then
     *     the most general acceptable type; otherwise undefined.
     * @throws NullPointerException if context is <code>null</code>
     * @throws PropertyNotFoundException if <code>base</code> is not
     *     <code>null</code> and the specified property does not exist
     *     or is not readable.
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public Class<?> getType(ELContext context,
                         Object base,
                         Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null || property == null){
            return null;
        }

        BeanProperty bp = getBeanProperty(context, base, property);
        context.setPropertyResolved(true);
        return bp.getPropertyType();
    }

    /**
     * If the base object is not <code>null</code>, returns the current
     * value of the given property on this bean.
     *
     * <p>If the base is not <code>null</code>, the 
     * <code>propertyResolved</code> property of the <code>ELContext</code>
     * object must be set to <code>true</code> by this resolver, before
     * returning. If this property is not <code>true</code> after this 
     * method is called, the caller should ignore the return value.</p>
     *
     * <p>The provided property name will first be coerced to a
     * <code>String</code>. If the property is a readable property of the 
     * base object, as per the JavaBeans specification, then return the 
     * result of the getter call. If the getter throws an exception, 
     * it is propagated to the caller. If the property is not found or is 
     * not readable, a <code>PropertyNotFoundException</code> is thrown.</p>
     *
     * @param context The context of this evaluation.
     * @param base The bean on which to get the property.
     * @param property The name of the property to get. Will be coerced to
     *     a <code>String</code>.
     * @return If the <code>propertyResolved</code> property of 
     *     <code>ELContext</code> was set to <code>true</code>, then
     *     the value of the given property. Otherwise, undefined.
     * @throws NullPointerException if context is <code>null</code>.
     * @throws PropertyNotFoundException if <code>base</code> is not
     *     <code>null</code> and the specified property does not exist
     *     or is not readable.
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public Object getValue(ELContext context,
                           Object base,
                           Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null || property == null){
            return null;
        }

        BeanProperty bp = getBeanProperty(context, base, property);
        Method method = bp.getReadMethod();
        if (method == null) {
            throw new PropertyNotFoundException(
                        ELUtil.getExceptionMessageString(context,
                            "propertyNotReadable",
                            new Object[] { base.getClass().getName(),
                                           property.toString()}));
        }

        Object value;
        try {
            value = method.invoke(base, new Object[0]);
            context.setPropertyResolved(true);
        } catch (ELException ex) {
            throw ex;
        } catch (InvocationTargetException ite) {
            throw new ELException(ite.getCause());
        } catch (Exception ex) {
            throw new ELException(ex);
        }
        return value;
    }

    /**
     * If the base object is not <code>null</code>, attempts to set the
     * value of the given property on this bean.
     *
     * <p>If the base is not <code>null</code>, the 
     * <code>propertyResolved</code> property of the <code>ELContext</code>
     * object must be set to <code>true</code> by this resolver, before
     * returning. If this property is not <code>true</code> after this 
     * method is called, the caller can safely assume no value was set.</p>
     *
     * <p>If this resolver was constructed in read-only mode, this method will
     * always throw <code>PropertyNotWritableException</code>.</p>
     *
     * <p>The provided property name will first be coerced to a
     * <code>String</code>. If property is a writable property of 
     * <code>base</code> (as per the JavaBeans Specification), the setter 
     * method is called (passing <code>value</code>). If the property exists
     * but does not have a setter, then a
     * <code>PropertyNotFoundException</code> is thrown. If the property
     * does not exist, a <code>PropertyNotFoundException</code> is thrown.</p>
     *
     * @param context The context of this evaluation.
     * @param base The bean on which to set the property.
     * @param property The name of the property to set. Will be coerced to
     *     a <code>String</code>.
     * @param val The value to be associated with the specified key.
     * @throws NullPointerException if context is <code>null</code>.
     * @throws PropertyNotFoundException if <code>base</code> is not
     *     <code>null</code> and the specified property does not exist.
     * @throws PropertyNotWritableException if this resolver was constructed
     *     in read-only mode, or if there is no setter for the property.
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public void setValue(ELContext context,
                         Object base,
                         Object property,
                         Object val) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null || property == null){
            return;
        }

        if (isReadOnly) {
            throw new PropertyNotWritableException(
                        ELUtil.getExceptionMessageString(context,
                            "resolverNotwritable",
                            new Object[] { base.getClass().getName() }));
        } 

        BeanProperty bp = getBeanProperty(context, base, property);
        Method method = bp.getWriteMethod();
        if (method == null) {
            throw new PropertyNotWritableException(
                        ELUtil.getExceptionMessageString(context,
                            "propertyNotWritable",
                            new Object[] { base.getClass().getName(),
                                           property.toString()}));
        }

        try {
            method.invoke(base, new Object[] {val});
            context.setPropertyResolved(true);
        } catch (ELException ex) {
            throw ex;
        } catch (InvocationTargetException ite) {
            throw new ELException(ite.getCause());
        } catch (Exception ex) {
            if (null == val) {
                val = "null";
            }
            String message = ELUtil.getExceptionMessageString(context,
                    "setPropertyFailed",
                    new Object[] { property.toString(),
                                   base.getClass().getName(), val });
            throw new ELException(message, ex);
        }
    }

    /**
     * If the base object is not <code>null</code>, invoke the method, with
     * the given parameters on this bean.  The return value from the method
     * is returned.
     *
     * <p>If the base is not <code>null</code>, the
     * <code>propertyResolved</code> property of the <code>ELContext</code>
     * object must be set to <code>true</code> by this resolver, before
     * returning. If this property is not <code>true</code> after this
     * method is called, the caller should ignore the return value.</p>
     *
     * <p>The provided method object will first be coerced to a
     * <code>String</code>.  The methods in the bean is then examined and 
     * an attempt will be made to select one for invocation.  If no suitable
     * can be found, a <code>MethodNotFoundException</code> is thrown.
     *
     * If the given paramTypes is not <code>null</code>, select the method
     * with the given name and parameter types.
     *
     * Else select the method with the given name that has the same number
     * of parameters.  If there are more than one such method, the method
     * selection process is undefined.
     *
     * Else select the method with the given name that takes a variable
     * number of arguments.
     *
     * Note the resolution for overloaded methods will likely be clarified
     * in a future version of the spec.
     *
     * The provide parameters are coerced to the correcponding parameter
     * types of the method, and the method is then invoked.
     *
     * @param context The context of this evaluation.
     * @param base The bean on which to invoke the method
     * @param method The simple name of the method to invoke.
     *     Will be coerced to a <code>String</code>.  If method is
     *     "&lt;init&gt;"or "&lt;clinit&gt;" a MethodNotFoundException is
     *     thrown.
     * @param paramTypes An array of Class objects identifying the
     *     method's formal parameter types, in declared order.
     *     Use an empty array if the method has no parameters.
     *     Can be <code>null</code>, in which case the method's formal
     *     parameter types are assumed to be unknown.
     * @param params The parameters to pass to the method, or
     *     <code>null</code> if no parameters.
     * @return The result of the method invocation (<code>null</code> if
     *     the method has a <code>void</code> return type).
     * @throws MethodNotFoundException if no suitable method can be found.
     * @throws ELException if an exception was thrown while performing
     *     (base, method) resolution.  The thrown exception must be
     *     included as the cause property of this exception, if
     *     available.  If the exception thrown is an
     *     <code>InvocationTargetException</code>, extract its
     *     <code>cause</code> and pass it to the
     *     <code>ELException</code> constructor.
     * @since EL 2.2
     */

    public Object invoke(ELContext context,
                         Object base,
                         Object method,
                         Class<?>[] paramTypes,
                         Object[] params) {

        if (base == null || method == null) {
            return null;
        }
        Method m = findMethod(base, method.toString(), paramTypes, params);
        Object ret = invokeMethod(m, base, params);
        context.setPropertyResolved(true);
        return ret;
    }

    /**
     * If the base object is not <code>null</code>, returns whether a call
     * to {@link #setValue} will always fail.
     *
     * <p>If the base is not <code>null</code>, the 
     * <code>propertyResolved</code> property of the <code>ELContext</code>
     * object must be set to <code>true</code> by this resolver, before
     * returning. If this property is not <code>true</code> after this 
     * method is called, the caller can safely assume no value was set.</p>
     *
     * <p>If this resolver was constructed in read-only mode, this method will
     * always return <code>true</code>.</p>
     *
     * <p>The provided property name will first be coerced to a
     * <code>String</code>. If property is a writable property of 
     * <code>base</code>, <code>false</code> is returned. If the property is
     * found but is not writable, <code>true</code> is returned. If the
     * property is not found, a <code>PropertyNotFoundException</code>
     * is thrown.</p>
     *
     * @param context The context of this evaluation.
     * @param base The bean to analyze.
     * @param property The name of the property to analyzed. Will be coerced to
     *     a <code>String</code>.
     * @return If the <code>propertyResolved</code> property of 
     *     <code>ELContext</code> was set to <code>true</code>, then
     *     <code>true</code> if calling the <code>setValue</code> method
     *     will always fail or <code>false</code> if it is possible that
     *     such a call may succeed; otherwise undefined.
     * @throws NullPointerException if context is <code>null</code>
     * @throws PropertyNotFoundException if <code>base</code> is not
     *     <code>null</code> and the specified property does not exist.
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public boolean isReadOnly(ELContext context,
                              Object base,
                              Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null || property == null){
            return false;
        }

        context.setPropertyResolved(true);
        if (isReadOnly) {
            return true;
        }

        BeanProperty bp = getBeanProperty(context, base, property);
        return bp.isReadOnly();
    }

    /**
     * If the base object is not <code>null</code>, returns an
     * <code>Iterator</code> containing the set of JavaBeans properties
     * available on the given object. Otherwise, returns <code>null</code>.
     *
     * <p>The <code>Iterator</code> returned must contain zero or more 
     * instances of {@link java.beans.FeatureDescriptor}. Each info object 
     * contains information about a property in the bean, as obtained by
     * calling the <code>BeanInfo.getPropertyDescriptors</code> method.
     * The <code>FeatureDescriptor</code> is initialized using the same
     * fields as are present in the <code>PropertyDescriptor</code>,
     * with the additional required named attributes "<code>type</code>" and 
     * "<code>resolvableAtDesignTime</code>" set as follows:
     * <dl>
     *     <li>{@link ELResolver#TYPE} - The runtime type of the property, from
     *         <code>PropertyDescriptor.getPropertyType()</code>.</li>
     *     <li>{@link ELResolver#RESOLVABLE_AT_DESIGN_TIME} - <code>true</code>.</li>
     * </dl>
     * </p>
     * 
     * @param context The context of this evaluation.
     * @param base The bean to analyze.
     * @return An <code>Iterator</code> containing zero or more 
     *     <code>FeatureDescriptor</code> objects, each representing a property
     *     on this bean, or <code>null</code> if the <code>base</code>
     *     object is <code>null</code>.
     */
    public Iterator<FeatureDescriptor> getFeatureDescriptors(
                                          ELContext context,
                                          Object base) {
        if (base == null){
            return null;
        }

        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(base.getClass());
        } catch (Exception ex) {
        }
        if (info == null) {
            return null;
        }
        ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(
                info.getPropertyDescriptors().length);
        for (PropertyDescriptor pd: info.getPropertyDescriptors()) {
            pd.setValue("type", pd.getPropertyType());
            pd.setValue("resolvableAtDesignTime", Boolean.TRUE);
            list.add(pd);
        }
        return list.iterator();
    }

    /**
     * If the base object is not <code>null</code>, returns the most 
     * general type that this resolver accepts for the 
     * <code>property</code> argument. Otherwise, returns <code>null</code>.
     *
     * <p>Assuming the base is not <code>null</code>, this method will always
     * return <code>Object.class</code>. This is because any object is
     * accepted as a key and is coerced into a string.</p>
     *
     * @param context The context of this evaluation.
     * @param base The bean to analyze.
     * @return <code>null</code> if base is <code>null</code>; otherwise
     *     <code>Object.class</code>.
     */
    public Class<?> getCommonPropertyType(ELContext context,
                                               Object base) {
        if (base == null){
            return null;
        }

        return Object.class;
    }

    /*
     * Get a public method form a public class or interface of a given method.
     * Note that if a PropertyDescriptor is obtained for a non-public class that
     * implements a public interface, the read/write methods will be for the
     * class, and therefore inaccessible.  To correct this, a version of the
     * same method must be found in a superclass or interface.
     **/

    static private Method getMethod(Class cl, Method method) {

        if (method == null) {
            return null;
        }

        if (Modifier.isPublic (cl.getModifiers ())) {
            return method;
        }
        Class [] interfaces = cl.getInterfaces ();
        for (int i = 0; i < interfaces.length; i++) {
            Class c = interfaces[i];
            Method m = null;
            try {
                m = c.getMethod(method.getName(), method.getParameterTypes());
                c = m.getDeclaringClass();
                if ((m = getMethod(c, m)) != null)
                    return m;
            } catch (NoSuchMethodException ex) {
            }
        }
        Class c = cl.getSuperclass();
        if (c != null) {
            Method m = null;
            try {
                m = c.getMethod(method.getName(), method.getParameterTypes());
                c = m.getDeclaringClass();
                if ((m = getMethod(c, m)) != null)
                    return m;
            } catch (NoSuchMethodException ex) {
            }
        }
        return null;
    }

    private BeanProperty getBeanProperty(ELContext context,
                                         Object base,
                                         Object prop) {

        String property = prop.toString();
        Class baseClass = base.getClass();
        BeanProperties bps = properties.get(baseClass);
        if (bps == null) {
            bps = new BeanProperties(baseClass);
            properties.putIfAbsent(baseClass, bps);
        }
        BeanProperty bp = bps.getBeanProperty(property);
        if (bp == null) {
            throw new PropertyNotFoundException(
                        ELUtil.getExceptionMessageString(context,
                            "propertyNotFound",
                            new Object[] { baseClass.getName(),
                                           property}));
        }
        return bp;
    }

    private void removeFromMap(Map<Class, BeanProperties> map,
                               ClassLoader classloader) {
        Iterator<Class> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            Class mbeanClass = iter.next();
            if (classloader.equals(mbeanClass.getClassLoader())) {
                iter.remove();
            }
        }

    }

    /*
     * This method is not part of the API, though it can be used (reflectively)
     * by clients of this class to remove entries from the cache when the beans
     * are being unloaded.
     *
     * A note about why WeakHashMap is not used.  Measurements has shown 
     * that ConcurrentHashMap is much more scalable than synchronized
     * WeakHashMap.  A manual purge seems to be a good compromise.
     *
     * @param classloader The classLoader used to load the beans.
     */
    private void purgeBeanClasses(ClassLoader classloader) {
        removeFromMap(properties, classloader);
    }

    private Method findMethod(Object base, String method,
                              Class<?>[] paramTypes, Object[] params) {

        Class<?>beanClass = base.getClass();
        if (paramTypes != null) {
            try {
                return beanClass.getMethod(method, paramTypes);
            } catch (java.lang.NoSuchMethodException ex) {
                throw new MethodNotFoundException(ex);
            }
        }

        int paramCount = (params == null)? 0: params.length;
        for (Method m: base.getClass().getMethods()) {
            if (m.getName().equals(method) && (
                         m.isVarArgs() ||
                         m.getParameterTypes().length==paramCount)){
                return m;
            }
        }
        throw new MethodNotFoundException("Method " + method + " not found");
    }

    private Object invokeMethod(Method m, Object base, Object[] params) {

        Class[] parameterTypes = m.getParameterTypes();
        Object[] parameters = null;
        if (parameterTypes.length > 0) {
            ExpressionFactory exprFactory = ExpressionFactory.newInstance();
            if (m.isVarArgs()) {
                // TODO
            } else {
                parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    parameters[i] = exprFactory.coerceToType(params[i],
                                                           parameterTypes[i]);
                }
            }
        }
        try {
            return m.invoke(base, parameters);
        } catch (IllegalAccessException iae) {
            throw new ELException(iae);
        } catch (InvocationTargetException ite) {
            throw new ELException(ite.getCause());
        }
    }
}

