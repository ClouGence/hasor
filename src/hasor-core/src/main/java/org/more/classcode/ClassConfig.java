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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.more.asm.ClassVisitor;
import org.more.classcode.objects.SimplePropertyDelegate;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClassConfig {
    /**默认超类java.lang.Object。*/
    public static final Class<?>             DefaultSuperClass = java.lang.Object.class;
    private Class<?>                         superClass        = DefaultSuperClass;
    private String                           className         = null;                   //新类名称
    private byte[]                           classBytes        = null;                   //新类字节码
    private MasterClassLoader                parentLoader      = new MasterClassLoader();
    //
    private List<InnerAopInterceptor>        aopList           = null;                   //Aop
    //
    private Map<Class<?>, MethodDelegate>    newDelegateMap    = null;                   //方法委托
    private Map<String, PropertyDelegate<?>> newPropertyMap    = null;                   //属性委托
    //
    //
    //
    public ClassConfig(Class<?> superClass) {
        this.superClass = superClass;
    }
    public ClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        this.superClass = superClass;
        if (parentLoader instanceof MasterClassLoader) {
            this.parentLoader = (MasterClassLoader) parentLoader;
        } else {
            this.parentLoader = new MasterClassLoader(parentLoader);
        }
    }
    /**
     * 向新类中添加一个委托接口实现，该委托接口中的所有方法均通过委托对象代理处理。如果委托接口中有方法与基类的方法冲突时。
     * 新生成的委托方法则会丢弃委托接口中的方法去保留基类方法。这在java中也是相当于实现，但是更重要的是保护了基类。
     * 如果重复添加同一个接口则该接口将被置于最后一次添加。注意：如果试图添加一个非接口类型则会引发异常。
     * @param appendInterface 要附加的接口。
     * @param delegate 委托接口的方法处理委托。
     */
    public void addDelegate(final Class<?> appendInterface, final MethodDelegate delegate) {
        //1.参数判断
        if (appendInterface.isInterface() == false || delegate == null) {
            throw new ClassCastException("委托不是一个有效的接口类型，或者MethodDelegate类型参数为空。");
        }
        //2.测试该接口是否已经得到实现
        if (this.superClass.isAssignableFrom(appendInterface) == true) {
            throw new IllegalStateException("委托接口已经被实现。");
        }
        //3.检测重复,附加接口实现
        if (this.newDelegateMap == null) {
            this.newDelegateMap = new LinkedHashMap<Class<?>, MethodDelegate>();
        }
        if (this.newDelegateMap.containsKey(appendInterface) == false) {
            this.newDelegateMap.put(appendInterface, delegate);
        }
    };
    /**在新生成的类中添加一个属性字段，并且依据属性策略生成其get/set方法。*/
    public void addProperty(final String name, final Class<?> type) {
        if (name == null || name.equals("") || type == null) {
            throw new NullPointerException("参数name或type为空。");
        }
        this.addProperty(name, new SimplePropertyDelegate(type), true, true);
    };
    public void addProperty(final String name, final PropertyDelegate<?> delegate, boolean readOnly) {
        this.addProperty(name, delegate, readOnly, false);
    }
    /**在新生成的类中添加一个委托属性，并且依据属性策略生成其get/set方法。*/
    public void addProperty(final String name, final PropertyDelegate<?> delegate, boolean read, boolean write) {
        if (name == null || name.equals("") || delegate == null) {
            throw new NullPointerException("参数name或delegate为空。");
        }
        if (this.newPropertyMap == null) {
            this.newPropertyMap = new LinkedHashMap<String, PropertyDelegate<?>>();
        }
        //检测是否是为已存在的属性，如果是则类型必须相同
        //        this.getSuperClass().getMethod("", parameterTypes)
        //
        this.newPropertyMap.put(name, delegate);
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopInterceptor aopInterceptor) {
        this.addAopInterceptor(new AopMatcher() {
            public boolean matches(Method target) {
                return true;
            }
        }, aopInterceptor);
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopMatcher aopMatcher, AopInterceptor... aopInterceptor) {
        for (AopInterceptor aop : aopInterceptor) {
            this.addAopInterceptor(aopMatcher, aop);
        }
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopMatcher aopMatcher, AopInterceptor aopInterceptor) {
        if (this.aopList == null) {
            this.aopList = new ArrayList<InnerAopInterceptor>();
        }
        this.aopList.add(new InnerAopInterceptor(aopMatcher, aopInterceptor));
    }
    //
    protected ClassVisitor acceptClass(ClassVisitor writer) {
        return null;
    }
    //
    public synchronized Class<?> toClass() throws IOException, ClassNotFoundException {
        if (this.classBytes == null) {
            this.classBytes = this.parentLoader.buildClass(this);
        }
        return this.parentLoader.loadClass(getClassName());
    }
    //
    public byte[] toBytes() {
        return this.classBytes;
    }
    public Class<?> getSuperClass() {
        return superClass;
    }
    public String getClassName() {
        return this.className;
    }
    //
    //
    //
    //bytecode called.
    private Map<String, Method>           $methodMapping   = null;
    private Map<String, AopInterceptor[]> $finalAopMapping = new HashMap<String, AopInterceptor[]>();
    //
    private void initMapping() {
        if (this.$methodMapping != null) {
            return;
        }
        this.$methodMapping = new HashMap<String, Method>();
        Method[] methodSet = this.getSuperClass().getMethods();
        for (Method tMethod : methodSet) {
            String tmDesc = InnerEngineToos.toAsmFullDesc(tMethod);
            this.$methodMapping.put(tmDesc, tMethod);
        }
    }
    AopInterceptor[] findInterceptor(String tmDesc) {
        AopInterceptor[] aopArrays = this.$finalAopMapping.get(tmDesc);
        if (aopArrays == null) {
            //
            this.initMapping();
            List<AopInterceptor> aopList = new ArrayList<AopInterceptor>();
            //
            Method targetMethod = this.$methodMapping.get(tmDesc);
            if (targetMethod != null && this.aopList != null) {
                for (InnerAopInterceptor inner : this.aopList) {
                    if (inner.matches(targetMethod) == true) {
                        aopList.add(inner);
                    }
                }
            }
            //
            aopArrays = aopList.toArray(new AopInterceptor[aopList.size()]);
            this.$finalAopMapping.put(tmDesc, aopArrays);
        }
        return aopArrays;
    }
    void setClassName(String className) {
        this.className = className;
    }
}