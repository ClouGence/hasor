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
package org.more.hypha.commons.engine;
import java.util.HashMap;
import java.util.Map;
import org.more.ClassFormatException;
import org.more.DoesSupportException;
import org.more.RepeateException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.commons.engine.ioc.IocEngine;
import org.more.hypha.context.AbstractApplicationContext;
/**
 * 该类负责hypha的整个bean创建流程，是一个核心类。
 * @version : 2011-5-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class EngineLogic {
    private Map<String, AbstractBeanBuilder<AbstractBeanDefine>> builderMap         = null;
    private RootValueMetaDataParser                              rootParser         = null;
    private AbstractApplicationContext                           applicationContext = null;
    //
    private Map<String, IocEngine>                               engineMap          = null;
    private EngineClassLoader                                    classLoader        = null;
    //----------------------------------------------------------------------------------------------------------
    private class EngineClassLoader extends ClassLoader {
        public EngineClassLoader(AbstractApplicationContext applicationContext) {
            super(applicationContext.getBeanClassLoader());
        };
        public Class<?> loadClass(byte[] beanBytes, AbstractBeanDefine define) throws ClassFormatException {
            //如果不传递要装载的类名JVM就不会调用本地的类检查器去检查这个类是否存在。
            return this.defineClass(beanBytes, 0, beanBytes.length);
        };
    };
    /**初始化*/
    public void init(AbstractApplicationContext applicationContext) throws Throwable {
        this.applicationContext = applicationContext;
        this.classLoader = new EngineClassLoader(this.applicationContext);//使用EngineClassLoader装载构造的字节码
        this.builderMap = new HashMap<String, AbstractBeanBuilder<AbstractBeanDefine>>();
        this.rootParser = new RootValueMetaDataParser() {};
    }
    /**获取{@link AbstractApplicationContext}用于生成Bean的生成器。*/
    protected IocEngine getEngine(String key) throws Throwable {
        return this.engineMap.get(key);
    };
    /**添加一个bean注入引擎，注意重复注册将会导致替换。*/
    public void addIocEngine(String key, IocEngine engine) throws Throwable {
        engine.init(this.applicationContext, this.rootParser);
        this.engineMap.put(key, engine);
    };
    /*
     * 
     * 
     */
    //----------------------------------------------------------------------------------------------------------
    /**注册{@link ValueMetaDataParser}，如果注册的解析器出现重复则会引发{@link RepeateException}异常。*/
    public void regeditValueMetaDataParser(String metaDataType, ValueMetaDataParser<ValueMetaData> parser) {
        this.rootParser.addParser(metaDataType, parser);
    };
    /**解除注册{@link ValueMetaDataParser}，如果要移除的解析器如果不存在也不会抛出异常。*/
    public void unRegeditValueMetaDataParser(String metaDataType) {
        this.rootParser.removeParser(metaDataType);
    };
    /**
     * 注册一种bean定义类型，使之可以被引擎解析。如果重复注册同一种bean类型将会引发{@link RepeateException}类型异常。
     * @param beanType 注册的bean定义类型。
     * @param builder 要注册的bean定义生成器。
     */
    public void regeditBeanBuilder(String beanType, AbstractBeanBuilder<AbstractBeanDefine> builder) throws RepeateException {
        if (this.builderMap.containsKey(beanType) == false)
            this.builderMap.put(beanType, builder);
        else
            throw new RepeateException("不能重复注册[" + beanType + "]类型的BeanBuilder。");
    };
    /**解除指定类型bean的解析支持，无论要接触注册的bean类型是否存在该方法都会被正确执行。*/
    public void unRegeditBeanBuilder(String beanType) {
        if (this.builderMap.containsKey(beanType) == true)
            this.builderMap.remove(beanType);
    };
    /*
     * 
     * 
     */
    //----------------------------------------------------------------------------------------------------------
    /**
     * 将{@link AbstractBeanDefine}定义对象解析并且装载成为Class类型对象，期间会依次引发{@link ClassBytePoint}和{@link ClassTypePoint}两个扩展点。
     * 如果解析bean的是{@link AbstractBeanBuilder}类型则只会执行{@link ClassTypePoint}扩展点。
     * 如果解析bean的是{@link AbstractBeanBuilderEx}类型则只会执行两个扩展点。
     * @param define 要装载类型的bean定义。
     * @param params getBean时候传入的参数。
     */
    public Class<?> builderType(AbstractBeanDefine define, Object[] params) {
        String defineType = define.getBeanType();
        String defineID = define.getID();
        String defineLogStr = defineID + "[" + defineType + "]";//log前缀
        //1.
        AbstractBeanBuilder<AbstractBeanDefine> builder = this.builderMap.get(defineType);
        if (builder == null)
            throw new DoesSupportException(defineLogStr + "，该Bean不是一个hypha所支持的Bean类型或者该类型的Bean不支持Builder。");
        if (builder instanceof AbstractBeanBuilderEx == true)
            return this.doBuilderExForType((AbstractBeanBuilderEx<AbstractBeanDefine>) builder, define, params);
        else
            return this.doBuilderForType(builder, define, params);
    }
    /**执行{@link AbstractBeanBuilder}生成器过程。*/
    private Class<?> doBuilderForType(AbstractBeanBuilder<AbstractBeanDefine> builder, AbstractBeanDefine define, Object[] params) {
        Class<?> beanType = builder.loadType(define, params);
        //执行ClassTypePoint扩展点。
        beanType = (Class<?>) this.applicationContext.getExpandPointManager().exePointOnSequence(ClassTypePoint.class,//
                new Object[] { beanType, define, this.applicationContext });//Param
        return beanType;
    };
    /**执行{@link AbstractBeanBuilderEx}生成器过程。*/
    private Class<?> doBuilderExForType(AbstractBeanBuilderEx<AbstractBeanDefine> builderEx, AbstractBeanDefine define, Object[] params) {
        byte[] beanBytes = builderEx.loadBytes(define, params);
        //执行ClassBytePoint扩展点
        beanBytes = (byte[]) this.applicationContext.getExpandPointManager().exePointOnSequence(ClassBytePoint.class, //
                new Object[] { beanBytes, define, this.applicationContext });//Param
        Class<?> beanType = null;
        //执行ClassTypePoint扩展点。
        beanType = (Class<?>) this.applicationContext.getExpandPointManager().exePointOnSequence(ClassTypePoint.class,//
                new Object[] { beanType, define, this.applicationContext });//Param
        //
        if (beanType != null)
            return beanType;
        return this.classLoader.loadClass(beanBytes, define);
    };
    //----------------------------------------------------------------------------------------------------------
    /*
     * 
     * 
     */
    public <T> T builderBean(AbstractBeanDefine define, Object[] params) {
        return null;
    };
}
//class ProxyFinalizeClassEngine extends ClassEngine {
//private final ProxyFinalizeClassBuilder builder = new ProxyFinalizeClassBuilder();
//public ProxyFinalizeClassEngine(BeanEngine beanEngine) throws ClassNotFoundException {
//  super(false);
//};
//protected ClassBuilder createBuilder(BuilderMode builderMode) {
//  return this.builder;
//};
//public Object newInstance(Object propxyBean) throws FormatException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
//  Object obj = super.newInstance(propxyBean);
//  //this.toClass().getMethod("", parameterTypes);
//  return obj;
//};
//};
//class ProxyFinalizeClassBuilder extends ClassBuilder {
//protected ClassAdapter acceptClass(ClassWriter classVisitor) {
//  return new ClassAdapter(classVisitor) {
//      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//          if (name.equals("finalize()V") == true)
//              System.out.println();
//          return super.visitMethod(access, name, desc, signature, exceptions);
//      }
//  };
//}
//};