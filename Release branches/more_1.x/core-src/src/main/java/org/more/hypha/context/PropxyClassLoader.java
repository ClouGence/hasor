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
package org.more.hypha.context;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
/**
* 
* @version : 2011-5-9
* @author ’‘”¿¥∫ (zyc@byshell.org)
*/
class PropxyClassLoader extends ClassLoader {
    private static Log  log    = LogFactory.getLog(PropxyClassLoader.class);
    private ClassLoader loader = null;
    //
    public ClassLoader getLoader() {
        if (this.loader == null) {
            this.loader = Thread.currentThread().getContextClassLoader();
            log.info("propxy is null, use Thread.currentThread().getContextClassLoader().");
        }
        return this.loader;
    }
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }
    //
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.getLoader().loadClass(name);
    }
    public URL getResource(String name) {
        return this.getLoader().getResource(name);
    }
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.getLoader().getResources(name);
    }
    public InputStream getResourceAsStream(String name) {
        return this.getLoader().getResourceAsStream(name);
    }
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        this.getLoader().setDefaultAssertionStatus(enabled);
    }
    public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
        this.getLoader().setPackageAssertionStatus(packageName, enabled);
    }
    public synchronized void setClassAssertionStatus(String className, boolean enabled) {
        this.getLoader().setClassAssertionStatus(className, enabled);
    }
    public synchronized void clearAssertionStatus() {
        this.getLoader().clearAssertionStatus();
    }
}