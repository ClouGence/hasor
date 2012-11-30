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
package org.more.util;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.more.util.ResourcesUtil.ScanEvent;
import org.more.util.ResourcesUtil.ScanItem;
/**
 * 有关class的工具类。
 * @version : 2011-6-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ClassUtil {
    private String[]                     scanPackages = null;
    private Map<Class<?>, Set<Class<?>>> cacheMap     = new HashMap<Class<?>, Set<Class<?>>>();
    private ClassUtil(String[] scanPackages) {
        this.scanPackages = scanPackages;
    };
    public static ClassUtil newInstance(String[] scanPackages) {
        return new ClassUtil(scanPackages) {};
    }
    public static ClassUtil newInstance(String scanPackages) {
        return new ClassUtil(new String[] { scanPackages }) {};
    }
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param packagePath 要扫描的包名。
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(Class<?> compareType) {
        Set<Class<?>> returnData = this.cacheMap.get(compareType);
        if (returnData == null) {
            returnData = getClassSet(scanPackages, compareType);
            this.cacheMap.put(compareType, returnData);
        }
        return Collections.unmodifiableSet(returnData);
    }
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param packagePath 要扫描的包名。
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public static Set<Class<?>> getClassSet(String packagePath, Class<?> compareType) {
        return getClassSet(new String[] { packagePath }, compareType);
    }
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param packages 要扫描的包名。
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public static Set<Class<?>> getClassSet(final String[] packages, final Class<?> compareType) {
        final Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            ResourcesUtil.scan(packages[0].replace(".", "/") + "*.class", new ScanItem() {
                @Override
                public boolean goFind(ScanEvent event, boolean isInJar) {
                    String name = event.getName();
                    name = name.substring(0, name.length() - ".class".length());
                    name = name.replace("/", ".");
                    try {
                        Class<?> loadType = Thread.currentThread().getContextClassLoader().loadClass(name);
                        if (compareType.isAnnotation() == true) {
                            //目标是注解
                            Class<Annotation> annoType = (Class<Annotation>) compareType;
                            if (loadType.getAnnotation(annoType) != null)
                                classSet.add(loadType);
                        } else if (compareType.isAssignableFrom(loadType) == true)
                            //目标是类
                            classSet.add(loadType);
                    } catch (ClassNotFoundException e) {}
                    return false;
                }
            });
        } catch (Throwable e) {}
        return classSet;
    }
};