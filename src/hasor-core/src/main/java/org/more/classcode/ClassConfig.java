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
import org.more.util.BeanUtils;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClassConfig {
    /**默认超类java.lang.Object。*/
    public static final Class<?>               DefaultSuperClass = java.lang.Object.class;
    private Class<?>                           superClass        = DefaultSuperClass;
    private String                             className         = null;                   //新类名称
    private byte[]                             classBytes        = null;                   //新类字节码
    private MasterClassLoader                  parentLoader      = new MasterClassLoader();
    //
    private List<InnerAopInterceptor>          aopList           = null;                   //Aop
    private Map<Class<?>, MethodDelegate>      newDelegateMap    = null;                   //方法委托
    private Map<String, InnerPropertyDelegate> newPropertyMap    = null;                   //属性委托
    //
    //
    /**创建{@link ClassConfig}类型对象。 */
    public ClassConfig(Class<?> superClass) {
        this.superClass = superClass;
        this.className = this.initClassName();
    }
    /**创建{@link ClassConfig}类型对象。 */
    public ClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        this.superClass = superClass;
        this.className = this.initClassName();
        if (parentLoader instanceof MasterClassLoader) {
            this.parentLoader = (MasterClassLoader) parentLoader;
        } else {
            this.parentLoader = new MasterClassLoader(parentLoader);
        }
    }
    protected String initClassName() {
        return this.superClass.getName() + "$Aop";
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
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, Class<?> propertyType) {
        this.addProperty(propertyName, propertyType, true, true);
    }
    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法。
     * @param readOnly 是否为只读属性
     */
    public void addProperty(final String propertyName, Class<?> propertyType, boolean readOnly) {
        this.addProperty(propertyName, propertyType, !readOnly, true);
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, Class<?> propertyType, boolean canRead, boolean canWrite) {
        if (propertyName == null || propertyName.equals("") || propertyType == null) {
            throw new NullPointerException("参数 propertyName 或 propertyType 为空。");
        }
        this.addProperty(propertyName, new SimplePropertyDelegate(propertyType), canRead, canWrite);
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, final PropertyDelegate<?> delegate) {
        this.addProperty(propertyName, delegate, true, true);
    }
    /**
    * 动态添加一个属性，并且生成可以属性的get/set方法。
    * @param readOnly 是否为只读属性
    */
    public void addProperty(final String propertyName, final PropertyDelegate<?> delegate, boolean readOnly) {
        this.addProperty(propertyName, delegate, !readOnly, true);
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, final PropertyDelegate<?> delegate, boolean canRead, boolean canWrite) {
        if (propertyName == null || propertyName.equals("") || delegate == null) {
            throw new NullPointerException("参数 propertyName 或 delegate 为空。");
        }
        //如果存在这个属性，则抛出异常
        boolean readMark = BeanUtils.canReadProperty(propertyName, this.getSuperClass());
        boolean writeMark = BeanUtils.canWriteProperty(propertyName, this.getSuperClass());
        if (readMark == true || writeMark == true) {
            throw new IllegalStateException("已存在的属性。");
        }
        //
        if (this.newPropertyMap == null) {
            this.newPropertyMap = new LinkedHashMap<String, InnerPropertyDelegate>();
        }
        //
        InnerPropertyDelegate inner = new InnerPropertyDelegate(propertyName, delegate, canRead, canWrite);
        this.newPropertyMap.put(propertyName, inner);
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptors(AopMatcher aopMatcher, AopInterceptor... aopInterceptor) {
        for (AopInterceptor aop : aopInterceptor) {
            this.addAopInterceptor(aopMatcher, aop);
        }
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopInterceptor aopInterceptor) {
        this.addAopInterceptor(new AopMatcher() {
            public boolean matcher(Method target) {
                return true;
            }
        }, aopInterceptor);
    }
    /**添加Aop拦截器。*/
    public void addAopInterceptor(AopMatcher aopMatcher, AopInterceptor aopInterceptor) {
        if (this.aopList == null) {
            this.aopList = new ArrayList<InnerAopInterceptor>();
        }
        this.aopList.add(new InnerAopInterceptor(aopMatcher, aopInterceptor));
    }
    /**是否包含改变*/
    public boolean hasChange() {
        if (this.aopList.isEmpty() == false ||
            this.newDelegateMap.isEmpty() == false ||
            this.newPropertyMap.isEmpty() == false) {
            return true;
        }
        return false;
    }
    //
    protected ClassVisitor acceptClass(ClassVisitor writer) {
        return null;
    }
    /**调用ClassLoader，生成字节码并装载它*/
    public synchronized Class<?> toClass() throws IOException, ClassNotFoundException {
        if (this.classBytes == null) {
            this.classBytes = this.parentLoader.buildClass(this);
        }
        return this.parentLoader.loadClass(getClassName());
    }
    /**取得字节码信息*/
    public byte[] toBytes() {
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
    public PropertyDelegate<Object> getPropertyDelegate(String propertyName) {
        if (this.newPropertyMap != null) {
            return this.newPropertyMap.get(propertyName);
        }
        return null;
    }
    //
    //
    //
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
    //
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
                    if (inner.matcher(targetMethod) == true) {
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
    InnerPropertyDelegate[] getNewPropertyList() {
        if (this.newPropertyMap == null) {
            return new InnerPropertyDelegate[0];
        }
        InnerPropertyDelegate[] newProperty = this.newPropertyMap.values()//
                .toArray(new InnerPropertyDelegate[this.newPropertyMap.size()]);
        return newProperty;
    }
}