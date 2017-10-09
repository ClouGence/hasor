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
package net.hasor.utils;
import net.hasor.utils.ResourcesUtils.ScanEvent;
import net.hasor.utils.ResourcesUtils.ScanItem;
import net.hasor.utils.asm.AnnotationVisitor;
import net.hasor.utils.asm.ClassReader;
import net.hasor.utils.asm.ClassVisitor;
import net.hasor.utils.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @version : 2013-8-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ScanClassPath {
    private ClassLoader                  classLoader  = null;
    private String[]                     scanPackages = null;
    private Map<Class<?>, Set<Class<?>>> cacheMap     = new WeakHashMap<Class<?>, Set<Class<?>>>();
    //
    private ScanClassPath(final String[] scanPackages) {
        this(scanPackages, null);
    }
    private ScanClassPath(final String[] scanPackages, final ClassLoader classLoader) {
        this.scanPackages = scanPackages;
        this.classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
    }
    //
    public static ScanClassPath newInstance(final String[] scanPackages) {
        return new ScanClassPath(scanPackages) {
        };
    }
    public static ScanClassPath newInstance(final String scanPackages) {
        return new ScanClassPath(new String[] { scanPackages }) {
        };
    }
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param packagePath 要扫描的包名。
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public static Set<Class<?>> getClassSet(final String packagePath, final Class<?> compareType) {
        return ScanClassPath.getClassSet(new String[] { packagePath }, compareType);
    }
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param loadPackages 要扫描的包名。
     * @param featureType 要查找的特征。
     * @return 返回扫描结果。
     */
    public static Set<Class<?>> getClassSet(final String[] loadPackages, final Class<?> featureType) {
        return ScanClassPath.newInstance(loadPackages).getClassSet(featureType);
    }
    /**
     * 扫描jar包中凡是匹配compareType参数的类均被返回。（对执行结果不缓存）
     * @param compareType 要查找的特征。
     * @return 返回扫描结果。
     */
    public Set<Class<?>> getClassSet(final Class<?> compareType) {
        //0.尝试从缓存中获取
        Set<Class<?>> returnData = this.cacheMap.get(compareType);
        if (returnData != null) {
            return Collections.unmodifiableSet(returnData);
        }
        //1.准备参数
        final String compareTypeStr = compareType.getName();//要匹配的类型
        final Set<String> classStrSet = new HashSet<String>();//符合条件的Class
        //2.扫描
        for (String tiem : this.scanPackages) {
            if (StringUtils.isBlank(tiem)) {
                continue;
            }
            try {
                ResourcesUtils.scan(tiem.replace(".", "/") + "*.class", new ScanItem() {
                    @Override
                    public void found(final ScanEvent event, final boolean isInJar) throws IOException {
                        String name = event.getName();
                        if (name.endsWith(".class") == false) {
                            return;
                        }
                        //1.取得类名
                        name = name.substring(0, name.length() - ".class".length());
                        name = name.replace("/", ".");
                        //2.装载类
                        InputStream inStream = event.getStream();
                        ClassInfo info = ScanClassPath.this.loadClassInfo(name, inStream, ScanClassPath.this.classLoader);
                        //3.测试目标类是否匹配
                        for (String castType : info.castType) {
                            if (castType.equals(compareTypeStr)) {
                                classStrSet.add(name);
                                return;
                            }
                        }
                        for (String face : info.annos) {
                            if (face.equals(compareTypeStr)) {
                                classStrSet.add(name);
                                return;
                            }
                        }
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        //3.缓存
        returnData = new HashSet<Class<?>>();
        for (String atClass : classStrSet) {
            try {
                Class<?> clazz = Class.forName(atClass, false, this.classLoader);
                returnData.add(clazz);
            } catch (Throwable e) { /**/}
        }
        this.cacheMap.put(compareType, returnData);
        return returnData;
    }
    //
    private Map<String, ClassInfo> classInfoMap = new ConcurrentHashMap<String, ClassInfo>();
    /**分析类的字节码，分析过程中会递归解析父类和实现的接口*/
    private ClassInfo loadClassInfo(String className, final InputStream inStream, final ClassLoader loader) throws IOException {
        /*一、检查类是否已经被加载过，避免重复扫描同一个类*/
        if (this.classInfoMap.containsKey(className) == true) {
            return this.classInfoMap.get(className);
        }
        /*二、使用 ClassReader 读取类的基本信息*/
        ClassReader classReader = null;
        try {
            classReader = new ClassReader(inStream);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
        //className = classReader.getClassName().replace('/', '.');
        /*三、读取类的（名称、父类、接口、注解）信息*/
        final ClassInfo info = new ClassInfo();
        classReader.accept(new ClassVisitor(Opcodes.ASM4) {
            @Override
            public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                //1.读取基本信息
                info.className = name.replace('/', '.');
                if (superName != null) {
                    info.superName = superName.replace('/', '.');
                }
                //2.读取接口
                info.interFaces = interfaces;
                for (int i = 0; i < info.interFaces.length; i++) {
                    info.interFaces[i] = info.interFaces[i].replace('/', '.');
                }
                super.visit(version, access, name, signature, superName, interfaces);
            }
            @Override
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                //3.扫描类信息，获取标记的注解
                /**将一个Ljava/lang/Object;形式的字符串转化为java/lang/Object形式。*/
                String[] annoArrays = info.annos == null ? new String[0] : info.annos;
                //
                String[] newAnnoArrays = new String[annoArrays.length + 1];
                System.arraycopy(annoArrays, 0, newAnnoArrays, 0, annoArrays.length);
                //
                String annnoType = desc.substring(1, desc.length() - 1);
                newAnnoArrays[newAnnoArrays.length - 1] = annnoType.replace('/', '.');
                //
                info.annos = newAnnoArrays;
                return super.visitAnnotation(desc, visible);
            }
        }, ClassReader.SKIP_CODE);
        //四、递归解析父类
        if (info.superName != null) {
            InputStream superStream = loader.getResourceAsStream(info.superName.replace('.', '/') + ".class");
            if (superStream != null) {
                this.loadClassInfo(info.superName, superStream, loader);//加载父类
            }
        }
        //五、递归解析接口
        for (String faces : info.interFaces) {
            InputStream superStream = loader.getResourceAsStream(faces.replace('.', '/') + ".class");
            if (superStream != null) {
                this.loadClassInfo(faces, superStream, loader);//加载父类
            }
        }
        //六、类型链
        Set<String> castTypeList = new TreeSet<String>();/*可转换的类型*/
        String superName = info.superName;
        addCastTypeList(info, castTypeList);//this
        //
        if (superName != null) {
            while (true) {
                if (superName == null || this.classInfoMap.containsKey(superName) == false) {
                    break;
                }
                ClassInfo superInfo = this.classInfoMap.get(superName);
                addCastTypeList(superInfo, castTypeList);//super
                superName = superInfo.superName;
            }
        }
        info.castType = castTypeList.toArray(new String[castTypeList.size()]);
        //
        this.classInfoMap.put(info.className, info);
        return info;
    }
    private void addCastTypeList(final ClassInfo info, final Set<String> addTo) {
        if (info == null) {
            return;
        }
        addTo.add(info.className);
        if (info.superName != null) {
            addTo.add(info.superName);
        }
        if (info.interFaces != null) {
            for (String atFaces : info.interFaces) {
                addTo.add(atFaces);
                this.addCastTypeList(this.classInfoMap.get(atFaces), addTo);
            }
        }
    }
    //
    /**类信息结构*/
    private static class ClassInfo {
        /*类名*/
        public String   className  = null;
        /*继承的父类*/
        public String   superName  = null;
        /*直接实现的接口*/
        public String[] interFaces = new String[0];
        /*可以转换的类型*/
        public String[] castType   = new String[0];
        /*标记的注解*/
        public String[] annos      = new String[0];
    }
}