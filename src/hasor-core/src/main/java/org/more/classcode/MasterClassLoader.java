/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.more.asm.ClassReader;
import org.more.asm.ClassVisitor;
import org.more.asm.ClassWriter;
import org.more.util.ResourcesUtils;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class MasterClassLoader extends ClassLoader {
    private Map<String, ClassConfig> classMap = new ConcurrentHashMap<String, ClassConfig>();
    //
    public MasterClassLoader() {
        //
    }
    public MasterClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }
    //
    public ClassConfig findClassConfig(String className) {
        return this.classMap.get(className);
    }
    //
    protected final Class<?> findClass(final String className) throws ClassNotFoundException {
        if (this.classMap.containsKey(className) == true) {
            byte[] bs = this.classMap.get(className).toBytes();
            return this.defineClass(className, bs, 0, bs.length);
        }
        return super.findClass(className);
    }
    public InputStream getResourceAsStream(final String classResource) {
        if (classResource.endsWith(".class")) {
            String className = classResource.substring(0, classResource.length() - 6).replace("/", ".");
            if (this.classMap.containsKey(className) == true) {
                ClassConfig ce = this.classMap.get(className);
                return new ByteArrayInputStream(ce.toBytes());
            }
        }
        return super.getResourceAsStream(classResource);
    }
    //
    protected byte[] buildClass(final ClassConfig config) throws IOException {
        //
        //1.基本信息
        Class<?> superClass = config.getSuperClass();
        //
        //2.构建visitor环
        //------第一环，写入
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        //------第二环，用户扩展
        ClassVisitor visitor = config.acceptClass(writer);
        visitor = (visitor == null) ? writer : visitor;
        //------第三环，方法委托
        visitor = new MethodDelegateClassAdapter(visitor, config);
        //------第四环，属性委托
        visitor = new PropertyDelegateClassAdapter(visitor, config);
        //------第五环，Aop
        visitor = new AopClassAdapter(visitor, config);
        //
        //3.Read
        String resName = superClass.getName().replace(".", "/") + ".class";
        InputStream inStream = ResourcesUtils.getResourceAsStream(resName);
        ClassReader reader = new ClassReader(inStream);//创建ClassReader
        reader.accept(visitor, ClassReader.SKIP_DEBUG);
        byte[] newClassBytes = writer.toByteArray();
        //
        String cname = config.getClassName();
        if (this.classMap.containsKey(cname) == false) {
            this.classMap.put(cname, config);
        } else {
            newClassBytes = this.classMap.get(cname).toBytes();
        }
        //
        return newClassBytes;
    }
}