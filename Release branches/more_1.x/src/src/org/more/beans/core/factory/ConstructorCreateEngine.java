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
import org.more.beans.core.TypeParser;
import org.more.beans.info.BeanConstructor;
import org.more.beans.info.BeanConstructorParam;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanInterface;
import org.more.core.classcode.ClassEngine;
import org.more.util.StringConvert;
/**
 * new方式是常规的执行构造方法来创建对象，如果bean没有配置构造方法则系统会调用Class的newInstance()方法创建对象。如果配置了构造方法，那么系统会自动
 * 寻找相关构造方法并且执行其构造方法（注意：默认不带参的构造方法可以不配置）。在首次找到相关类和构造方法之后这些信息会被缓存在BeanDefinition对象中。<br/>
 * 有关AOP或者附加接口实现。如果New方式创建的类配置了AOP或者接口实现则性能会大大下降，但是这个是在10万~100万个不同Class类对象上的测试结果，测试数据
 * 在下面会有介绍。在AOP或者附加接口配置下新的类对象与classcode工具的Super方式相同（私有和保护方法将不受到aop影响，如果是new方式则可以受到影响）。
 * Date : 2009-11-14
 * @author 赵永春
 */
public class ConstructorCreateEngine extends CreateEngine {
    //========================================================================================Field
    /** 属性缓存对象，缓存属性名。 */
    private String catchDataName = "$more_CreateEngine_Constructor";
    //==========================================================================================Job
    /**查找构造方法参数*/
    private Object[] findConstructorObject(BeanDefinition definition, Object[] params, BeanFactory context) {
        BeanConstructorParam[] beanConParams = definition.getConstructor().getParamTypes();
        Object[] classConParams = new Object[beanConParams.length];
        for (int i = 0; i < beanConParams.length; i++) {
            BeanConstructorParam beanP = beanConParams[i];
            String propType = beanP.getPropType();
            String propValue = beanP.getValue();
            if (propType == BeanConstructorParam.TS_Boolean)
                classConParams[i] = StringConvert.parseBoolean(propValue);
            else if (propType == BeanConstructorParam.TS_Byte)
                classConParams[i] = StringConvert.parseByte(propValue);
            else if (propType == BeanConstructorParam.TS_Char)
                if (propValue == null)
                    classConParams[i] = (char) 0;
                else
                    classConParams[i] = propValue.charAt(0);
            else if (propType == BeanConstructorParam.TS_Double)
                classConParams[i] = StringConvert.parseDouble(propValue);
            else if (propType == BeanConstructorParam.TS_Float)
                classConParams[i] = StringConvert.parseFloat(propValue);
            else if (propType == BeanConstructorParam.TS_Integer)
                classConParams[i] = StringConvert.parseInt(propValue);
            else if (propType == BeanConstructorParam.TS_Long)
                classConParams[i] = StringConvert.parseLong(propValue);
            else if (propType == BeanConstructorParam.TS_Short)
                classConParams[i] = StringConvert.parseShort(propValue);
            else if (propType == BeanConstructorParam.TS_String)
                classConParams[i] = propValue;
            else if (propType == BeanConstructorParam.TS_List)
                classConParams[i] = TypeParser.passerList(null, params, beanP, context);
            else if (propType == BeanConstructorParam.TS_Set)
                classConParams[i] = TypeParser.passerSet(null, params, beanP, context);
            else if (propType == BeanConstructorParam.TS_Map)
                classConParams[i] = TypeParser.passerMap(null, params, beanP, context);
            else if (propType == BeanConstructorParam.TS_Array)
                classConParams[i] = TypeParser.passerArray(null, params, beanP, context);
            else
                classConParams[i] = TypeParser.passerType(null, params, beanP, context);
        }
        return classConParams;
    }
    /**
     * 查找构造方法，如果没有配置构造方法将返回null。
     * 如果配置的构造方法参数是引用的其他bean则会导致context.getOriginalBeanType(beanCP.getRefBean())。
     */
    private Constructor<?> findConstructor(Class<?> type, BeanDefinition definition, BeanFactory context) throws Throwable {
        BeanConstructor constructor = definition.getConstructor();
        if (constructor == null)
            return null;
        //
        ClassLoader contextLoader = context.getBeanClassLoader();
        BeanConstructorParam[] beanConParams = constructor.getParamTypes();
        Class<?>[] classConParams = new Class<?>[beanConParams.length];
        for (int i = 0; i < beanConParams.length; i++) {
            BeanConstructorParam beanCP = beanConParams[i];
            String paramType = beanCP.getPropType();
            if (paramType != null)
                classConParams[i] = this.toClass(paramType, contextLoader);
            else
                classConParams[i] = context.getBeanType(beanCP.getRefBean());
        }
        return type.getConstructor(classConParams);
    }
    /**
     * 调用目标类的构造方法实例化这个对象。如果目标配置了AOP或者附加接口实现，则这个类会被ClassEngine改写。
     * 然后在依照配置的构造方法初始化这个对象。在方法最后根据对象创建时使用的类装载器类型决定是否调用ClassEngine进行配置这个新对象。
     */
    @Override
    public Object newInstance(BeanDefinition definition, Object[] params, BeanFactory context) throws Throwable {
        ConstructorCreateEngineData createEngineData;
        BeanConstructor bc = definition.getConstructor();
        //一、数据准备&缓存
        if (definition.containsKey(catchDataName) == false) {
            ClassLoader contextLoader = context.getBeanClassLoader();
            createEngineData = new ConstructorCreateEngineData();
            //缓存之前的基本信息
            String[] aopFilters = definition.getAopFilterRefBean();
            BeanInterface[] implsFilters = definition.getImplImplInterface();
            //确定类型
            if (aopFilters == null && implsFilters == null)
                createEngineData.type = this.toClass(definition.getType(), contextLoader); //没有AOP要求
            else {
                createEngineData.type = this.getClassType(definition, params, context);//需要AOP
                createEngineData.loaderClassEngine = (ClassEngine) createEngineData.type.getClassLoader();
            }
            //确定构造方法
            if (bc != null)
                createEngineData.c = this.findConstructor(createEngineData.type, definition, context);
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