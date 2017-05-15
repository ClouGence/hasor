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
package net.hasor.core.classcode;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class MoreClassLoader extends ClassLoader {
    private Map<String, ClassInfo>       classMap  = new ConcurrentHashMap<String, ClassInfo>();
    private ThreadLocal<ClassCodeObject> localLocl = new ThreadLocal<ClassCodeObject>() {
        protected ClassCodeObject initialValue() {
            return new ClassCodeObject();
        }
    };
    //
    public MoreClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }
    public MoreClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }
    //
    public AbstractClassConfig findClassConfig(String className) {
        ClassInfo ci = this.classMap.get(className);
        return ci == null ? null : ci.classConfig;
    }
    //
    protected final Class<?> findClass(final String className) throws ClassNotFoundException {
        ClassInfo acc = this.classMap.get(className);
        if (acc != null) {
            if (acc.classInfo == null) {
                synchronized (localLocl.get()) {
                    if (acc.classInfo == null) {
                        byte[] bs = acc.classConfig.getBytes();
                        acc.classInfo = this.defineClass(className, bs, 0, bs.length);
                    }
                }
            }
            return acc.classInfo;
        }
        return super.findClass(className);
    }
    public InputStream getResourceAsStream(final String classResource) {
        if (classResource.endsWith(".class")) {
            String className = classResource.substring(0, classResource.length() - 6).replace("/", ".");
            if (this.classMap.containsKey(className)) {
                ClassInfo ce = this.classMap.get(className);
                return new ByteArrayInputStream(ce.classConfig.getBytes());
            }
        }
        return super.getResourceAsStream(classResource);
    }
    /***/
    public void addClassConfig(AbstractClassConfig config) {
        String cname = config.getClassName();
        if (!this.classMap.containsKey(cname)) {
            ClassInfo ci = new ClassInfo();
            ci.classConfig = config;
            ci.classInfo = null;
            this.classMap.put(cname, ci);
        } else {
            //
        }
    }
}