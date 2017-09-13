/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.utils;
import java.util.Map;
import java.util.WeakHashMap;
/**
 * An instance of this class represents a value that is provided per (thread)
 * context classloader.
 *
 * <p>Occasionally it is necessary to store data in "global" variables
 * (including uses of the Singleton pattern). In applications which have only
 * a single classloader such data can simply be stored as "static" members on
 * some class. When multiple classloaders are involved, however, this approach
 * can fail; in particular, this doesn't work when the code may be run within a
 * servlet container or a j2ee container, and the class on which the static
 * member is defined is loaded via a "shared" classloader that is visible to all
 * components running within the container. This class provides a mechanism for
 * associating data with a ClassLoader instance, which ensures that when the
 * code runs in such a container each component gets its own copy of the
 * "global" variable rather than unexpectedly sharing a single copy of the
 * variable with other components that happen to be running in the same
 * container at the same time (eg servlets or EJBs.)</p>
 *
 * <p>This class is strongly patterned after the java.lang.ThreadLocal
 * class, which performs a similar task in allowing data to be associated
 * with a particular thread.</p>
 *
 * <p>When code that uses this class is run as a "normal" application, ie
 * not within a container, the effect is identical to just using a static 
 * member variable to store the data, because Thread.getContextClassLoader
 * always returns the same classloader (the system classloader).</p>
 *
 * <p>Expected usage is as follows:<br>
 * <pre>
 *  public class SomeClass {
 *    private static final ContextClassLoaderLocal global 
 *      = new ContextClassLoaderLocal() {
 *          protected Object initialValue() {
 *              return new String("Initial value");
 *          };
 *
 *    public void testGlobal() {
 *      String s = (String) global.get();
 *      System.out.println("global value:" + s);
 *      buf.set("New Value");
 *    }
 * </pre>
 * </p>
 *
 * <p><strong>Note:</strong> This class takes some care to ensure that when
 * a component which uses this class is "undeployed" by a container the
 * component-specific classloader and all its associated classes (and their
 * static variables) are garbage-collected. Unfortunately there is one
 * scenario in which this does <i>not</i> work correctly and there
 * is unfortunately no known workaround other than ensuring that the
 * component (or its container) calls the "unset" method on this class for
 * each instance of this class when the component is undeployed. The problem
 * occurs if:
 * <ul>
 * <li>the class containing a static instance of this class was loaded via
 * a shared classloader, and</li>
 * <li>the value stored in the instance is an object whose class was loaded
 * via the component-specific classloader (or any of the objects it refers
 * to were loaded via that classloader).</li>
 * </ul>
 * The result is that the map managed by this object still contains a strong
 * reference to the stored object, which contains a strong reference to the
 * classloader that loaded it, meaning that although the container has
 * "undeployed" the component the component-specific classloader and all the
 * related classes and static variables cannot be garbage-collected. This is
 * not expected to be an issue with the commons-beanutils library as the only
 * classes which use this class are BeanUtilsBean and ConvertUtilsBean and
 * there is no obvious reason for a user of the beanutils library to subclass
 * either of those classes.</p>
 *
 * <p><strong>Note:</strong> A WeakHashMap bug in several 1.3 JVMs results in 
 * a memory leak for those JVMs.</p>
 *
 * <p><strong>Note:</strong> Of course all of this would be unnecessary if
 * containers required each component to load the full set of classes it
 * needs, ie avoided providing classes loaded via a "shared" classloader.</p>
 *
 * @see Thread#getContextClassLoader
 * @author Eric Pabst
 */
public class ContextClassLoaderLocal<T> {
    private Map<ClassLoader, T> valueByClassLoader     = new WeakHashMap<ClassLoader, T>();
    private boolean             globalValueInitialized = false;
    private T globalValue;
    /** Construct a context classloader instance */
    public ContextClassLoaderLocal() {
        super();
    }
    /** Construct a context classloader instance */
    public ContextClassLoaderLocal(T globalValue) {
        super();
        if (globalValue != null) {
            this.set(globalValue);
        }
    }
    /**
     * Returns the initial value for this ContextClassLoaderLocal
     * variable. This method will be called once per Context ClassLoader for
     * each ContextClassLoaderLocal, the first time it is accessed 
     * with get or set.  If the programmer desires ContextClassLoaderLocal variables
     * to be initialized to some value other than null, ContextClassLoaderLocal must
     * be subclassed, and this method overridden.  Typically, an anonymous
     * inner class will be used.  Typical implementations of initialValue
     * will call an appropriate constructor and return the newly constructed
     * object.
     *
     * @return a new Object to be used as an initial value for this ContextClassLoaderLocal
     */
    protected T initialValue() {
        return null;
    }
    /**
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container. 
     * @return the object currently associated with the context-classloader of the current thread. 
     */
    public synchronized T get() {
        // synchronizing the whole method is a bit slower 
        // but guarantees no subtle threading problems, and there's no 
        // need to synchronize valueByClassLoader
        // make sure that the map is given a change to purge itself
        this.valueByClassLoader.isEmpty();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                T value = this.valueByClassLoader.get(contextClassLoader);
                if (value == null && !this.valueByClassLoader.containsKey(contextClassLoader)) {
                    value = this.initialValue();
                    this.valueByClassLoader.put(contextClassLoader, value);
                }
                return value;
            }
        } catch (SecurityException e) { /* SWALLOW - should we log this? */}
        // if none or exception, return the globalValue 
        if (!this.globalValueInitialized) {
            this.globalValue = this.initialValue();
            this.globalValueInitialized = true;
        } //else already set
        return this.globalValue;
    }
    /**
     * Sets the value - a value is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container. 
     *
     * @param value the object to be associated with the entrant thread's context classloader
     */
    public synchronized void set(final T value) {
        // synchronizing the whole method is a bit slower 
        // but guarentees no subtle threading problems
        // make sure that the map is given a change to purge itself
        this.valueByClassLoader.isEmpty();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                this.valueByClassLoader.put(contextClassLoader, value);
                return;
            }
        } catch (SecurityException e) { /* SWALLOW - should we log this? */}
        // if in doubt, set the global value
        this.globalValue = value;
        this.globalValueInitialized = true;
    }
    /**
     * Unsets the value associated with the current thread's context classloader
     */
    public synchronized void unset() {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            this.unset(contextClassLoader);
        } catch (SecurityException e) { /* SWALLOW - should we log this? */}
    }
    /**
     * Unsets the value associated with the given classloader
     * @param classLoader The classloader to <i>unset</i> for
     */
    public synchronized void unset(final ClassLoader classLoader) {
        this.valueByClassLoader.remove(classLoader);
    }
}