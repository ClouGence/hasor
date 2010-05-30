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
package org.more.beans.core.factory;
import java.lang.reflect.Constructor;
import org.more.beans.BeanFactory;
import org.more.beans.core.propparser.MainPropertyParser;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanInterface;
import org.more.beans.info.BeanProperty;
import org.more.core.classcode.ClassEngine;
/**
 * new方式是常规的执行构造方法来创建对象，如果bean没有配置构造方法则系统会调用Class的newInstance()方法创建对象。如果配置了构造方法，那么系统会自动
 * 寻找相关构造方法并且执行其构造方法（注意：默认不带参的构造方法可以不配置）。在首次找到相关类和构造方法之后这些信息会被缓存在{@link BeanDefinition}对象中。<br/>
 * 有关AOP或者附加接口实现。如果New方式创建的类配置了AOP或者接口实现则性能会大大下降，但是这个是在10万~100万个不同Class类对象上的测试结果，测试数据
 * 在下面会有介绍。在AOP或者附加接口配置下新的类对象与classcode工具的Super方式相同（私有和保护方法将不受到aop影响，如果是new方式则可以受到影响）。
 * @version 2009-11-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstructorCreateEngine extends CreateEngine {
    //========================================================================================Field
    /** 属性缓存对象，缓存属性名。 */
    private String             catchDataName = "$more_CreateEngine_Constructor";
    /**属性解析器*/
    private MainPropertyParser propParser    = null;
    //==================================================================================Constructor
    /**创建一个ConstructorCreateEngine对象，创建时必须指定属性解析器。*/
    public ConstructorCreateEngine(MainPropertyParser propParser) {
        if (propParser == null)
            throw new NullPointerException("必须指定propParser参数对象，ConstructorCreateEngine使用这个属性解析器解析属性。");
        this.propParser = propParser;
    }
    //==========================================================================================Job
    /**查找构造方法参数*/
    private Object[] findConstructorObject(BeanDefinition definition, Object[] params, BeanFactory context) throws Exception {
        BeanProperty[] beanConParams = definition.getConstructorParams();
        Object[] classConParams = new Object[beanConParams.length];
        for (int i = 0; i < beanConParams.length; i++) {
            BeanProperty beanP = beanConParams[i];
            if (beanP == null)
                continue;//忽略空的
            classConParams[i] = this.propParser.parser(null, params, beanP.getRefValue(), beanP, definition);
        }
        return classConParams;
    }
    /**
     * 查找构造方法，如果没有配置构造方法将返回null。
     * 如果配置的构造方法参数是引用的其他bean则会导致context.getBeanType(beanCP.getRefBean())。
     */
    private Constructor<?> findConstructor(Class<?> type, BeanDefinition definition, Object[] params, BeanFactory context) throws Exception {
        BeanProperty[] beanConParams = definition.getConstructorParams();
        if (beanConParams == null)
            return null;
        //
        Class<?>[] classConParams = new Class<?>[beanConParams.length];
        for (int i = 0; i < beanConParams.length; i++) {
            BeanProperty beanCP = beanConParams[i];
            classConParams[i] = this.propParser.parserType(null, params, beanCP.getRefValue(), beanCP, definition);;
        }
        return type.getConstructor(classConParams);
    }
    /**
     * 调用目标类的构造方法实例化这个对象。如果目标配置了AOP或者附加接口实现，则这个类会被{@link ClassEngine}改写。
     * 然后在依照配置的构造方法初始化这个对象。在方法最后根据对象创建时使用的类装载器类型决定是否调用{@link ClassEngine}进行配置这个新对象。
     */
    @Override
    public Object newInstance(BeanDefinition definition, Object[] params, BeanFactory context) throws Exception {
        ConstructorCreateEngineData createEngineData;
        BeanProperty[] bc = definition.getConstructorParams();
        //一、数据准备&缓存
        if (definition.containsKey(catchDataName) == false) {
            ClassLoader contextLoader = context.getBeanClassLoader();
            createEngineData = new ConstructorCreateEngineData();
            //缓存之前的基本信息
            String[] aopFilters = definition.getAopFiltersRefBean();
            BeanInterface[] implsFilters = definition.getImplImplInterface();
            //确定类型
            if (aopFilters == null && implsFilters == null)
                createEngineData.type = contextLoader.loadClass(definition.getPropType()); //没有AOP要求
            else {
                createEngineData.type = this.getClassType(definition, params, context);//需要AOP
                createEngineData.loaderClassEngine = (ClassEngine) createEngineData.type.getClassLoader();
            }
            //确定构造方法
            if (bc != null)
                createEngineData.c = this.findConstructor(createEngineData.type, definition, params, context);
            //缓存
            definition.put(catchDataName, createEngineData);
        } else
            createEngineData = (ConstructorCreateEngineData) definition.get(this.catchDataName);
        //二、执行创建
        Object obj = null;
        if (createEngineData.c == null)
            obj = createEngineData.type.newInstance();
        else
            obj = createEngineData.c.newInstance(this.findConstructorObject(definition, params, context));
        //三、装配
        return this.configurationBean(createEngineData.loaderClassEngine, obj, definition, params, context);
    }
}
/**
 * ConstructorCreateEngine方式所需要缓存的数据。
 * Date : 2009-11-14
 * @author 赵永春
 */
class ConstructorCreateEngineData {
    public Class<?>       type              = null;
    public Constructor<?> c                 = null;
    public ClassEngine    loaderClassEngine = null;
}