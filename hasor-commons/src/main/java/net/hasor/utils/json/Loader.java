// ========================================================================
// Copyright (c) 2004-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at 
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses. 
// ========================================================================
package net.hasor.utils.json;
import java.net.URL;
/* ------------------------------------------------------------ */
/** ClassLoader Helper.
 * This helper class allows classes to be loaded either from the
 * Thread's ContextClassLoader, the classloader of the derived class
 * or the system ClassLoader.
 *
 * <B>Usage:</B><PRE>
 * public class MyClass {
 *     void myMethod() {
 *          ...
 *          Class c=Loader.loadClass(this.getClass(),classname);
 *          ...
 *     }
 * </PRE>          
 *
 */
class Loader {
    /* ------------------------------------------------------------ */
    public static URL getResource(Class<?> loadClass, String name, boolean checkParents) throws ClassNotFoundException {
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        while (url == null && loader != null) {
            url = loader.getResource(name);
            loader = (url == null && checkParents) ? loader.getParent() : null;
        }
        loader = loadClass == null ? null : loadClass.getClassLoader();
        while (url == null && loader != null) {
            url = loader.getResource(name);
            loader = (url == null && checkParents) ? loader.getParent() : null;
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(name);
        }
        return url;
    }
    /* ------------------------------------------------------------ */
    @SuppressWarnings("rawtypes")
    public static Class loadClass(Class loadClass, String name) throws ClassNotFoundException {
        return loadClass(loadClass, name, false);
    }
    /* ------------------------------------------------------------ */
    /** Load a class.
     *
     * @param loadClass
     * @param name
     * @param checkParents If true, try loading directly from parent classloaders.
     * @return Class
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    public static Class loadClass(Class loadClass, String name, boolean checkParents) throws ClassNotFoundException {
        ClassNotFoundException ex = null;
        Class<?> c = null;
        ClassLoader loader = (loadClass == null) ? Thread.currentThread().getContextClassLoader() : loadClass.getClassLoader();
        while (c == null && loader != null) {
            try {
                c = loader.loadClass(name);
            } catch (ClassNotFoundException e) {
                if (ex == null) {
                    ex = e;
                }
            }
            loader = (c == null && checkParents) ? loader.getParent() : null;
        }
        if (c != null) {
            return c;
        }
        throw ex;
    }
}