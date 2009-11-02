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
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.more.FormatException;
import org.more.TypeException;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassReader;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.ClassWriter;
import org.more.core.asm.Opcodes;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 字节码生成工具，该工具可以在已有类型上附加接口实现，使用ClassEngine还可以对类对象提供AOP的支持。ClassEngine提供了两种工作模式。<br/>
 * <b>继承方式</b>--继承方式实现新类，这种生成模式下必须要求先有类型后有对象。生成的新类是继承原有类实现的，
 * 所有附加方法都写到新类中。原始类中的所有方法都被重写并且以super形式调用父类。私有方法不包括重写范畴。
 * 私有方法将不参与AOP功能。在继承模式下保护方法与公共方法参与AOP功能。<br/>
 * <b>代理方式</b>--代理方式实现新类，这种生成模式下可以在已有的对象上附加接口实现而不需要重新创建对象。同时生成的新对象
 * 不破坏原有对象。整个实现方式就是一个静态代理方式实现。注意这种生成方式会取消所有原始类中的构造方法。
 * 取而代之的是生成一个一个参数的构造方法，该参数类型就是基类类型。所有方法调用都使用这个注入的类型对象调用。
 * 同时该中生成方式的私有方法不包括重写范畴。<br/>
 * 在代理模式下只有公共方法参与AOP功能，私有方法和受保护的方法因访问权限问题不能参与AOP。<br/>
 * <b>AOP特性</b>--ClassEngine引擎的AOP特性是可以配置是否启用的。如果附加AOP相关功能则字节码在生成时除了经过了第一次接口附加操作之后
 * 还需要经过第二次AOP特性加入。所有本类方法包括可以继承的方法均被重写。启用AOP特性会少量增加字节码体积同时也比不使用AOP特性的运行效率要慢些。
 * <br/>Date : 2009-10-15
 * @author 赵永春
 */
public class ClassEngine extends ClassLoader implements Opcodes {
    //=================================================================================Builder Type
    /** ClassEngine引擎类生成模式。 */
    public enum BuilderMode {
        /** 
         * 继承方式实现新类，这种生成模式下必须要求先有类型后有对象。生成的新类是继承原有类实现的，
         * 所有附加方法都写到新类中。原始类中的所有方法都被重写并且以super形式调用父类。私有方法不包括重写范畴。
         * 私有方法将不参与AOP功能。在继承模式下保护方法与公共方法参与AOP功能。
         */
        Super,
        /** 
         * 代理方式实现新类，这种生成模式下可以在已有的对象上附加接口实现而不需要重新创建对象。同时生成的新对象
         * 不破坏原有对象。整个实现方式就是一个静态代理方式实现。注意这种生成方式会取消所有原始类中的构造方法。
         * 取而代之的是生成一个一个参数的构造方法，该参数类型就是基类类型。所有方法调用都使用这个注入的类型对象调用。
         * 同时该中生成方式的私有方法不包括重写范畴。<br/>
         * 在代理模式下只有公共方法参与AOP功能，私有方法和受保护的方法因访问权限问题不能参与AOP。
         */
        Propxy
    }
    //================================================================================Builder Const
    static final String                             BuilderClassPrefix    = "$More_";                                      //生成类的类名后缀名
    static final String                             DefaultPackageName    = "org.more.core.classcode.test";                //默认类所属包名
    static final String                             ObjectDelegateMapName = "$More_DelegateMap";                           //生成类中委托映射对象名
    static final String                             DelegateMapUUIDPrefix = "$More_DelegatePrefix_";                       //存放在委托映射中的KEY前缀
    static final String                             PropxyModeObjectName  = "$More_PropxyObject";                          //代理方式实现时代理对象在类中的名称
    static final String                             AOPFilterChainName    = "$More_AOPFilterChain";                        //AOP过滤器对象名称
    static final String                             AOPMethodNamePrefix   = "$More_";                                      //AOP方法的原始方法名前缀
    //========================================================================================Field
    /** 生成的新类类名 */
    private String                                  className             = null;
    /** 生成的新类类型 */
    private Class<?>                                classType             = null;
    /** 生成的新类的字节码 */
    private byte[]                                  classByte             = null;
    /** 生成类的基类类型 */
    private Class<?>                                superClass            = Object.class;
    /** 生成的新类所附加实现的接口 */
    private LinkedHashMap<Class<?>, MethodDelegate> impls                 = new LinkedHashMap<Class<?>, MethodDelegate>(0);
    /** 生成模式，默认是继承方式 */
    private ClassEngine.BuilderMode                 mode                  = ClassEngine.BuilderMode.Super;
    /** 是否对生成类进行AOP封装。 */
    private boolean                                 enableAOP             = true;
    /** 生成的新类类名尾部的递增标识量 */
    private static long                             builderClassNumber    = 0;
    /** 负责输出日志的日志接口。 */
    private static ILog                             log                   = LogFactory.getLog("org_more_core_classcode");
    /**实现AOP的方法调用过滤器组合。*/
    private ImplAOPFilterChain                      invokeFilterChain     = null;
    //
    /**保存经过AOP代理之后的方法和代理方法。*/
    LinkedHashMap<String, AOPMethods>               aopMethods            = new LinkedHashMap<String, AOPMethods>(0);
    //==================================================================================Constructor
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类，使用的是当前线程的ClassLoader类装载对象作为父装载器。 */
    public ClassEngine() {
        this(ClassEngine.DefaultPackageName + ".Object" + ClassEngine.getPrefix(), Object.class, Thread.currentThread().getContextClassLoader());
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类，使用的是当前线程的ClassLoader类装载对象作为父装载器。 */
    public ClassEngine(String className, Class<?> superClass) {
        this(className, superClass, Thread.currentThread().getContextClassLoader());
    }
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类，同时指定类装载器。 */
    public ClassEngine(ClassLoader parentLoader) {
        this(ClassEngine.DefaultPackageName + ".Object" + ClassEngine.getPrefix(), Object.class, parentLoader);
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类，同时指定类装载器。 */
    public ClassEngine(String className, Class<?> superClass, ClassLoader parentLoader) {
        super(parentLoader);
        if (className == null || className.equals("") || superClass == null)
            throw new TypeException("必须指定className和superClass参数。并且className不能为空字符串。");
        this.setNewClass(className, superClass);
    }
    //======================================================================================Get/Set
    /** 获取生成类名的后缀编号，每一次调用该方法都回返回一个新的后缀名。 */
    private static synchronized String getPrefix() {
        String prefix = ClassEngine.BuilderClassPrefix + ClassEngine.builderClassNumber;
        ClassEngine.builderClassNumber++;//全局类编号增加1
        return prefix;
    }
    /** 生成类名，检测父类等信息。 */
    private void setNewClass(String newClassName, Class<?> newClass) {
        //1.格式正确判断
        if (EngineToos.checkClassName(newClassName) == false)
            throw new FormatException("类名[" + newClassName + "]不是一个格式良好的类名。");
        if (newClass.isEnum() == true || newClass.isAnnotation() == true || newClass.isInterface() == true || //
                EngineToos.checkIn(newClass.getModifiers(), Modifier.ABSTRACT) == true || EngineToos.checkIn(newClass.getModifiers(), Modifier.FINAL) == true)
            throw new FormatException("基类[" + newClass + "]不是一个受支持的类。注意基类不可以是如下类型[enum、abstract、interface、final、annotation].");
        //2.如果为代理模式需要基类中必须存在一个公共的无参构造函数。
        if (this.mode == BuilderMode.Propxy) {
            try {
                newClass.getConstructor();
            } catch (Exception e) {
                throw new FormatException("使用代理模式生成类，基类必须拥有一个无参的构造函数。");
            }
        }
        //2.确定基类
        this.superClass = newClass;
        this.className = newClassName;
        log.debug("builderClass the superClass=" + this.superClass + " ,className=" + this.className);
        //3.初始化字节码信息
        if (this.classByte != null) {
            this.resetByte();
            this.impls.clear();
        }
    }
    /**
     * 获取生成的类完整限定名中类名部分。
     * @return 返回生成的类完整限定名中类名部分。
     */
    public String getSimpleName() {
        return EngineToos.splitSimpleName(this.className);
    }
    /**
     * 获取生成类的完整类名。
     * @return 返回生成类的完整类名。
     */
    public String getClassName() {
        return this.className;
    }
    /**
     * 获取生成类的超类(基类)
     * @return 返回生成类的超类(基类)
     */
    public Class<?> getSuperClass() {
        return this.superClass;
    }
    /**
     * 设置生成类的基类类型。每次改变基类类型都会导致清空附加实现接口列表同时清空生成的字节码数据。
     * @param type 生成类的基类类型
     */
    public void setSuperClass(Class<?> type) {
        this.setNewClass(this.className, type);
    }
    /**
     * 设置新类的类名如果设置的新类名为null则新的类名是基类名尾部加上$More_&lt;n&gt;n是一个自动编号。
     * 每次改变基类类型都会导致清空附加实现接口列表同时清空生成的字节码数据。
     * @param className 要设置的类名。
     */
    public void setClassName(String className) {
        this.setNewClass(className, this.superClass);
    }
    /**
     * 获取生成的类所附加实现的接口集合。appendImpl方法可以附加一个新的接口实现。
     * @return 返回生成的类所附加实现的接口集合。appendImpl方法可以附加一个新的接口实现。
     */
    public Class<?>[] getAppendImpls() {
        Class<?>[] cl = new Class<?>[this.impls.size()];
        this.impls.keySet().toArray(cl);
        return cl;
    }
    /**
     * 获取当前引擎的生成模式。生成模式由ClassEngine.BuilderMode枚举决定。默认生成模式是ClassEngine.BuilderMode.Super
     * @return 返回当前引擎的生成模式。生成模式由ClassEngine.BuilderMode枚举决定。
     */
    public ClassEngine.BuilderMode getMode() {
        return mode;
    }
    /**
     * 设置当前引擎的生成模式，生成模式由ClassEngine.BuilderMode枚举定义。默认的生成模式是ClassEngine.BuilderMode.Super
     * 如果设置了新的生成模式则会引发ClassEngine的字节码初始化操作。引发了初始化操作字节码需要重新生成。
     * @param mode 设置的新生成模式。
     */
    public void setMode(ClassEngine.BuilderMode mode) {
        if (this.mode == mode)
            return;
        this.mode = mode;
        this.setNewClass(this.className, this.superClass);
    }
    /**
     * 获取引擎当前是否在生成类时候将AOP特性加入。加入AOP特性会增加额外的字节码操作着会比没有AOP特性的新类运行效率要底。 默认是启用AOP特性的。
     * @return 返回引擎当前是否在生成类时候将AOP特性。
     */
    public boolean isEnableAOP() {
        return enableAOP;
    }
    /**
     * 设置引擎当前是否在生成类时候将AOP特性加入。加入AOP特性会增加额外的字节码操作着会比没有AOP特性的新类运行效率要底。 默认是启用AOP特性的。
     * @param enableAOP true表示启用AOP特性(默认)，false表示不使用AOP特性。
     */
    public void setEnableAOP(boolean enableAOP) {
        this.enableAOP = enableAOP;
        this.setNewClass(this.className, this.superClass);
    }
    //==========================================================================================Job
    /**
     * 向类中附加一个接口实现。该接口中的所有方法均通过委托对象代理处理。如果附加的接口中有方法与基类的方法冲突时。
     * appendImpl会丢弃添加接口的冲突方法保留基类方法。这样做相当于基类的方法实现了接口的方法。
     * 如果多次输出一种签名的方法时ClassEngine只会保留最后一次的注册。被输出的方法在类生成时会保留其注解等信息。
     * 如果重复添加同一个接口则该接口将被置于最后一次添加。
     * 注意：如果试图添加一个非接口类型则会引发异常。
     * @param appendInterface 要附加的接口。
     * @param delegate 附加接口的方法处理委托。
     */
    public void appendImpl(Class<?> appendInterface, MethodDelegate delegate) {
        //1.参数判断
        if (appendInterface.isInterface() == false || delegate == null)
            throw new FormatException("参数appendInterface不是一个有效的接口，或者参数delegate为空。");
        //2.测试该接口是否已经得到实现
        try {
            this.superClass.asSubclass(appendInterface);
            return;
        } catch (Exception e) {}
        //3.检测重复
        if (this.impls.containsKey(appendInterface) == true)
            this.impls.remove(appendInterface);
        //4.附加接口实现
        this.setNewClass(this.className, this.superClass);
        this.impls.put(appendInterface, delegate);
    }
    /**
     * 设置AOP回调方法链，参数是一个过滤器集合。在执行过滤器链的时候是依照如下顺序进行执行4321。
     * 就是说越靠近数组前端的对象在过滤器链中的位置就越靠后。在过滤器执行完毕返回是否执行顺序是1234。
     * @param filters 要设置的过滤器链数组。
     */
    public void setCallBacks(AOPInvokeFilter[] filters) {
        if (filters == null)
            return;
        ImplAOPFilterChain filterChain = new ImplAOPFilterChain(null, null);
        for (AOPInvokeFilter thisFilter : filters)
            filterChain = new ImplAOPFilterChain(thisFilter, filterChain);
        this.invokeFilterChain = filterChain;
    }
    //==================================================================================newInstance
    /** 清空生成的字节码数据。 */
    public void resetByte() {
        this.classByte = null;
        this.classType = null;;
    }
    /**
     * 获取已经生成的字节码数据，如果类还没有经过生成则该方法将导致生成操作。生成方法是builderClass();
     * @return 返回生成的字节码数据。
     * @throws IOException 在调用builderClass时候发生异常。通常发生IO异常是无法读取基类类型或者附加接口类型。
     * @throws ClassNotFoundException 在调用builderClass时候发生异常。通常该类型异常是生成的类格式出现错误或者无法装载新类。
     */
    public byte[] toBytes() throws IOException, ClassNotFoundException {
        if (this.classByte == null)
            this.builderClass();
        return this.classByte;
    };
    /**
     * 获取已经生成的类对象，如果类还没有经过生成则该方法将导致生成操作。生成方法是builderClass();
     * @return 返回生成的字节码数据。
     * @throws IOException 在调用builderClass时候发生异常。通常发生IO异常是无法读取基类类型或者附加接口类型。
     * @throws ClassNotFoundException 在调用builderClass时候发生异常。通常该类型异常是生成的类格式出现错误或者无法装载新类。
     */
    public Class<?> toClass() throws IOException, ClassNotFoundException {
        if (this.classType == null)
            this.builderClass();
        return this.classType;
    };
    /**
     * 使用默认构造方法创建新的类对象并且装配这个新对象。如果该类对象还没有生成则会引发生成操作。此外当ClassEngine工作在代理模式时
     * 必须指定参数superClassObject，当运行在继承模式时该参数将被忽略。
     * @return 返回创建并且装配完的类对象。
     */
    public Object newInstance(Object superClassObject) throws Exception {
        //1.创建对象
        log.debug("newInstance this class!");
        Object obj = null;
        if (this.mode == ClassEngine.BuilderMode.Super)
            //Super
            obj = this.toClass().newInstance();
        else
            obj = this.toClass().getConstructor(this.superClass).newInstance(superClassObject);
        return this.configuration(obj);
    }
    /**
     * 对某个新生成的类对象执行装配。ClassEngine生成的新类中可能存在附加接口。这些附加的接口委托在新的类中是不存在的。
     * 因此需要装配，否则当调用这些附加接口方法时会产生空指针异常。configuration就是装配这个新对象的方法。注意：生成的类对象应当使用
     * 对应的ClassEngine进行装配。装配过程就是将委托对象注入到新类对象中的过程。另外还需要注意的是如果ClassEngine在创建完第一批类对象
     * 之后修改了基类等信息重新生成了新的类对象。那么第一批类对象将永远不能在执行装配过程。
     * @param newAsmObject 要装配的类对象。
     * @return 返回装配之后的类对象
     * @throws Exception 如果在装配期间发生异常。
     */
    public Object configuration(Object newAsmObject) throws Exception {
        //1.如果目标要装配的对象不属于当前引擎生成的类对象则取消装配过程并且返回null;
        if (newAsmObject.getClass() != this.classType)
            return null;
        //3.执行装配过程，准备注入数据
        Hashtable<String, Method> map = new Hashtable<String, Method>();
        for (Class<?> type : this.impls.keySet()) {
            Method m = new Method();
            m.uuid = ClassEngine.DelegateMapUUIDPrefix + UUID.randomUUID().toString().replace("-", "");
            m.delegate = this.impls.get(type);
            map.put(type.getName(), m);
        }
        //4.执行代理注入
        java.lang.reflect.Method m1 = newAsmObject.getClass().getMethod("set" + ClassEngine.ObjectDelegateMapName, Hashtable.class);
        m1.invoke(newAsmObject, map);
        //5.装配AOP链
        if (this.enableAOP == true && this.invokeFilterChain != null) {
            java.lang.reflect.Method m2 = this.classType.getMethod("set" + ClassEngine.AOPFilterChainName, ImplAOPFilterChain.class);
            m2.invoke(newAsmObject, this.invokeFilterChain);
        }
        log.debug("configure object [" + this.className + "]");
        return newAsmObject;
    }
    //=================================================================================BuilderClass
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equals(this.className) == true)
            return this.defineClass(name, this.classByte, 0, this.classByte.length);
        else
            return super.findClass(name);
    }
    /**
     * 生成类的字节码
     * @throws IOException 通常发生IO异常是无法读取基类类型或者附加接口类型。
     * @throws ClassNotFoundException 通常该类型异常是生成的类格式出现错误或者无法装载新类。
     */
    public synchronized void builderClass() throws IOException, ClassNotFoundException {
        //------------------------------一、确认是否进行生成字节码 
        if (this.classByte != null) {
            //如果已经生成了类的字节码则返回生成的字节码
            log.debug("Byte-code has been generated! return this method");
            return;
        } else
            log.debug("builderClass!");
        //------------------------------二、准备生成字节码的相关数据
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        InputStream inStream = EngineToos.getClassInputStream(this.superClass);//获取输入流
        ClassReader reader = new ClassReader(inStream);//创建ClassReader
        log.debug("ready builderClass stream=" + inStream + ", reader=" + reader + ", writer=" + writer);
        ClassVisitor acceptVisitor = this.acceptClass(writer);
        if (acceptVisitor == null) {
            log.debug("ready [Error] method acceptClass return null!");
            acceptVisitor = new ClassAdapter(writer);
        }
        //------------------------------三、对生成的类对象附加AOP功能
        if (this.enableAOP == true && this.invokeFilterChain != null)
            acceptVisitor = new AOPClassAdapter(acceptVisitor, this);
        log.debug("ready [OK]!");
        //------------------------------四、使用代理拦截所有方法
        BuilderClassAdapter builderAdapter = new BuilderClassAdapter(this, acceptVisitor, this.superClass, this.impls);
        //------------------------------五、调用ClassReader引擎解析原始类，并生成新对象
        ClassAdapter ca = new ClassAdapter(builderAdapter);
        reader.accept(ca, ClassReader.SKIP_DEBUG);
        log.debug("generated Class [OK]! get ByteCode");
        //------------------------------六、取得生成的字节码
        this.classByte = writer.toByteArray();
        this.classType = this.loadClass(this.className);
        //
        for (java.lang.reflect.Method m : this.classType.getDeclaredMethods()) {
            String desc = EngineToos.toAsmType(m.getParameterTypes());
            String returnDesc = EngineToos.toAsmType(m.getReturnType());
            String fullName = m.getName() + "(" + desc + ")" + returnDesc;
            if (this.aopMethods.containsKey(fullName) == true) {
                java.lang.reflect.Method m1 = EngineToos.getMethod(this.classType, this.AOPMethodNamePrefix + m.getName(), m.getParameterTypes());
                this.aopMethods.put(fullName, new AOPMethods(m1, m));
            }
        }
    }
    /** 子类决定最终的ClassAdapter，子类ClassAdapter可以更自由的控制字节码注意子类在重写该方法时一定要使用classWriter作为最终的字节码输出对象。*/
    protected ClassAdapter acceptClass(final ClassWriter classWriter) {
        return null;
    };
    /** 内部忽略方法*/
    boolean ignoreMethod(String fullDesc) {
        String[] ignoreMethod = new String[2];
        ignoreMethod[0] = "set" + ClassEngine.ObjectDelegateMapName + "(Ljava/util/Hashtable;)V";
        ignoreMethod[1] = "set" + ClassEngine.AOPFilterChainName + "(Lorg/more/core/classcode/ImplAOPFilterChain;)V";
        for (String n : ignoreMethod)
            if (n.equals(fullDesc) == true)
                return false;
        //构造方法
        if (fullDesc.indexOf("<init>") != -1)
            return false;
        return true;
    };
    /** 子类决定发现的这个方法是否处于AOP方法。*/
    protected boolean acceptMethod(java.lang.reflect.Method method) {
        return true;
    };
}