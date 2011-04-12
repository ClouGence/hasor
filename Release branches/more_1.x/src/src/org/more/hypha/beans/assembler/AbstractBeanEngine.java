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
package org.more.hypha.beans.assembler;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.more.ClassFormatException;
import org.more.DoesSupportException;
import org.more.RepeateException;
import org.more.core.ognl.OgnlContext;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.AbstractExpandPointManager;
import org.more.hypha.AbstractMethodDefine;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.util.attribute.IAttribute;
/**
* 该类的职责是负责将{@link AbstractBeanDefine}转换成类型或者Bean实体对象。
* 该类是一个抽象类，在使用时需要通过子类给定其{@link AbstractExpandPointManager}对象。
* @version 2011-1-13
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class AbstractBeanEngine {
    private ApplicationContext                                   applicationContext = null;
    private Map<String, AbstractBeanBuilder<AbstractBeanDefine>> beanBuilderMap     = new HashMap<String, AbstractBeanBuilder<AbstractBeanDefine>>();
    //
    private EngineClassLoader                                    classLoader        = null;
    private Map<String, Object>                                  singleBeanCache    = new Hashtable<String, Object>();
    //----------------------------------------------------------------------------------------------------------
    /** 该类的职责是负责将{@link AbstractBeanDefine}转换成类型或者Bean实体对象。该类是一个抽象类，在使用时需要通过子类给定其{@link AbstractExpandPointManager}对象。*/
    public AbstractBeanEngine(ApplicationContext applicationContext, IAttribute flashContext) {
        this.applicationContext = applicationContext;
        this.classLoader = new EngineClassLoader(this.applicationContext.getBeanClassLoader());//使用EngineClassLoader装载构造的字节码
    };
    //----------------------------------------------------------------------------------------------------------
    class EngineClassLoader extends ClassLoader {
        public EngineClassLoader(ClassLoader parent) {
            super(parent);
        };
        public Class<?> loadClass(byte[] beanBytes, AbstractBeanDefine define) throws ClassFormatException {
            //如果不传递要装载的类名JVM就不会调用本地的类检查器去检查这个类是否存在。
            return this.defineClass(beanBytes, 0, beanBytes.length);
        };
    };
    //----------------------------------------------------------------------------------------------------------
    /**返回配置文件的输入流列表。*/
    protected abstract List<InputStream> getConfigStreams() throws IOException;
    /**获取扩展点管理器，子类需要重写该方法来提供引擎扩展点管理器。*/
    protected abstract AbstractExpandPointManager getExpandPointManager();
    /**解析配置文件，并且装载其中所定义的对象类型。*/
    public void loadConfig() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        List<InputStream> ins = this.getConfigStreams();
        Properties prop = new Properties();
        for (InputStream is : ins)
            prop.load(is);
        for (Object key : prop.keySet()) {
            String beanBuilderClass = prop.getProperty((String) key);
            Object builder = Class.forName(beanBuilderClass).getConstructor().newInstance();
            AbstractBeanBuilder abb = (AbstractBeanBuilder) builder;
            abb.setApplicationContext(this.applicationContext);
            this.regeditBeanBuilder((String) key, abb);
        }
    };
    /**清空单态池中的单例Bean。*/
    public void clearSingleBean() {
        this.singleBeanCache.clear();
    };
    /**
     * 注册一种bean定义类型，使之可以被引擎解析。如果重复注册同一种bean类型将会引发{@link RepeateException}类型异常。
     * @param beanType 注册的bean定义类型。
     * @param builder 要注册的bean定义生成器。
     */
    public void regeditBeanBuilder(String beanType, AbstractBeanBuilder<AbstractBeanDefine> builder) throws RepeateException {
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
        this.singleBeanCache.clear();
    };
    /**获取一个int该int表示了{@link BeanEngine}对象中已经缓存了的单例对象数目。*/
    public int getCacheBeanCount() {
        return this.singleBeanCache.size();
    };
    //----------------------------------------------------------------------------------------------------------
    /**
     * 将{@link AbstractBeanDefine}定义对象解析并且装载成为Class类型对象，期间会一次引发{@link ClassByteExpandPoint}和{@link ClassTypeExpandPoint}两个扩展点。
     * @param define 要装载类型的bean定义。
     * @return 返回装载的bean类型。
     * @throws DoesSupportException 如果{@link BeanEngine}遇到一个不支持的Bean定义类型则会引发该异常。
     * @throws IOException 在将字节码转换成Class类的时候如果发问题常则会该引发。
     * @throws ClassFormatException 在执行扩展点{@link ClassTypeExpandPoint}如果不能成功装载字节码则引发该异常。
     * @throws ClassNotFoundException 如果整个装载过程结束没有得到Class类型则会引发该类异常。
     */
    public final synchronized Class<?> builderType(AbstractBeanDefine define) throws DoesSupportException, IOException, ClassFormatException, ClassNotFoundException {
        String defineType = define.getBeanType();
        String defineID = define.getID();
        String defineLogStr = defineID + "[" + defineType + "]";//log前缀
        //1.
        AbstractBeanBuilder<AbstractBeanDefine> builder = this.beanBuilderMap.get(defineType);
        if (builder == null || builder.canBuilder() == false)
            throw new DoesSupportException(defineLogStr + "，该Bean不是一个hypha所支持的Bean类型或者该类型的Bean不支持Builder。");
        //--------------------------------------------------------------------------------------------------------------准备阶段
        //2.通过Builder装载class
        byte[] beanBytes = builder.loadBeanBytes(define);
        beanBytes = (byte[]) this.getExpandPointManager().exePoint(ClassByteExpandPoint.class, define, new Object[] { beanBytes, define, this.applicationContext });
        Class<?> beanType = (Class<?>) this.getExpandPointManager().exePoint(ClassTypeExpandPoint.class, define, new Object[] { beanBytes, define, this.applicationContext });
        //3.
        if (beanType == null)
            beanType = this.classLoader.loadClass(beanBytes, define);
        if (beanType == null)
            throw new ClassNotFoundException(defineLogStr + "，该Bean类型无法被装载或装载失败。");
        return beanType;
    };
    /**
     * 调用引擎创建过程去生成bean代码。
     * @param define 要装载类型的bean定义。
     * @param params 生成bean时传递的参数。
     * @return 返回创建的bean对象。
     */
    public final synchronized Object builderBean(AbstractBeanDefine define, Object[] params) throws Throwable {
        String defineID = define.getID();
        //--------------------------------------------------------------------------------------------------------------检查单态
        //0.单态
        if (this.singleBeanCache.containsKey(defineID) == true)
            return this.singleBeanCache.get(defineID);
        //--------------------------------------------------------------------------------------------------------------
        AbstractBeanBuilder<AbstractBeanDefine> builder = this.beanBuilderMap.get(define.getBeanType());
        Class<?> beanType = this.builderType(define);
        //1.创建
        Object obj = this.getExpandPointManager().exePoint(BeforeCreateExpandPoint.class, define, new Object[] { beanType, params, define, this.applicationContext }); //预创建Bean
        if (obj == null)
            if (builder.ifDefaultBeanCreateMode() == false)
                //------------------------（不用默认策略）
                obj = builder.createBean(define, params);
            else {
                //------------------------（使用默认策略）
                // create begin
                AbstractMethodDefine factory = define.factoryMethod();
                Collection<? extends AbstractPropertyDefine> initParam = null;
                if (factory != null) {
                    //1.工厂方式
                    initParam = factory.getParams();
                    Class<?>[] types = transform_toType(initParam, params);
                    Object[] objects = transform_toObject(initParam, params);
                    String factoryBeanID = factory.getForBeanDefine().getID();
                    if (factory.isStatic() == true) {
                        Class<?> factoryType = this.applicationContext.getBeanType(factoryBeanID);
                        Method factoryMethod = factoryType.getMethod(factory.getCodeName(), types);
                        obj = factoryMethod.invoke(null, objects);
                    } else {
                        Object factoryObject = this.applicationContext.getBean(factoryBeanID, params);/*params参数会被顺势传入工厂bean中。*/
                        Method factoryMethod = factoryObject.getClass().getMethod(factory.getCodeName(), types);
                        obj = factoryMethod.invoke(factoryObject, objects);
                    }
                } else {
                    //2.构造方法
                    initParam = define.getInitParams();
                    Class<?>[] types = transform_toType(initParam, params);
                    Object[] objects = transform_toObject(initParam, params);
                    //
                    Constructor<?> constructor = beanType.getConstructor(types);
                    obj = constructor.newInstance(objects);
                }
                // create end//
            }
        //2.属性注入
        OgnlContext ognl = new OgnlContext();
        ognl.put("this", obj);
        for (AbstractPropertyDefine propDefine : define.getPropertys()) {
            //TODO EL注入
        }
        //--------------------------------------------------------------------------------------------------------------初始化阶段
        //3.单态缓存&类型匹配
        obj = this.cast(beanType, obj);
        if (define.isSingleton() == true)
            this.singleBeanCache.put(defineID, obj);
        //4.代理销毁方法
        {
            //TODO  ProxyFinalizeClassEngine ce = new ProxyFinalizeClassEngine(this);
            //TODO  ce.setBuilderMode(BuilderMode.Propxy);
            //TODO  ce.setSuperClass(objType);
            //TODO  obj = ce.newInstance(obj);
        }
        //5.执行扩展点
        obj = this.getExpandPointManager().exePoint(AfterCreateExpandPoint.class, define, new Object[] { obj, params, define, this.applicationContext });//Bean装饰
        //6.执行生命周期init方法
        {
            Class<?> objType = obj.getClass();
            String initMethodName = define.getInitMethod();
            if (initMethodName != null) {
                Method m = objType.getMethod(initMethodName, Object[].class);
                m.invoke(obj, params);
            }
        }
        return obj;
    };
    /**检测对象类型是否匹配定义类型，如果没有指定beanType参数则直接返回。*/
    private Object cast(Class<?> beanType, Object obj) throws ClassCastException {
        if (beanType != null)
            return beanType.cast(obj);
        return obj;
    };
    /*将一组属性转换成类型。*/
    private Class<?>[] transform_toType(Collection<? extends AbstractPropertyDefine> pds, Object[] params) {
        //this.rootMetaDataParser.parser(data, rootParser, context);
        return null;//TODO
    };
    /*将一组属性转换成对象。*/
    private Object[] transform_toObject(Collection<? extends AbstractPropertyDefine> pds, Object[] params) {
        return null;//TODO
    };
    private Collection<? extends ValueMetaData> transform(Collection<? extends AbstractPropertyDefine> pds) {
        ArrayList<ValueMetaData> vms = new ArrayList<ValueMetaData>(pds.size());
        for (AbstractPropertyDefine pd : pds)
            vms.add(pd.getMetaData());
        return vms;
    }
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