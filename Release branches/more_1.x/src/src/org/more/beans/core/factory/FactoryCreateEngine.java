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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.more.beans.BeanFactory;
import org.more.beans.core.TypeParser;
import org.more.beans.info.BeanConstructorParam;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanInterface;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.CreateTypeEnum;
import org.more.core.classcode.AOPInvokeFilter;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.MethodDelegate;
import org.more.core.classcode.ClassEngine.BuilderMode;
import org.more.util.StringConvert;
/**
 * 使用工厂方式创建一个Bean对象这种方式需要指定工厂类以及工厂方法相关参数，Factory方式中aop所能拦截到的方法与classcode工具的Propxt方式相同。
 * <br/><br/>该方式需要beans配置{@link CreateTypeEnum}属性为Factory，并且提供对象创建工厂时所依赖的工厂对象以及工厂方法。
 * 如果bean配置了aop或者附加接口实现则工厂bean返回的对象将由这个子系统创建一个这个对象的子类，并且以静态代理方式在代理类上实现aop以及附加接口实现。
 * 此时aop所能拦截到的方法与classcode工具的Propxt方式相同（私有和保护方法将不受到aop影响，如果是new方式则可以受到影响）。
 * Date : 2009-11-12
 * @author 赵永春
 */
public class FactoryCreateEngine extends CreateEngine {
    //========================================================================================Field
    /** 属性缓存对象，缓存属性名。 */
    private String catchFactoryMethodName   = "$more_CreateEngine_Factory";
    /** 代理对象属性缓存对象，缓存属性名。 */
    private String catchFactoryObjectPropxy = "$more_CreateEngine_Factory_PropxyConstructor";
    //==========================================================================================Job
    /**查找并获取工厂方法参数*/
    private Object[] findMethodParamObject(BeanDefinition definition, Object[] params, BeanFactory context) {
        BeanProperty[] fmParams = definition.getFactoryMethodParams();
        Object[] fmParamTypes = new Object[fmParams.length];
        for (int i = 0; i < fmParams.length; i++) {
            BeanProperty beanP = fmParams[i];
            String propType = beanP.getPropType();
            String propValue = beanP.getValue();
            if (propType == BeanConstructorParam.TS_Boolean)
                fmParamTypes[i] = StringConvert.parseBoolean(propValue);
            else if (propType == BeanConstructorParam.TS_Byte)
                fmParamTypes[i] = StringConvert.parseByte(propValue);
            else if (propType == BeanConstructorParam.TS_Char)
                if (propValue == null)
                    fmParamTypes[i] = (char) 0;
                else
                    fmParamTypes[i] = propValue.charAt(0);
            else if (propType == BeanConstructorParam.TS_Double)
                fmParamTypes[i] = StringConvert.parseDouble(propValue);
            else if (propType == BeanConstructorParam.TS_Float)
                fmParamTypes[i] = StringConvert.parseFloat(propValue);
            else if (propType == BeanConstructorParam.TS_Integer)
                fmParamTypes[i] = StringConvert.parseInt(propValue);
            else if (propType == BeanConstructorParam.TS_Long)
                fmParamTypes[i] = StringConvert.parseLong(propValue);
            else if (propType == BeanConstructorParam.TS_Short)
                fmParamTypes[i] = StringConvert.parseShort(propValue);
            else if (propType == BeanConstructorParam.TS_String)
                fmParamTypes[i] = propValue;
            else if (propType == BeanConstructorParam.TS_List)
                fmParamTypes[i] = TypeParser.passerList(null, params, beanP, context);
            else if (propType == BeanConstructorParam.TS_Set)
                fmParamTypes[i] = TypeParser.passerSet(null, params, beanP, context);
            else if (propType == BeanConstructorParam.TS_Map)
                fmParamTypes[i] = TypeParser.passerMap(null, params, beanP, context);
            else if (propType == BeanConstructorParam.TS_Array)
                fmParamTypes[i] = TypeParser.passerArray(null, params, beanP, context);
            else
                fmParamTypes[i] = TypeParser.passerType(null, params, beanP, context);
        }
        return fmParamTypes;
    }
    /**启动创建过程调用工厂类的方法创建对象，并且将创建的bean对象进行静态代理以实现AOP和附加接口方法功能。*/
    @Override
    public Object newInstance(BeanDefinition definition, Object[] params, BeanFactory context) throws Throwable {
        Method factoryMethod;
        String refBean = definition.getFactoryRefBean();
        ClassLoader loader = context.getBeanClassLoader();
        //一、获取工厂方法。
        if (definition.containsKey(catchFactoryMethodName) == false) {
            String refBeanMethod = definition.getFactoryMethodName();
            //准备方法参数类型列表
            BeanProperty[] refBeanMethodParam = definition.getFactoryMethodParams();
            Class<?>[] refBeanMethodTypes = null;
            if (refBeanMethodParam != null) {
                refBeanMethodTypes = new Class[refBeanMethodParam.length];
                for (int i = 0; i < refBeanMethodParam.length; i++) {
                    BeanProperty beanP = refBeanMethodParam[i];
                    String paramType = beanP.getPropType();
                    if (paramType != null)
                        refBeanMethodTypes[i] = this.toClass(paramType, loader);
                    else
                        refBeanMethodTypes[i] = context.getBeanType(beanP.getRefBean());
                }
            }
            //获取工厂方法
            Class<?> factoryType = context.getBeanType(refBean);
            factoryMethod = factoryType.getMethod(refBeanMethod, refBeanMethodTypes);
            definition.put(catchFactoryMethodName, factoryMethod);
        } else
            factoryMethod = (Method) definition.get(catchFactoryMethodName);
        //二、执行工厂方法调用，创建目标对象。
        Object newObject;
        Object[] invokeMethodParams = this.findMethodParamObject(definition, params, context);
        if (definition.isFactoryIsStaticMethod() == true)
            newObject = factoryMethod.invoke(null, invokeMethodParams);//静态工厂方法
        else
            newObject = factoryMethod.invoke(context.getBean(refBean, params), invokeMethodParams); //对象工厂方法
        //三、决定是否通过AOP代理，工厂方法模式下不支持Super类型AOP
        String[] aopFilters = definition.getAopFilterRefBean();
        BeanInterface[] implsFilters = definition.getImplImplInterface();
        if (aopFilters == null && implsFilters == null)
            return newObject; //没有代理要求
        else {
            //代理对象缓存
            Constructor<?> propxyConstructor = null;
            if (definition.containsKey(catchFactoryObjectPropxy) == false) {
                Class<?> superClass = newObject.getClass();
                ClassEngine engine = new ClassEngine(context.getBeanClassLoader());
                engine.setSuperClass(superClass);
                engine.setMode(BuilderMode.Propxy);
                this.configurationImpl_AOP(engine, definition, params, context);//配置engine
                propxyConstructor = engine.toClass().getConstructor(superClass);
                definition.put(catchFactoryObjectPropxy, propxyConstructor);
            } else
                propxyConstructor = (Constructor<?>) definition.get(catchFactoryObjectPropxy);
            //
            Object propxy = propxyConstructor.newInstance(newObject);
            return this.configurationBean(propxy.getClass().getClassLoader(), propxy, definition, params, context);
        }
    }
    /** 配置ClassEngine，的接口实现以及AOP。 */
    private void configurationImpl_AOP(ClassEngine engine, BeanDefinition definition, Object[] params, BeanFactory context) throws ClassNotFoundException, IOException {
        ClassLoader loader = context.getBeanClassLoader();
        {
            //---------------------------------------------------------------Impl
            BeanInterface[] implS = definition.getImplImplInterface();
            if (implS != null) {
                for (int i = 0; i < implS.length; i++) {
                    BeanInterface beanI = implS[i];
                    Class<?> typeClass = null;
                    String type = beanI.getType();
                    //获取附加的类型，该段代码可以支持引用方式引用其他接口bean。
                    if (type != null)
                        typeClass = this.toClass(type, loader);
                    else
                        typeClass = context.getBeanType(beanI.getTypeRefBean());
                    //附加接口实现
                    engine.appendImpl(typeClass, (MethodDelegate) context.getBean(beanI.getImplDelegateRefBean(), params));
                }
            }
            //
        }
        {
            //---------------------------------------------------------------AOP
            String[] aopFilters = definition.getAopFilterRefBean();
            if (aopFilters != null) {
                AOPInvokeFilter[] filters = new AOPInvokeFilter[aopFilters.length];
                for (int i = 0; i < aopFilters.length; i++)
                    filters[i] = (AOPInvokeFilter) context.getBean(aopFilters[i], params);
                engine.setCallBacks(filters);
            }
        }
    }
}