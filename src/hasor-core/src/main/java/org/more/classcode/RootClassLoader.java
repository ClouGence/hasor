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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
/**
 * {@link ClassEngine}引擎的类装载器，该类的职责是负责装载所有引擎生成的Class字节码。<br/>
 * 除此之外当引擎需要装载某些参数的类型时也需要通过该类装载器来装载。
 * @version 2010-9-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class RootClassLoader extends ClassLoader {
    private Map<String, ClassEngine> classMap2 = null; // key is org.more.Test
    private Map<String, ClassEngine> classMap  = null; // key is org/more.Test.class
    /**创建引擎类装载器，parentClassLoader参数是该装载器所使用的父类装载器。*/
    public RootClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
        this.classMap = new HashMap<String, ClassEngine>();
        this.classMap2 = new HashMap<String, ClassEngine>();
    }
    /**负责装载新类。*/
    protected final Class<?> findClass(String name) throws ClassNotFoundException {
        if (this.classMap.containsKey(name) == true) {
            byte[] bs = this.classMap.get(name).toBytes();
            return this.defineClass(name, bs, 0, bs.length);
        }
        return super.findClass(name);
    }
    /**获取某一个已经类型的字节码，该字节码必须是通过{@link ClassEngine}引擎生成的。*/
    public byte[] toBytes(Class<?> type) {
        ClassLoader cl = type.getClassLoader();
        if (cl instanceof RootClassLoader)
            return ((RootClassLoader) cl).getRegeditEngine(type.getName()).toBytes();
        return null;
    }
    /**注册一个{@link ClassEngine}引擎到类装载器中。*/
    public void regeditEngine(ClassEngine classEngine) {
        String cn = classEngine.getClassName();
        if (this.classMap.containsKey(cn) == false) {
            this.classMap.put(cn, classEngine);
            cn = cn.replace(".", "/") + ".class";
            this.classMap2.put(cn, classEngine);
        }
    }
    /**解除一个{@link ClassEngine}引擎的注册，接触注册之后该类装载器将不能再次获取到其字节码。*/
    public void unRegeditEngine(ClassEngine classEngine) {
        String cn = classEngine.getClassName();
        if (this.classMap.containsKey(cn) == true) {
            this.classMap.remove(cn);
            cn = cn.replace(".", "/") + ".class";
            this.classMap2.put(cn, classEngine);
        }
    }
    /**获取一个以注册的{@link ClassEngine}引擎。*/
    public ClassEngine getRegeditEngine(String className) {
        if (this.classMap.containsKey(className) == true)
            return this.classMap.get(className);
        return null;
    }
    public InputStream getResourceAsStream(String name) {
        if (this.classMap.containsKey(name) == true) {
            ClassEngine ce = this.classMap.get(name);
            return new ByteArrayInputStream(ce.toBytes());
        }
        if (this.classMap2.containsKey(name) == true) {
            ClassEngine ce = this.classMap2.get(name);
            return new ByteArrayInputStream(ce.toBytes());
        }
        return super.getResourceAsStream(name);
    }
}