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
package org.more.core.classcode;
import java.util.LinkedList;
/**
 * {@link ClassEngine}引擎的类装载器，该类的职责是负责装载所有引擎生成的Class字节码。<br/>
 * 除此之外当引擎需要装载某些参数的类型时也需要通过该类装载器来装载。
 * @version 2010-9-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class RootClassLoader extends ClassLoader {
    private LinkedList<ClassEngine> classEngineList = null;
    /**创建引擎类装载器，parentClassLoader参数是该装载器所使用的父类装载器。*/
    public RootClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
        this.classEngineList = new LinkedList<ClassEngine>();
    }
    /**负责装载新类。*/
    @Override
    protected final Class<?> findClass(String name) throws ClassNotFoundException {
        for (ClassEngine engine : this.classEngineList)
            if (engine.getClassName().equals(name) == true) {
                byte[] bs = engine.toBytes();
                return this.defineClass(name, bs, 0, bs.length);
            }
        return super.findClass(name);
    }
    /**获取某一个已经类型的字节码，该字节码必须是通过{@link ClassEngine}引擎生成的。*/
    public byte[] toBytes(Class<?> type) {
        for (ClassEngine engine : this.classEngineList) {
            Class<?> findType = engine.toClass();
            if (findType == type)
                return engine.toBytes();
        }
        return null;
    }
    /**注册一个{@link ClassEngine}引擎到类装载器中。*/
    public void regeditEngine(ClassEngine classEngine) {
        if (this.classEngineList.contains(classEngine) == false)
            this.classEngineList.add(classEngine);
    }
    /**解除一个{@link ClassEngine}引擎的注册，接触注册之后该类装载器将不能再次获取到其字节码。*/
    public void unRegeditEngine(ClassEngine classEngine) {
        if (this.classEngineList.contains(classEngine) == true)
            this.classEngineList.remove(classEngine);
    }
}