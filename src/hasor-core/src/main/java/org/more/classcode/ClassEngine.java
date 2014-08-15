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
import java.util.LinkedHashMap;
import java.util.Map;
import org.more.asm.ClassVisitor;
import org.more.asm.ClassWriter;
import org.more.classcode.objects.DefaultAopStrategy;
import org.more.classcode.objects.DefaultClassNameStrategy;
import org.more.classcode.objects.DefaultDelegateStrategy;
import org.more.classcode.objects.DefaultMethodStrategy;
import org.more.classcode.objects.DefaultPropertyStrategy;
/**
* classcode v2.0引擎。新引擎增加了debug模式，在debug模式下{@link ClassEngine#builderClass()}方法在装载生成的新类 时不会抛出。
* 如果没有指定类装载引擎会使用Thread.currentThread().getContextClassLoader()方法返回的类装载器来装载类。
* @version 2010-9-5
* @author 赵永春 (zyc@hasor.net)
*/
public class ClassEngine {
    /**默认超类java.lang.Object。*/
    public static final Class<?>             DefaultSuperClass        = java.lang.Object.class;
    /**默认生成模式{@link BuilderMode#Super}。*/
    public static final BuilderMode          DefaultBuilderMode       = BuilderMode.Super;
    /**默认类名策略{@link DefaultClassNameStrategy}。*/
    public static final ClassNameStrategy    DefaultClassNameStrategy = new DefaultClassNameStrategy();
    /**默认委托策略{@link DefaultDelegateStrategy}*/
    public static final DelegateStrategy     DefaultDelegateStrategy  = new DefaultDelegateStrategy();
    /**默认的Aop策略{@link DefaultAopStrategy}。*/
    public static final AopStrategy          DefaultAopStrategy       = new DefaultAopStrategy();
    /**默认方法策略{@link DefaultMethodStrategy}，负责方法的管理。*/
    public static final MethodStrategy       DefaultMethodStrategy    = new DefaultMethodStrategy();
    /**默认属性策略{@link DefaultPropertyStrategy}。*/
    public static final PropertyStrategy     DefaultPropertyStrategy  = new DefaultPropertyStrategy();
    //
    //策略信息
    private ClassNameStrategy                classNameStrategy        = ClassEngine.DefaultClassNameStrategy; //类名策略，负责生成类名的管理。
    private DelegateStrategy                 delegateStrategy         = ClassEngine.DefaultDelegateStrategy; //委托策略，负责委托接口实现的管理。
    private AopStrategy                      aopStrategy              = ClassEngine.DefaultAopStrategy;      //AOP策略，负责Aop方法的管理。
    private PropertyStrategy                 propertyStrategy         = ClassEngine.DefaultPropertyStrategy; //属性策略。
    private MethodStrategy                   methodStrategy           = ClassEngine.DefaultMethodStrategy;   //方法策略，负责方法的管理。
    //新类信息
    private String                           className                = null;                                //新类名称,由构造方法初始化。
    private Class<?>                         superClass               = null;                                //超类类型,由构造方法初始化。
    private BuilderMode                      builderMode              = ClassEngine.DefaultBuilderMode;      //生成模式
    private Map<Class<?>, MethodDelegate>    addDelegateMap           = null;                                //委托表
    private Map<String, Class<?>>            addPropertyMap           = null;                                //新属性表
    private Map<String, PropertyDelegate<?>> addPropertyDelMap        = null;                                //新委托属性表
    //拦截器，消息器
    private ArrayList<AopInvokeFilter>       aopFilters               = null;                                //aop拦截器表
    private ArrayList<AopBeforeListener>     aopBeforeListeners       = null;                                //开始调用，消息监听器
    private ArrayList<AopReturningListener>  aopReturningListeners    = null;                                //调用返回，消息监听器
    private ArrayList<AopThrowingListener>   aopThrowingListeners     = null;                                //抛出异常，消息监听器
    //生成的
    private Class<?>                         newClass                 = null;                                //新类
    private byte[]                           newClassBytes            = null;                                //新类的字节码。
    private CreatedConfiguration             configuration            = null;
    private RootClassLoader                  rootClassLoader          = null;                                //处理来自类新类装载请求的类装载器。
    //==================================================================================Constructor
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类， */
    public ClassEngine() {
        this(null, ClassEngine.DefaultSuperClass, null);
    };
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类， */
    public ClassEngine(final String className) {
        this(className, ClassEngine.DefaultSuperClass, null);
    };
    /**
     * 创建一个ClassEngine类型对象，该构造参数指定了新类类名、新类的基类以类装载器。<br/>
     * 类装载的设定会遵循如下规则，如果parentLoader参数为空则会使用当前线程的类装载器作为引擎类装载器的父类装载器。
     * 如果指定的是{@link RootClassLoader}类型装载器，则引擎直接使用该类装载器作为引擎的类装载器。如果指定的是一个
     * {@link ClassLoader}类型参数则引擎的类装载器会使用这个类装载器作为其父类装载器。     * @param className 新类的类名，如果类名为空则使用默认生成策略生成。
     * @param superClass 父类类型字符串，该类型最终使用parentLoader参数的类装载器装载。
     * @param parentLoader ClassEngine类装载器。
     */
    public ClassEngine(final String className, final String superClass, final ClassLoader parentLoader) throws ClassNotFoundException {
        this(className, parentLoader.loadClass(superClass), parentLoader);
    };
    /** 创建一个ClassEngine类型对象，参数指定的是新类的父类类型。*/
    public ClassEngine(final Class<?> superClass) {
        this(null, superClass, null);
    };
    /** 创建一个ClassEngine类型对象，参数指定的是新类的父类类型。*/
    public ClassEngine(final Class<?> superClass, final ClassLoader parentLoader) {
        this(null, superClass, parentLoader);
    };
    /**
     * 创建一个ClassEngine类型对象，该构造参数指定了新类类名、新类的基类以类装载器。<br/>
     * 类装载的设定会遵循如下规则，如果parentLoader参数为空则会使用当前线程的类装载器作为引擎类装载器的父类装载器。
     * 如果指定的是{@link RootClassLoader}类型装载器，则引擎直接使用该类装载器作为引擎的类装载器。如果指定的是一个
     * {@link ClassLoader}类型参数则引擎的类装载器会使用这个类装载器作为其父类装载器。
     * @param className 新类的类名，如果类名为空则使用默认生成策略生成。
     * @param superClass 基类类型。
     * @param parentLoader 父类装载器，的父类装载器。
     */
    public ClassEngine(final String className, final Class<?> superClass, final ClassLoader parentLoader) {
        //1.参数className
        if (className == null || className.equals("") == true) {
            this.className = this.classNameStrategy.generateName(superClass);
        } else {
            this.className = className;
        }
        //2.参数superClass
        if (superClass != null) {
            this.superClass = superClass;
        } else {
            this.superClass = ClassEngine.DefaultSuperClass;
        }
        //3.参数parentLoader
        if (parentLoader == null) {
            this.rootClassLoader = new RootClassLoader(Thread.currentThread().getContextClassLoader());
        } else if (parentLoader instanceof RootClassLoader) {
            this.rootClassLoader = (RootClassLoader) parentLoader;
        } else {
            this.rootClassLoader = new RootClassLoader(parentLoader);
        }
    };
    //======================================================================================private
    /**返回新类的名称(字节码形式)。*/
    final String getAsmClassName() {
        return EngineToos.replaceClassName(this.getClassName());
    }
    /**返回父类的名称(字节码形式)。*/
    final String getAsmSuperClassName() {
        return EngineToos.replaceClassName(this.getSuperClass().getName());
    };
    //======================================================================================Get/Set
    /**获取类名的生成策略。*/
    public ClassNameStrategy getClassNameStrategy() {
        return this.classNameStrategy;
    };
    /**设置类名生成策略，如果设置为空则使用默认类名生成策略。*/
    public void setClassNameStrategy(final ClassNameStrategy classNameStrategy) {
        if (classNameStrategy == null) {
            this.classNameStrategy = ClassEngine.DefaultClassNameStrategy;
        } else {
            this.classNameStrategy = classNameStrategy;
        }
    };
    /**获取代理的生成策略。*/
    public DelegateStrategy getDelegateStrategy() {
        return this.delegateStrategy;
    };
    /**设置代理生成策略，如果设置为空则使用默认代理生成策略。*/
    public void setDelegateStrategy(final DelegateStrategy delegateStrategy) {
        if (delegateStrategy == null) {
            this.delegateStrategy = ClassEngine.DefaultDelegateStrategy;
        } else {
            this.delegateStrategy = delegateStrategy;
        }
    };
    /**获取Aop生成策略。*/
    public AopStrategy getAopStrategy() {
        return this.aopStrategy;
    };
    /**设置aop生成策略，如果设置为空则使用默认aop生成策略。*/
    public void setAopStrategy(final AopStrategy aopStrategy) {
        if (aopStrategy == null) {
            this.aopStrategy = ClassEngine.DefaultAopStrategy;
        } else {
            this.aopStrategy = aopStrategy;
        }
    };
    /**获取属性生成策略。*/
    public PropertyStrategy getPropertyStrategy() {
        return this.propertyStrategy;
    };
    /**设置属性生成策略，如果设置为空则使用默认属性生成策略。*/
    public void setPropertyStrategy(final PropertyStrategy propertyStrategy) {
        if (propertyStrategy == null) {
            this.propertyStrategy = ClassEngine.DefaultPropertyStrategy;
        } else {
            this.propertyStrategy = propertyStrategy;
        }
    };
    /**获取Method生成策略。*/
    public MethodStrategy getMethodStrategy() {
        return this.methodStrategy;
    };
    /**设置Method生成策略，如果设置为空则使用默认Method生成策略。*/
    public void setMethodStrategy(final MethodStrategy methodStrategy) {
        if (methodStrategy == null) {
            this.methodStrategy = ClassEngine.DefaultMethodStrategy;
        } else {
            this.methodStrategy = methodStrategy;
        }
    };
    /**获取新类生成方式，默认的生成方式{@link ClassEngine#DefaultBuilderMode Super}。*/
    public BuilderMode getBuilderMode() {
        return this.builderMode;
    };
    /**设置新类生成方式，如果设置为空则使用默认生成方式{@link ClassEngine#DefaultBuilderMode Super}。*/
    public void setBuilderMode(final BuilderMode builderMode) {
        if (builderMode == null) {
            this.builderMode = ClassEngine.DefaultBuilderMode;
        } else {
            this.builderMode = builderMode;
        }
    };
    /** 获取生成的类完整限定名中类名部分。*/
    public String getSimpleName() {
        return EngineToos.splitSimpleName(this.className);
    };
    /** 获取生成类的完整类名。*/
    public String getClassName() {
        return this.className;
    };
    /**设置新类的类名和其所属包。如果包名为null则引擎会调用名称生成策略返回生成的包名。类名也同理。*/
    public void setClassName(final String className) {
        if (className == null || className.equals("") == true) {
            this.className = this.classNameStrategy.generateName(this.superClass);
        } else {
            this.className = className;
        }
    };
    /**该方法是调用类名生成策略生成一个包名以及类名，其原理就是通过设置空类名和空包名来实现。可以通过调用setClassName方法传递两个null来完成。*/
    public void generateName() {
        this.setClassName(null);
    };
    /** 获取生成类的超类(基类)。*/
    public Class<?> getSuperClass() {
        return this.superClass;
    };
    /**设置生成类的基类类型。*/
    public void setSuperClass(final Class<?> superClass) {
        if (superClass == null) {
            throw new NullPointerException("参数为空。");
        }
        this.superClass = superClass;
    };
    /**设置生成类的基类类型，每次改变基类类型都会导致清空附加实现接口列表同时清空生成的字节码数据。*/
    public void setSuperClass(final String superClass, final ClassLoader parentLoader) throws ClassNotFoundException {
        this.setSuperClass(parentLoader.loadClass(superClass));
    };
    /**获取当前引擎正在使用的父类装载器。*/
    public RootClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    };
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
            throw new FormatException("委托不是一个有效的接口类型，或者MethodDelegate类型参数为空。");
        }
        //2.测试该接口是否已经得到实现
        try {
            this.superClass.asSubclass(appendInterface);
            return;
        } catch (Exception e) {}
        //3.检测重复,附加接口实现
        if (this.addDelegateMap == null) {
            this.addDelegateMap = new LinkedHashMap<Class<?>, MethodDelegate>();
        }
        if (this.addDelegateMap.containsKey(appendInterface) == false) {
            this.addDelegateMap.put(appendInterface, delegate);
        }
    };
    /**添加一个AOP过滤器，该过滤器可以重复添加。*/
    public void addAopFilter(final AopInvokeFilter filter) {
        if (filter == null) {
            return;
        }
        if (this.aopFilters == null) {
            this.aopFilters = new ArrayList<AopInvokeFilter>();
        }
        this.aopFilters.add(filter);
    };
    /**添加一个{@link AopBeforeListener}监听器，该监听器可以重复添加。*/
    public void addListener(final AopBeforeListener listener) {
        if (listener == null) {
            return;
        }
        if (this.aopBeforeListeners == null) {
            this.aopBeforeListeners = new ArrayList<AopBeforeListener>();
        }
        this.aopBeforeListeners.add(listener);
    };
    /**添加一个{@link AopReturningListener}监听器，该监听器可以重复添加。*/
    public void addListener(final AopReturningListener listener) {
        if (listener == null) {
            return;
        }
        if (this.aopReturningListeners == null) {
            this.aopReturningListeners = new ArrayList<AopReturningListener>();
        }
        this.aopReturningListeners.add(listener);
    };
    /**添加一个{@link AopThrowingListener}监听器，该监听器可以重复添加。*/
    public void addListener(final AopThrowingListener listener) {
        if (listener == null) {
            return;
        }
        if (this.aopThrowingListeners == null) {
            this.aopThrowingListeners = new ArrayList<AopThrowingListener>();
        }
        this.aopThrowingListeners.add(listener);
    };
    /**在新生成的类中添加一个属性字段，并且依据属性策略生成其get/set方法。*/
    public void addProperty(final String name, final Class<?> type) {
        if (name == null || name.equals("") || type == null) {
            throw new NullPointerException("参数name或type为空。");
        }
        if (this.addPropertyMap == null) {
            this.addPropertyMap = new LinkedHashMap<String, Class<?>>();
        }
        this.addPropertyMap.put(name, type);
    };
    /**在新生成的类中添加一个委托属性，并且依据属性策略生成其get/set方法。*/
    public void addProperty(final String name, final PropertyDelegate<?> delegate) {
        if (name == null || name.equals("") || delegate == null) {
            throw new NullPointerException("参数name或delegate为空。");
        }
        if (this.addPropertyDelMap == null) {
            this.addPropertyDelMap = new LinkedHashMap<String, PropertyDelegate<?>>();
        }
        this.addPropertyDelMap.put(name, delegate);
    };
    /**获取所有添加的属性名集合*/
    public String[] getAppendPropertys() {
        String[] simpleProp = this.getAppendSimplePropertys();
        String[] delegateProp = this.getAppendDelegatePropertys();
        if (simpleProp == null && delegateProp == null) {
            return null;
        }
        //
        if (simpleProp == null) {
            simpleProp = new String[0];
        }
        if (delegateProp == null) {
            delegateProp = new String[0];
        }
        //
        String[] all = new String[simpleProp.length + delegateProp.length];
        for (int i = 0; i < simpleProp.length; i++) {
            all[i] = simpleProp[i];
        }
        int index = simpleProp.length;
        for (int i = 0; i < simpleProp.length; i++) {
            all[index + i] = simpleProp[i];
        }
        return all;
    };
    /**获取所有添加的简单属性名*/
    public String[] getAppendSimplePropertys() {
        if (this.addPropertyMap == null || this.addPropertyMap.size() == 0) {
            return null;
        }
        String[] strs = new String[this.addPropertyMap.size()];
        this.addPropertyMap.keySet().toArray(strs);
        return strs;
    };
    /**获取所有添加的代理属性名*/
    public String[] getAppendDelegatePropertys() {
        if (this.addPropertyDelMap == null || this.addPropertyDelMap.size() == 0) {
            return null;
        }
        String[] strs = new String[this.addPropertyDelMap.size()];
        this.addPropertyDelMap.keySet().toArray(strs);
        return strs;
    };
    /**根据要实现的代理接口获取其代理实现对象。*/
    public MethodDelegate getDelegate(final Class<?> impl) {
        if (this.addDelegateMap == null) {
            return null;
        }
        return this.addDelegateMap.get(impl);
    };
    /**根据属性名获取其属性类型。*/
    public Class<?> getSimplePropertyType(final String name) {
        if (this.addPropertyMap == null) {
            return null;
        }
        return this.addPropertyMap.get(name);
    };
    /**根据代理属性名，获取其属性代理类。*/
    public PropertyDelegate<?> getDelegateProperty(final String name) {
        if (this.addPropertyDelMap == null) {
            return null;
        }
        return this.addPropertyDelMap.get(name);
    };
    /** 获取生成的新类所添加的所有委托接口数组。*/
    public Class<?>[] getDelegates() {
        if (this.addDelegateMap == null || this.addDelegateMap.size() == 0) {
            return null;
        }
        Class<?>[] cl = new Class<?>[this.addDelegateMap.size()];
        this.addDelegateMap.keySet().toArray(cl);
        return cl;
    };
    /**获取其Aop过滤器集合。*/
    public AopInvokeFilter[] getAopFilters() {
        if (this.aopFilters == null || this.aopFilters.size() == 0) {
            return null;
        }
        AopInvokeFilter[] aops = new AopInvokeFilter[this.aopFilters.size()];
        this.aopFilters.toArray(aops);
        return aops;
    };
    /**获取before切面监听器。*/
    public AopBeforeListener[] getAopBeforeListeners() {
        if (this.aopBeforeListeners == null || this.aopBeforeListeners.size() == 0) {
            return null;
        }
        AopBeforeListener[] listeners = new AopBeforeListener[this.aopBeforeListeners.size()];
        this.aopBeforeListeners.toArray(listeners);
        return listeners;
    };
    /**获取returning切面监听器。*/
    public AopReturningListener[] getAopReturningListeners() {
        if (this.aopReturningListeners == null || this.aopReturningListeners.size() == 0) {
            return null;
        }
        AopReturningListener[] listeners = new AopReturningListener[this.aopReturningListeners.size()];
        this.aopReturningListeners.toArray(listeners);
        return listeners;
    };
    /**获取throwing切面监听器。*/
    public AopThrowingListener[] getAopThrowingListeners() {
        if (this.aopThrowingListeners == null || this.aopThrowingListeners.size() == 0) {
            return null;
        }
        AopThrowingListener[] listeners = new AopThrowingListener[this.aopThrowingListeners.size()];
        this.aopThrowingListeners.toArray(listeners);
        return listeners;
    };
    //=======================================================================================Method
    /**
     * 完全重置，该重置方法将会清除新生成的类同时也会清除添加的委托接口以及新属性。<br/>
     * 对于新类的类装载器该方法也会解除在其身上的注册。
     */
    public void reset() {
        this.newClass = null;
        this.newClassBytes = null;
        this.addDelegateMap = null; //委托表
        this.addPropertyMap = null; //新属性表
        this.addPropertyDelMap = null; //新委托属性表
        this.aopFilters = null; //aop拦截器表
        this.aopBeforeListeners = null; //开始调用，消息监听器
        this.aopReturningListeners = null; //调用返回，消息监听器
        this.aopThrowingListeners = null; //抛出异常，消息监听器
        this.rootClassLoader.unRegeditEngine(this);//
    };
    /**清空所有aop配置。*/
    public void resetAop() {
        this.aopFilters = null; //aop拦截器表
        this.aopBeforeListeners = null; //开始调用，消息监听器
        this.aopReturningListeners = null; //调用返回，消息监听器
        this.aopThrowingListeners = null; //抛出异常，消息监听器 
    };
    /**
     * 重置生成状态当再次调用生成时将会启动class构建过程，该方法不会影响到已经注册的aop，新属性等信息。
     * 但是该方法会解除在新类装载上的注册这样以助于从新装载新类。
     */
    public void resetBuilder() {
        this.newClass = null;
        this.newClassBytes = null;
        this.rootClassLoader.unRegeditEngine(this);//
    };
    /**获取已经生成的类对象*/
    public Class<?> toClass() {
        return this.newClass;
    };
    /**获取已经生成的字节码数据*/
    public byte[] toBytes() {
        return this.newClassBytes;
    };
    /**启动生成过程生成新类。*/
    public ClassEngine builderClass() throws ClassNotFoundException, IOException {
        if (this.newClassBytes != null) {
            return this;
        }
        //1.初始化策略
        this.classNameStrategy.initStrategy(this);
        this.delegateStrategy.initStrategy(this);
        this.aopStrategy.initStrategy(this);
        this.propertyStrategy.initStrategy(this);
        this.methodStrategy.initStrategy(this);
        this.rootClassLoader.regeditEngine(this);//注册类装载
        //2.
        if (EngineToos.checkClassName(this.className) == false) {
            throw new FormatException("在生成类的时，检测类名不通过。");
        }
        if (this.className == null || this.className.equals("") == true) {
            this.className = this.classNameStrategy.generateName(this.superClass);
        }
        //3.
        ClassBuilder cb = this.createBuilder(this.builderMode);
        cb.initBuilder(this);
        this.configuration = cb.builderClass();
        if (this.configuration == null) {
            throw new FormatException("builderClass失败。");
        }
        this.newClassBytes = cb.getClassBytes();
        //5.
        this.newClass = this.rootClassLoader.loadClass(this.className);
        return this;
    };
    //======================================================================================Builder
    /**子类可以通过重写该方法来返回一个新的ClassBuilder对象，在ClassBuilder对象中开发人员可以使用classcode扩展功能，同时也可以使用asm框架来扩展。*/
    protected ClassBuilder createBuilder(final BuilderMode builderMode) {
        /*空实现*/
        return new ClassBuilder() {
            @Override
            protected ClassVisitor acceptClass(final ClassWriter classVisitor) {
                return null;
            }
            @Override
            protected void init(final ClassEngine classEngine) {}
        };
    };
    //==========================================================================================New
    /**装载并且创建这个新类的一个实例，如果新类是Propxy模式下的，需要指定代理的父类类型。如果是Super则给null即可。*/
    public Object newInstance(final Object propxyBean) throws ClassNotFoundException, IOException {
        this.builderClass();
        Object obj = null;
        try {
            if (this.builderMode == BuilderMode.Super) {
                obj = this.newClass.newInstance();
            } else {
                obj = this.newClass.getConstructor(this.superClass).newInstance(propxyBean);
            }
        } catch (Exception e) {
            throw new InitializationException("初始化创建新类[" + this.newClass.getName() + "]", e.getCause());
        }
        return this.configuration.configBean(obj);
    };
    /**配置bean，执行aop注入等操作，该参数接收任何ClassEngine创建的对象。*/
    public Object configBean(final Object bean) {
        if (bean == null) {
            throw new NullPointerException("参数不能为空!");
        }
        ClassLoader loader = bean.getClass().getClassLoader();
        if (loader instanceof RootClassLoader == false) {
            return bean;
        }
        RootClassLoader rootLoader = (RootClassLoader) loader;
        ClassEngine engine = rootLoader.getRegeditEngine(bean.getClass().getName());
        return engine.configuration.configBean(bean);
    };
    /**判断该bean是否已经经过配置。*/
    public boolean isConfig(final Object bean) {
        if (bean == null) {
            throw new NullPointerException("参数不能为空!");
        }
        ClassLoader loader = bean.getClass().getClassLoader();
        if (loader instanceof RootClassLoader == false) {
            throw new TypeException("参数所表示的bean不是一个有效的生成bean。");
        }
        //
        try {
            Method method_1 = bean.getClass().getMethod("get" + BuilderClassAdapter.ConfigMarkName);
            Boolean res = (Boolean) method_1.invoke(bean);
            if (res == null) {
                return false;
            }
            return res;
        } catch (Exception e) {
            throw new InvokeException("在执行调用期间发生异常。");
        }
    };
}