/*
 * Copyright 2008-2009 the original author or authors.
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
package org.more.remote.client;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoaderSpi;
import org.more.core.error.FoundException;
/**
 * 
 * @version : 2011-8-19
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClientClassLoaderSpi extends RMIClassLoaderSpi implements ShortName {
    private static FacesMachining loader = null;
    public static FacesMachining getLoader() {
        if (loader == null)
            loader = new FacesMachining();
        return loader;
    }
    /*-------------------------------------------------------------------------------------------------*/
    private void tryLoadClass(String[] names) throws ClassNotFoundException {
        for (String name : names)
            this.tryLoadClass(name);
    }
    private Class<?> tryLoadClass(String name) throws ClassNotFoundException {
        if (name.length() > ShortNameLength)
            if (name.substring(name.length() - ShortNameLength).equals(ShortName) == true) {
                String className = name.substring(0, name.length() - ShortNameLength);
                Class<?> type = Thread.currentThread().getContextClassLoader().loadClass(className);
                try {
                    return getLoader().createFaces(type);
                } catch (IOException e) {
                    throw new FoundException("FacesMachining对象无法装载‘" + className + "’类型的字节码数据。");
                }
            }
        return Thread.currentThread().getContextClassLoader().loadClass(name);
    }
    /*-------------------------------------------------------------------------------------------------*/
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.tryLoadClass(name);
    }
    public Class<?> loadClass(String codebase, String name, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
        this.tryLoadClass(name);
        return sun.rmi.server.LoaderHandler.loadClass(codebase, name, getLoader());
    }
    public Class<?> loadProxyClass(String codebase, String[] interfaces, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
        this.tryLoadClass(interfaces);
        return sun.rmi.server.LoaderHandler.loadProxyClass(codebase, interfaces, getLoader());
    }
    public ClassLoader getClassLoader(String codebase) throws MalformedURLException {
        return sun.rmi.server.LoaderHandler.getClassLoader(codebase);
    }
    public String getClassAnnotation(Class<?> cl) {
        return sun.rmi.server.LoaderHandler.getClassAnnotation(cl);
    }
    //    public Class<?> loadClass(String codebase, String name, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
    //        this.tryLoadClass(name);
    //        return RMIClassLoader.getDefaultProviderInstance().loadClass(codebase, name, getLoader());
    //    }
    //    public Class<?> loadProxyClass(String codebase, String[] interfaces, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
    //        this.tryLoadClass(interfaces);
    //        return RMIClassLoader.getDefaultProviderInstance().loadProxyClass(codebase, interfaces, getLoader());
    //    }
    //    public ClassLoader getClassLoader(String codebase) throws MalformedURLException {
    //        return RMIClassLoader.getDefaultProviderInstance().getClassLoader(codebase);
    //    }
    //    public String getClassAnnotation(Class<?> cl) {
    //        return RMIClassLoader.getDefaultProviderInstance().getClassAnnotation(cl);
    //    }
}