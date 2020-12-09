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
package net.hasor.core.aop;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class AopClassLoader extends ClassLoader {
    private final Map<String, InnerClassInfo> classMap   = new ConcurrentHashMap<>();
    private final ThreadLocal<BasicObject>    localLocal = ThreadLocal.withInitial(BasicObject::new);

    public AopClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    public AopClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }

    public AopClassConfig findClassConfig(String className) {
        InnerClassInfo ci = this.classMap.get(className);
        return ci == null ? null : ci.classConfig;
    }

    protected final Class<?> findClass(final String className) throws ClassNotFoundException {
        InnerClassInfo acc = this.classMap.get(className);
        if (acc != null) {
            if (acc.classInfo == null) {
                synchronized (localLocal.get()) {
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
                InnerClassInfo ce = this.classMap.get(className);
                return new ByteArrayInputStream(ce.classConfig.getBytes());
            }
        }
        return super.getResourceAsStream(classResource);
    }

    /***/
    void addClassConfig(AopClassConfig config) {
        String cname = config.getClassName();
        if (!this.classMap.containsKey(cname)) {
            InnerClassInfo ci = new InnerClassInfo();
            ci.classConfig = config;
            ci.classInfo = null;
            this.classMap.put(cname, ci);
        }
    }

    public static Class<?> getPrototypeType(Object aopObject) {
        return getPrototypeType(aopObject.getClass());
    }

    public static Class<?> getPrototypeType(Class<?> aopType) {
        if (aopType.getClassLoader() instanceof AopClassLoader) {
            AopClassConfig classConfig = ((AopClassLoader) aopType.getClassLoader()).findClassConfig(aopType.getName());
            return classConfig.getSuperClass();
        }
        return aopType;
    }

    public static boolean isDynamic(Object aopObject) {
        return isDynamic(aopObject.getClass());
    }

    public static boolean isDynamic(Class<?> aopType) {
        return aopType.getClassLoader() instanceof AopClassLoader;
    }
}