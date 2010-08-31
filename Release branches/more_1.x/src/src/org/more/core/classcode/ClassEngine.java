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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.more.FormatException;
import org.more.StateException;
import org.more.core.classcode.objects.DefaultAopStrategy;
import org.more.core.classcode.objects.DefaultClassNameStrategy;
import org.more.core.classcode.objects.DefaultDelegateStrategy;
import org.more.core.classcode.objects.DefaultMethodStrategy;
import org.more.core.classcode.objects.DefaultPropertyStrategy;
/**
 * 字节码生成工具，该工具可以在已有类型上附加接口实现，使用ClassEngine还可以对类对象提供AOP的支持，此外ClassEngine提供了两种工作模式。<br/>
 * <br/><b>继承方式</b>--继承方式实现新类，这种生成模式下必须要求先有类型后有对象。生成的新类是继承原有类实现的，
 * 所有附加方法都写到新类中。原始类中的所有方法都被重写并且以super形式调用父类。私有方法不包括重写范畴。
 * 私有方法将不参与AOP功能。在继承模式下保护方法与公共方法参与AOP功能。<br/>
 * <br/><b>代理方式</b>--代理方式实现新类，这种生成模式下可以在已有的对象上附加接口实现而不需要重新创建对象。同时生成的新对象
 * 不破坏原有对象。整个实现方式就是一个静态代理方式实现。注意这种生成方式会取消所有原始类中的构造方法。
 * 取而代之的是生成一个一个参数的构造方法，该参数类型就是基类类型。所有方法调用都使用这个注入的类型对象调用。
 * 同时该中生成方式的私有方法不包括重写范畴。<br/>
 * 在代理模式下只有公共方法参与AOP功能，私有方法和受保护的方法因访问权限问题不能参与AOP。<br/>
 * <br/><b>AOP特性</b>--ClassEngine引擎的AOP特性是可以配置是否启用的。如果附加AOP相关功能则字节码在生成时除了经过了第一次接口附加操作之后
 * 还需要经过第二次AOP特性加入。所有本类方法包括可以继承的方法均被重写。启用AOP特性会少量增加字节码体积同时也比不使用AOP特性的运行效率要慢些。
 * @version 2009-10-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassEngine extends ClassLoader {
    //Engine配置信息，默认信息
    public static final String               DefaultSuperClass        = "java.lang.Object";            //超类
    public static final BuilderMode          DefaultBuilderMode       = BuilderMode.Super;             //默认生成模式
    public static final ClassNameStrategy    DefaultClassNameStrategy = new DefaultClassNameStrategy(); //类名策略
    public static final DelegateStrategy     DefaultDelegateStrategy  = new DefaultDelegateStrategy(); //委托策略
    public static final AopStrategy          DefaultAopStrategy       = new DefaultAopStrategy();      //AOP策略
    public static final MethodStrategy       DefaultMethodStrategy    = new DefaultMethodStrategy();   //方法策略，负责方法的管理。
    public static final PropertyStrategy     DefaultPropertyStrategy  = new DefaultPropertyStrategy(); //属性策略。
    private boolean                          debug                    = false;                         //调试模式，如果开启调试模式则只生成字节码不装载它
    //
    static {}
    //
    //策略信息
    private ClassNameStrategy                classNameStrategy        = DefaultClassNameStrategy;      //类名策略，负责生成类名的管理。
    private DelegateStrategy                 delegateStrategy         = DefaultDelegateStrategy;       //委托策略，负责委托接口实现的管理。
    private AopStrategy                      aopStrategy              = DefaultAopStrategy;            //AOP策略，负责Aop方法的管理。
    private PropertyStrategy                 propertyStrategy         = DefaultPropertyStrategy;       //属性策略。
    private MethodStrategy                   methodStrategy           = DefaultMethodStrategy;         //方法策略，负责方法的管理。
    //新类信息
    private String                           className                = null;                          //新类名称,由构造方法初始化。
    private Class<?>                         superClass               = null;                          //超类类型,由构造方法初始化。
    private BuilderMode                      builderMode              = DefaultBuilderMode;            //生成模式
    private Map<Class<?>, MethodDelegate>    addDelegateMap           = null;                          //委托表
    private Map<String, Class<?>>            addPropertyMap           = null;                          //新属性表
    private Map<String, PropertyDelegate<?>> addPropertyDelMap        = null;                          //新委托属性表
    //拦截器，消息器
    private ArrayList<AopInvokeFilter>       aopFilters               = null;                          //aop拦截器表
    private ArrayList<AopBeforeListener>     aopBeforeListeners       = null;                          //开始调用，消息监听器
    private ArrayList<AopReturningListener>  aopReturningListeners    = null;                          //调用返回，消息监听器
    private ArrayList<AopThrowingListener>   aopThrowingListeners     = null;                          //抛出异常，消息监听器
    //生成的
    private Class<?>                         newClass                 = null;                          //新类
    private byte[]                           newClassBytes            = null;                          //新类的字节码。
    private ClassConfiguration               configuration            = null;
    //==================================================================================Constructor
    public ClassEngine(boolean debug) throws ClassNotFoundException {
        this();
        this.debug = debug;
    }
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类，使用的是当前线程的ClassLoader类装载对象作为父装载器。 */
    public ClassEngine() throws ClassNotFoundException {
        this(ClassLoader.getSystemClassLoader().loadClass(DefaultSuperClass));
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类，使用的是当前线程的ClassLoader类装载对象作为父装载器。 */
    public ClassEngine(String className) throws ClassNotFoundException {
        this(className, DefaultSuperClass, ClassLoader.getSystemClassLoader());
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类，使用的是当前线程的ClassLoader类装载对象作为父装载器。 */
    public ClassEngine(String className, String superClass, ClassLoader parentLoader) throws ClassNotFoundException {
        this(className, parentLoader.loadClass(superClass));
    }
    public ClassEngine(Class<?> superClass) {
        this(null, superClass);
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类，使用的是当前线程的ClassLoader类装载对象作为父装载器。 */
    public ClassEngine(String className, Class<?> superClass) {
        super(Thread.currentThread().getContextClassLoader());
        if (className == null || className.equals("") == true) {
            String packageName = this.classNameStrategy.generatePackageName();
            String simpleName = this.classNameStrategy.generateSimpleName();
            this.className = packageName + "." + simpleName;
        } else
            this.className = className;
        this.superClass = superClass;
    }
    //======================================================================================Get/Set
    /**获取类名的生成策略。*/
    public ClassNameStrategy getClassNameStrategy() {
        return this.classNameStrategy;
    }
    /**设置类名生成策略，如果设置为空则使用默认类名生成策略。*/
    public void setClassNameStrategy(ClassNameStrategy classNameStrategy) {
        if (classNameStrategy == null)
            this.classNameStrategy = DefaultClassNameStrategy;
        else
            this.classNameStrategy = classNameStrategy;
    }
    /**获取代理的生成策略。*/
    public DelegateStrategy getDelegateStrategy() {
        return this.delegateStrategy;
    }
    /**设置代理生成策略，如果设置为空则使用默认代理生成策略。*/
    public void setDelegateStrategy(DelegateStrategy delegateStrategy) {
        if (delegateStrategy == null)
            this.delegateStrategy = DefaultDelegateStrategy;
        else
            this.delegateStrategy = delegateStrategy;
    }
    /**获取Aop生成策略。*/
    public AopStrategy getAopStrategy() {
        return this.aopStrategy;
    }
    /**设置aop生成策略，如果设置为空则使用默认aop生成策略。*/
    public void setAopStrategy(AopStrategy aopStrategy) {
        if (aopStrategy == null)
            this.aopStrategy = DefaultAopStrategy;
        else
            this.aopStrategy = aopStrategy;
    }
    /**获取属性生成策略。*/
    public PropertyStrategy getPropertyStrategy() {
        return propertyStrategy;
    }
    /**设置属性生成策略，如果设置为空则使用默认属性生成策略。*/
    public void setPropertyStrategy(PropertyStrategy propertyStrategy) {
        if (propertyStrategy == null)
            this.propertyStrategy = DefaultPropertyStrategy;
        else
            this.propertyStrategy = propertyStrategy;
    }
    /**获取Method生成策略。*/
    public MethodStrategy getMethodStrategy() {
        return methodStrategy;
    }
    /**设置Method生成策略，如果设置为空则使用默认Method生成策略。*/
    public void setMethodStrategy(MethodStrategy methodStrategy) {
        if (methodStrategy == null)
            this.methodStrategy = DefaultMethodStrategy;
        else
            this.methodStrategy = methodStrategy;
    }
    /**获取新类生成方式，默认的生成方式{@link ClassEngine#DefaultBuilderMode Super}。*/
    public BuilderMode getBuilderMode() {
        return this.builderMode;
    }
    /**设置新类生成方式，如果设置为空则使用默认生成方式{@link ClassEngine#DefaultBuilderMode Super}。*/
    public void setBuilderMode(BuilderMode builderMode) {
        if (builderMode == null)
            this.builderMode = DefaultBuilderMode;
        else
            this.builderMode = builderMode;
    }
    /** 获取生成的类完整限定名中类名部分。*/
    public String getSimpleName() {
        return EngineToos.splitSimpleName(this.className);
    }
    /** 获取生成类的完整类名。*/
    public String getClassName() {
        return this.className;
    }
    public void setClassName(String className, String packageName) {
        String _className = (className == null || className.equals("")) ? this.classNameStrategy.generateSimpleName() : className;
        String _packageName = (packageName == null || packageName.equals("")) ? this.classNameStrategy.generatePackageName() : packageName;
        this.className = _packageName + "." + _className;
    }
    public void generateName() {
        this.setClassName(null, null);
    }
    /** 获取生成类的超类(基类)。*/
    public Class<?> getSuperClass() {
        return this.superClass;
    }
    /**设置生成类的基类类型。*/
    public void setSuperClass(Class<?> superClass) {
        if (superClass == null)
            throw new NullPointerException("参数为空。");
        this.superClass = superClass;
    }
    /**设置生成类的基类类型，每次改变基类类型都会导致清空附加实现接口列表同时清空生成的字节码数据。*/
    public void setSuperClass(String superClass, ClassLoader parentLoader) throws ClassNotFoundException {
        this.setSuperClass(parentLoader.loadClass(superClass));
    }
    /**
     * 向类中附加一个接口实现，该接口中的所有方法均通过委托对象代理处理。如果附加的接口中有方法与基类的方法冲突时。
     * appendImpl会丢弃添加接口的冲突方法保留基类方法。这样做相当于基类的方法实现了接口的方法。
     * 如果多次输出一种签名的方法时ClassEngine只会保留最后一次的注册。被输出的方法在类生成时会保留其注解等信息。
     * 如果重复添加同一个接口则该接口将被置于最后一次添加。
     * 注意：如果试图添加一个非接口类型则会引发异常。
     * @param appendInterface 要附加的接口。
     * @param delegate 附加接口的方法处理委托。
     */
    public void addDelegate(Class<?> appendInterface, MethodDelegate delegate) {
        //1.参数判断
        if (appendInterface.isInterface() == false || delegate == null)
            throw new FormatException("参数appendInterface不是一个有效的接口，或者参数delegate为空。");
        //2.测试该接口是否已经得到实现
        try {
            this.superClass.asSubclass(appendInterface);
            return;
        } catch (Exception e) {}
        //3.检测重复,附加接口实现
        if (this.addDelegateMap == null)
            this.addDelegateMap = new LinkedHashMap<Class<?>, MethodDelegate>();
        if (this.addDelegateMap.containsKey(appendInterface) == false)
            this.addDelegateMap.put(appendInterface, delegate);
    }
    /**添加一个AOP过滤器，该过滤器可以重复添加。*/
    public void addAopFilter(AopInvokeFilter filter) {
        if (filter == null)
            return;
        if (this.aopFilters == null)
            this.aopFilters = new ArrayList<AopInvokeFilter>();
        this.aopFilters.add(filter);
    };
    /**添加一个AOP监听器，该监听器可以重复添加。*/
    public void addListener(AopBeforeListener listener) {
        if (listener == null)
            return;
        if (this.aopBeforeListeners == null)
            this.aopBeforeListeners = new ArrayList<AopBeforeListener>();
        this.aopBeforeListeners.add(listener);
    };
    /**添加一个AOP监听器，该监听器可以重复添加。*/
    public void addListener(AopReturningListener listener) {
        if (listener == null)
            return;
        if (this.aopReturningListeners == null)
            this.aopReturningListeners = new ArrayList<AopReturningListener>();
        this.aopReturningListeners.add(listener);
    };
    /**添加一个AOP监听器，该监听器可以重复添加。*/
    public void addListener(AopThrowingListener listener) {
        if (listener == null)
            return;
        if (this.aopThrowingListeners == null)
            this.aopThrowingListeners = new ArrayList<AopThrowingListener>();
        this.aopThrowingListeners.add(listener);
    };
    /**添加一个属性。*/
    public void addProperty(String name, Class<?> type) {
        if (name == null || name.equals("") || type == null)
            throw new NullPointerException("参数name或type为空。");
        if (this.addPropertyMap == null)
            this.addPropertyMap = new LinkedHashMap<String, Class<?>>();
        this.addPropertyMap.put(name, type);
    };
    /**添加一个属性。*/
    public void addProperty(String name, PropertyDelegate<?> delegate) {
        if (name == null || name.equals("") || delegate == null)
            throw new NullPointerException("参数name或delegate为空。");
        if (this.addPropertyDelMap == null)
            this.addPropertyDelMap = new LinkedHashMap<String, PropertyDelegate<?>>();
        this.addPropertyDelMap.put(name, delegate);
    };
    /**获取所有添加的属性名集合*/
    public String[] getAppendPropertys() {
        String[] simpleProp = this.getAppendSimplePropertys();
        String[] delegateProp = this.getAppendDelegatePropertys();
        if (simpleProp == null && delegateProp == null)
            return null;
        //
        if (simpleProp == null)
            simpleProp = new String[0];
        if (delegateProp == null)
            delegateProp = new String[0];
        //
        String[] all = new String[simpleProp.length + delegateProp.length];
        for (int i = 0; i < simpleProp.length; i++)
            all[i] = simpleProp[i];
        int index = simpleProp.length;
        for (int i = 0; i < simpleProp.length; i++)
            all[index + i] = simpleProp[i];
        return all;
    }
    /**获取所有添加的简单属性名*/
    public String[] getAppendSimplePropertys() {
        if (this.addPropertyMap == null || this.addPropertyMap.size() == 0)
            return null;
        String[] strs = new String[this.addPropertyMap.size()];
        this.addPropertyMap.keySet().toArray(strs);
        return strs;
    }
    /**获取所有添加的代理属性名*/
    public String[] getAppendDelegatePropertys() {
        if (this.addPropertyDelMap == null || this.addPropertyDelMap.size() == 0)
            return null;
        String[] strs = new String[this.addPropertyDelMap.size()];
        this.addPropertyDelMap.keySet().toArray(strs);
        return strs;
    }
    /**根据要实现的代理接口获取其代理实现对象。*/
    public MethodDelegate getDelegate(Class<?> impl) {
        if (this.addDelegateMap == null)
            return null;
        return this.addDelegateMap.get(impl);
    }
    /**根据属性名获取其属性类型。*/
    public Class<?> getSimplePropertyType(String name) {
        if (this.addPropertyMap == null)
            return null;
        return this.addPropertyMap.get(name);
    }
    /**根据代理属性名，获取其属性代理类。*/
    public PropertyDelegate<?> getDelegateProperty(String name) {
        if (this.addPropertyDelMap == null)
            return null;
        return this.addPropertyDelMap.get(name);
    }
    /**
     * 获取生成的类所附加实现的接口集合，appendImpl方法可以附加一个新的接口实现。
     * @return 返回生成的类所附加实现的接口集合。appendImpl方法可以附加一个新的接口实现。
     */
    public Class<?>[] getDelegates() {
        if (this.addDelegateMap == null || this.addDelegateMap.size() == 0)
            return null;
        Class<?>[] cl = new Class<?>[this.addDelegateMap.size()];
        this.addDelegateMap.keySet().toArray(cl);
        return cl;
    }
    /**获取其Aop过滤器集合。*/
    public AopInvokeFilter[] getAopFilters() {
        if (this.aopFilters == null || this.aopFilters.size() == 0)
            return null;
        AopInvokeFilter[] aops = new AopInvokeFilter[this.aopFilters.size()];
        this.aopFilters.toArray(aops);
        return aops;
    }
    /**获取before事件监听器。*/
    public AopBeforeListener[] getAopBeforeListeners() {
        if (this.aopBeforeListeners == null || this.aopBeforeListeners.size() == 0)
            return null;
        AopBeforeListener[] listeners = new AopBeforeListener[this.aopBeforeListeners.size()];
        this.aopBeforeListeners.toArray(listeners);
        return listeners;
    }
    /**获取returning事件监听器。*/
    public AopReturningListener[] getAopReturningListeners() {
        if (this.aopReturningListeners == null || this.aopReturningListeners.size() == 0)
            return null;
        AopReturningListener[] listeners = new AopReturningListener[this.aopReturningListeners.size()];
        this.aopReturningListeners.toArray(listeners);
        return listeners;
    }
    /**获取throwing事件监听器。*/
    public AopThrowingListener[] getAopThrowingListeners() {
        if (this.aopThrowingListeners == null || this.aopThrowingListeners.size() == 0)
            return null;
        AopThrowingListener[] listeners = new AopThrowingListener[this.aopThrowingListeners.size()];
        this.aopThrowingListeners.toArray(listeners);
        return listeners;
    }
    public boolean isDebug() {
        return debug;
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    //=======================================================================================Method
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
    };
    /**取消生成状态当再次调用生成时将会启动class构建过程。*/
    public void resetBuilder() {
        this.newClass = null;
        this.newClassBytes = null;
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
    public ClassEngine builderClass() throws ClassNotFoundException, IOException, FormatException {
        if (newClassBytes != null)
            return this;
        //初始化策略
        this.classNameStrategy.initStrategy(this);
        this.delegateStrategy.initStrategy(this);
        this.aopStrategy.initStrategy(this);
        this.propertyStrategy.initStrategy(this);
        this.methodStrategy.initStrategy(this);
        //
        if (EngineToos.checkClassName(this.className) == false)
            throw new FormatException("在生成类的时，检测类名不通过。");
        if (className == null || className.equals("") == true) {
            String packageName = this.classNameStrategy.generatePackageName();
            String simpleName = this.classNameStrategy.generateSimpleName();
            this.className = packageName + simpleName;
        }
        //
        ClassBuilder cb = this.createBuilder(this.builderMode);
        cb.initBuilder(this);
        this.configuration = cb.builderClass();
        if (this.configuration == null)
            throw new StateException("builderClass失败。");
        this.newClassBytes = cb.getClassBytes();
        if (this.debug == false)
            this.newClass = this.loadClass(this.className);//TODO
        //重置策略
        this.classNameStrategy.reset();
        this.delegateStrategy.reset();
        this.aopStrategy.reset();
        this.propertyStrategy.reset();
        this.methodStrategy.reset();
        //
        return this;
    }
    //======================================================================================Builder
    protected ClassBuilder createBuilder(BuilderMode builderMode) {
        return new ClassBuilder();
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equals(this.className) == true)
            return this.defineClass(name, this.newClassBytes, 0, this.newClassBytes.length);
        else
            return super.findClass(name);
    }
    //==========================================================================================New
    public Object newInstance(Object propxyBean) throws FormatException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
        this.builderClass();
        Object obj = null;
        if (this.builderMode == BuilderMode.Super)
            obj = this.newClass.newInstance();
        else
            obj = this.newClass.getConstructor(this.superClass).newInstance(propxyBean);
        return this.configuration.configBean(obj);
    };
}