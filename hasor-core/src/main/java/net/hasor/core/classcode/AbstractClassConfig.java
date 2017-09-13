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
import net.hasor.utils.asm.ClassReader;
import net.hasor.utils.asm.ClassVisitor;
import net.hasor.utils.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractClassConfig {
    /**默认超类java.lang.Object。*/
    public static final Class<?>        DefaultSuperClass = ClassCodeObject.class;
    private             Class<?>        superClass        = DefaultSuperClass;
    private             String          className         = null;                                    //新类名称
    private             byte[]          classBytes        = null;                                    //新类字节码
    private             MoreClassLoader parentLoader      = new MoreClassLoader();
    //
    /**创建{@link AbstractClassConfig}类型对象。 */
    public AbstractClassConfig(Class<?> superClass) {
        this(superClass, superClass.getClassLoader());
    }
    /**创建{@link AbstractClassConfig}类型对象。 */
    public AbstractClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        this.superClass = superClass;
        this.className = this.initClassName();
        if (parentLoader instanceof MoreClassLoader) {
            this.parentLoader = (MoreClassLoader) parentLoader;
        } else {
            this.parentLoader = new MoreClassLoader(parentLoader);
        }
    }
    //
    private static AtomicLong index = new AtomicLong(0);
    protected static long index() {
        return index.getAndIncrement();
    }
    protected String initClassName() {
        return this.superClass.getName() + "$Auto$" + index();
    }
    //
    protected ClassVisitor acceptClass(ClassVisitor writer) {
        return null;
    }
    /**调用ClassLoader，生成字节码并装载它*/
    public synchronized Class<?> toClass() throws IOException, ClassNotFoundException {
        this.classBytes = this.buildBytes();
        return this.parentLoader.findClass(getClassName());
    }
    /**取得字节码信息*/
    public byte[] getBytes() {
        return this.classBytes;
    }
    /**父类类型*/
    public Class<?> getSuperClass() {
        return superClass;
    }
    /**新类类名*/
    public String getClassName() {
        return this.className;
    }
    /**新类类名*/
    public String getSimpleName() {
        if (this.className == null)
            return null;
        return this.className.substring(this.className.lastIndexOf(".") + 1);
    }
    public byte[] buildBytes() throws IOException {
        if (this.classBytes == null) {
            this.classBytes = this.buildClass();
            this.parentLoader.addClassConfig(this);
        }
        return this.classBytes;
    }
    ;
    /**父类是否支持*/
    public static boolean isSupport(Class<?> superClass) {
        String resName = superClass.getName().replace(".", "/") + ".class";
        if (resName.startsWith("java/") || resName.startsWith("javax/")) {
            return false;
        } else {
            return ASMEngineTools.checkIn(superClass.getModifiers(), Modifier.PUBLIC);
        }
    }
    /**父类是否支持*/
    public boolean isSupport() {
        return isSupport(this.getSuperClass());
    }
    //
    protected final byte[] buildClass() throws IOException {
        //1.基本信息
        Class<?> superClass = this.getSuperClass();
        String resName = superClass.getName().replace(".", "/") + ".class";
        if (!isSupport()) {
            throw new IOException("class in package java or javax , does not support.");
        }
        //2.构建visitor环
        //------第一环，写入
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        //------第二环，用户扩展
        ClassVisitor visitor = this.acceptClass(writer);
        visitor = (visitor == null) ? writer : visitor;
        //------第三环，Aop
        visitor = this.buildClassVisitor(visitor);
        //3.Read
        ClassLoader typeLoader = superClass.getClassLoader();
        if (typeLoader == null) {
            typeLoader = ClassLoader.getSystemClassLoader();
        }
        InputStream inStream = typeLoader.getResourceAsStream(resName);
        ClassReader reader = new ClassReader(inStream);//创建ClassReader
        reader.accept(visitor, ClassReader.SKIP_DEBUG);
        return writer.toByteArray();
    }
    protected abstract ClassVisitor buildClassVisitor(ClassVisitor parentVisitor);

    /**是否包含改变*/
    public abstract boolean hasChange();
}