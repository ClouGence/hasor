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
package org.more.hypha.beans.assembler.factory;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.more.DoesSupportException;
import org.more.InitializationException;
import org.more.InvokeException;
import org.more.RepeateException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.AbstractMethodDefine;
import org.more.hypha.beans.AbstractPropertyDefine;
import org.more.hypha.beans.ValueMetaData;
import org.more.hypha.beans.ValueMetaDataParser;
import org.more.hypha.beans.assembler.a.AfterCreateExpandPoint;
import org.more.hypha.beans.assembler.a.BeforeCreateExpandPoint;
import org.more.hypha.beans.assembler.a.ClassByteExpandPoint;
import org.more.hypha.beans.assembler.a.ClassTypeExpandPoint;
import org.more.hypha.beans.assembler.a.DecoratorExpandPoint;
import org.more.util.attribute.IAttribute;
/**
 * 该类的职责是负责将{@link AbstractBeanDefine}转换成类型或者Bean实体对象。
 * @version 2011-1-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class BeanEngine {
    private Map<String, BeanBuilder> beanBuilderMap     = new HashMap<String, BeanBuilder>();
    //
    private IAttribute               flashContext       = null;
    private ApplicationContext       applicationContext = null;
    //
    private Map<String, Object>      beanCache          = new Hashtable<String, Object>();
    private EngineClassLoader        classLoader        = null;
    private RootValueMetaDataParser  rootMetaDataParser = new RootValueMetaDataParser();
    private ByteCodeCache            byteCodeCache      = null;
    //----------------------------------------------------------------------------------------------------------
    public BeanEngine(ApplicationContext applicationContext, IAttribute flashContext) {
        this.flashContext = flashContext;
        this.applicationContext = applicationContext;
        this.classLoader = new EngineClassLoader(applicationContext.getBeanClassLoader());//使用EngineClassLoader装载构造的字节码
    };
    //----------------------------------------------------------------------------------------------------------
    class EngineClassLoader extends ClassLoader {
        public EngineClassLoader(ClassLoader parent) {
            super(parent);
        };
        public Class<?> loadClass(byte[] beanBytes, AbstractBeanDefine define) {
            //如果不传递要装载的类名JVM就不会调用本地的类检查器去检查这个类是否存在。
            return this.defineClass(beanBytes, 0, beanBytes.length);
        };
    };
    //----------------------------------------------------------------------------------------------------------
    /**该方法的返回值决定是否忽略所有扩展点的执行，flase表示不忽略(默认值)。*/
    protected boolean isIgnorePoints() {
        return false;
    };
    /**该方法的返回值决定了是否忽略执行生命周期方法，false表示不忽略(默认值)。*/
    protected boolean isIgnoreLifeMethod() {
        return false;
    };
    /**获取字节码缓存器对象，该缓存器可以缓存*/
    protected ByteCodeCache getByteCodeCache() {
        return this.byteCodeCache;
    };
    //----------------------------------------------------------------------------------------------------------
    /***/
    public void init() {
        this.beanCache.clear();
        this.byteCodeCache.clearCache();
    };
    /**
     * 注册一种bean定义类型，使之可以被引擎解析。如果重复注册同一种bean类型将会引发{@link RepeateException}类型异常。
     * @param beanType 注册的bean定义类型。
     * @param builder 要注册的bean定义生成器。
     */
    public void regeditBeanBuilder(String beanType, BeanBuilder builder) throws RepeateException {
        if (this.beanBuilderMap.containsKey(beanType) == false)
            this.beanBuilderMap.put(beanType, builder);
        else
            throw new RepeateException("不能重复注册[" + beanType + "]类型的BeanBuilder。");
    };
    /**解除指定类型bean的解析支持，无论要接触注册的bean类型是否存在该方法都会被正确执行。*/
    public void unRegeditBeanBuilder(String beanType) {
        if (this.beanBuilderMap.containsKey(beanType) == true)
            this.beanBuilderMap.remove(beanType);
    };
    /**清理掉{@link BeanEngine}对象中所缓存的单例Bean对象。*/
    public void clearBeanCache() {
        this.beanCache.clear();
    };
    /**获取一个int该int表示了{@link BeanEngine}对象中已经缓存了的单例对象数目。*/
    public int getCacheBeanCount() {
        return this.beanCache.size();
    };
    /**
     * 注册一种{@link ValueMetaData}元信息解析器，使之可以被属性解析器解析。如果重复注册同一种元信息解析器将会引发{@link RepeateException}类型异常。
     * @param metaDataType 要注册的元信息类型。
     * @param parser 解析器
     */
    public void regeditMetaDataParser(String metaDataType, ValueMetaDataParser<?> parser) throws RepeateException {
        this.rootMetaDataParser.addParser(metaDataType, parser);
    };
    /**解除一种属性解析器的注册。*/
    public void unRegeditMetaDataParser(String metaDataType) {
        this.rootMetaDataParser.removeParser(metaDataType);
    };
    //----------------------------------------------------------------------------------------------------------
    /**
     * 将{@link AbstractBeanDefine}定义对象解析并且装载成为Class类型对象，
     * 期间会一次引发{@link ClassByteExpandPoint}和{@link ClassTypeExpandPoint}两个扩展点。
     * @param define 要装载类型的bean定义。
     * @return 返回装载的bean类型。
     */
    public synchronized Class<?> builderType(AbstractBeanDefine define) throws DoesSupportException, InitializationException {
        String defineType = define.getBeanType();
        String defineID = define.getID();
        ExpandPointManager epm = this.applicationContext.getExpandPointManager();
        //
        BeanBuilder builder = this.beanBuilderMap.get(defineType);
        if (builder == null)
            throw new DoesSupportException("hypha 不支持的Bean定义类型：" + defineType);
        //1.确定该类型bean是否可以被装载成为java类型。
        if (builder.canbuilder() == false)
            throw new DoesSupportException(defineID + "，的类型定义不能执行装载过程。");
        //--------------------------------------------------------------------------------------------------------------准备阶段
        //2.装载class
        ByteCodeCache cache = this.getByteCodeCache();
        byte[] beanBytes = null;
        if (builder.canCache() == true)
            beanBytes = cache.loadCode(defineID);//试图从缓存中装载
        if (beanBytes == null) {
            beanBytes = builder.loadBeanBytes(define);
            if (this.isIgnorePoints() == false)
                //如果配置决定不忽略扩展点则执行扩展点代码
                beanBytes = (byte[]) epm.exePoint(ClassByteExpandPoint.class, new Object[] { beanBytes, define, this.applicationContext });
            cache.saveCode(defineID, beanBytes);//缓存字节码信息
        }
        if (beanBytes == null)
            throw new NullPointerException("由于无法获取字节码信息，所以无法转换Bean定义成为类型。");
        //3.装载Class类，如果装载不了hypha不会强求
        Class<?> beanType = this.classLoader.loadClass(beanBytes, define);
        if (this.isIgnorePoints() == false)
            //如果配置决定不忽略扩展点则执行扩展点代码
            beanType = (Class<?>) epm.exePoint(ClassTypeExpandPoint.class, new Object[] { beanType, define, this.applicationContext });
        if (beanType == null)
            throw new NullPointerException("丢失Bean类型定义，请检查各扩展点是否正常返回类型。");
        return beanType;
    };
    /**
     * 调用引擎创建过程去生成bean代码。
     * @param define
     * @param params
     * @return
     */
    public synchronized Object builderBean(AbstractBeanDefine define, Object[] params) {
        String defineID = define.getID();
        Class<?> beanType = this.builderType(define);
        ExpandPointManager epm = this.applicationContext.getExpandPointManager();
        BeanBuilder builder = this.beanBuilderMap.get(define.getBeanType());
        //--------------------------------------------------------------------------------------------------------------检查单态
        //0.单态
        if (this.beanCache.containsKey(defineID) == true)
            return this.beanCache.get(defineID);
        //--------------------------------------------------------------------------------------------------------------创建阶段
        //1.预创建Bean
        Object obj = null;
        if (this.isIgnorePoints() == false)
            obj = epm.exePoint(BeforeCreateExpandPoint.class, new Object[] { beanType, params, define, this.applicationContext });
        //2.如果没有预创建的对象则执行系统默认的创建过程.
        if (obj == null) {
            AbstractMethodDefine factory = define.factoryMethod();
            Collection<? extends AbstractPropertyDefine> initParam = null;
            if (factory != null) {
                //1.工厂方式
                initParam = factory.getParams();
                //TODO
                //
            } else {
                //2.平常方式
                initParam = define.getInitParams();
                //TODO
                //
            }
        }
        obj = builder.builderBean(obj, define);
        //3.执行创建的后续操作
        if (this.isIgnorePoints() == false)
            //如果配置决定不忽略扩展点则执行扩展点代码
            obj = epm.exePoint(AfterCreateExpandPoint.class, new Object[] { obj, params, define, this.applicationContext });
        if (obj == null)
            throw new InvokeException("创建[" + defineID + "]，异常不能正常创建或者，装饰的扩展点返回为空。");
        //--------------------------------------------------------------------------------------------------------------初始化阶段
        //4.装饰
        if (this.isIgnorePoints() == false)
            //如果配置决定不忽略扩展点则执行扩展点代码
            obj = epm.exePoint(DecoratorExpandPoint.class, new Object[] { obj, params, define, this.applicationContext });
        //5.执行初始化
        Class<?> objType = obj.getClass();
        String initMethodName = define.getInitMethod();
        if (initMethodName != null)
            try {
                Method m = objType.getMethod(initMethodName, Object[].class);
                m.invoke(obj, params);//执行初始化
            } catch (Exception e) {
                throw new InitializationException(e);
            }
        //6.代理销毁方法
        //ProxyFinalizeClassEngine ce = new ProxyFinalizeClassEngine(this);
        //ce.setBuilderMode(BuilderMode.Propxy);
        //ce.setSuperClass(objType);
        //obj = ce.newInstance(obj);
        //8.单态缓存&类型匹配
        obj = this.cast(beanType, obj);
        if (define.isSingleton() == true)
            this.beanCache.put(defineID, obj);
        return obj;
    };
    /**检测对象类型是否匹配定义类型，如果没有指定beanType参数则直接返回。*/
    private Object cast(Class<?> beanType, Object obj) {
        if (beanType != null)
            return beanType.cast(obj);
        return obj;
    };
};
//class ProxyFinalizeClassEngine extends ClassEngine {
//    private final ProxyFinalizeClassBuilder builder = new ProxyFinalizeClassBuilder();
//    public ProxyFinalizeClassEngine(BeanEngine beanEngine) throws ClassNotFoundException {
//        super(false);
//    };
//    protected ClassBuilder createBuilder(BuilderMode builderMode) {
//        return this.builder;
//    };
//    public Object newInstance(Object propxyBean) throws FormatException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
//        Object obj = super.newInstance(propxyBean);
//        //this.toClass().getMethod("", parameterTypes);
//        return obj;
//    };
//};
//class ProxyFinalizeClassBuilder extends ClassBuilder {
//    protected ClassAdapter acceptClass(ClassWriter classVisitor) {
//        return new ClassAdapter(classVisitor) {
//            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//                if (name.equals("finalize()V") == true)
//                    System.out.println();
//                return super.visitMethod(access, name, desc, signature, exceptions);
//            }
//        };
//    }
//};