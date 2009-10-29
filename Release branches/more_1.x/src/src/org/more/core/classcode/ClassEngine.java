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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.more.FormatException;
import org.more.InvokeException;
import org.more.TypeException;
import org.more.core.asm.ClassAdapter;
import org.more.core.asm.ClassReader;
import org.more.core.asm.ClassVisitor;
import org.more.core.asm.ClassWriter;
import org.more.core.asm.Opcodes;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * More项目中ClassCode工具核心类。使用该工具可以根据一个已有类生成一个新的类，并且可以配置
 * 这个类的AOP。除此之外该工具还可以使一个类在运行时附加一个接口实现并且可以正常调用这些接口方法。
 * 所调用的方法均通过附加接口时传递的委托来处理其业务逻辑。
 * Date : 2009-10-15
 * @author 赵永春
 */
public abstract class ClassEngine extends ClassLoader implements Opcodes {
    //================================================================================Builder Const
    static final String                             BuilderClassPrefix    = "$More_";                                      //生成类的类名后缀名
    static final String                             ObjectDelegateMapName = "$delegateMap";                                //生成类中委托映射对象名
    static final String                             DelegateMapUUIDPrefix = "$MoreDelegate_";                              //存放在委托映射中的KEY前缀
    static final String                             DefaultPackageName    = "org.more.core.classcode.test";                //默认类所属包名
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
    //    /** 委托方法映射 */
    //    private Hashtable<String, Method>      delegateMethodMap     = new Hashtable<String, Method>();             //委托方法映射。 
    /** 生成的新类类名尾部的递增标识量 */
    private static long                             builderClassNumber    = 0;
    /** 负责输出日志的日志接口。 */
    private static ILog                             log                   = LogFactory.getLog("org_more_core_classcode");
    //    //AOP相关数据
    //    private MethodInvokeFilter             methodFilter      = null;                                        //实现AOP的方法调用过滤器
    //==================================================================================Constructor
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类。 */
    public ClassEngine() {
        this(ClassEngine.DefaultPackageName + ".Object" + ClassEngine.getPrefix(), Object.class, Thread.currentThread().getContextClassLoader());
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类。 */
    public ClassEngine(String className, Class<?> superClass) {
        this(className, superClass, Thread.currentThread().getContextClassLoader());
    }
    /** 创建一个ClassEngine类型对象，默认生成的类是Object的子类。 */
    public ClassEngine(ClassLoader parentLoader) {
        this(ClassEngine.DefaultPackageName + ".Object" + ClassEngine.getPrefix(), Object.class, parentLoader);
    }
    /** 使用指定类名创建一个ClassEngine类型对象，如果指定的类名是空则采用Object作为父类。 */
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
    //==========================================================================================Job
    /**
     * 向类中附加一个接口实现。该接口中的所有方法均通过委托对象代理处理。如果附加的接口中有方法与基类的方法冲突时。
     * appendImpl会丢弃添加接口的冲突方法保留基类方法。这样做相当于基类的方法实现了接口的方法。
     * 如果多次输出一种签名的方法时ClassEngine只会保留最后一次的注册。被输出的方法在类生成时会保留其注解等信息。
     * 如果重复添加同一个接口则该接口将被置于最后一次添加。
     * 注意：如果试图添加一个非接口类型则会引发异常。
     * @param interfaceName 要附加的接口。
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
     * 生成并且创建这个类对象，同时装配新对象的代理对象。如果该类对象还没有生成则会引发生成操作。
     * @return 返回创建并且装配完的类对象。
     * @throws InvokeException 调用toClass时候发生异常。
     */
    public Object newInstance() throws Exception {
        //1.创建对象
        log.debug("newInstance this class!");
        Object obj = this.toClass().newInstance();
        Hashtable<String, Method> map = new Hashtable<String, Method>();
        //2.准备注入数据
        for (Class<?> type : this.impls.keySet()) {
            Method m = new Method();
            m.uuid = ClassEngine.DelegateMapUUIDPrefix + UUID.randomUUID().toString().replace("-", "");
            m.delegate = this.impls.get(type);
            map.put(type.getName(), m);
        }
        //3.执行注入
        Field field = obj.getClass().getField(ClassEngine.ObjectDelegateMapName);
        field.set(obj, map);
        String className = obj.getClass().getName();
        log.debug("configure object [" + className + "]" + obj);
        return obj;
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
        log.debug("ready [OK]!");
        //------------------------------三、使用代理拦截所有方法
        BuilderClassAdapter builderAdapter = new BuilderClassAdapter(this, acceptVisitor, this.superClass, this.impls);
        //------------------------------四、调用ClassReader引擎解析原始类
        ClassAdapter ca = new ClassAdapter(builderAdapter);
        reader.accept(ca, ClassReader.SKIP_DEBUG);
        //------------------------------五、取得生成的字节码
        log.debug("generated Class [OK]! get ByteCode");
        this.classByte = writer.toByteArray();
        //------------------------------六、对生成的类对象附加AOP功能
        //
        //------------------------------七、生成Class对象
        //this.classByte = this.classByte;
        this.classType = this.loadClass(this.className);
    }
    /** 子类决定最终的ClassAdapter，子类ClassAdapter可以接收到附加接口相关的visit。 */
    protected abstract ClassAdapter acceptClass(final ClassWriter classWriter);
}