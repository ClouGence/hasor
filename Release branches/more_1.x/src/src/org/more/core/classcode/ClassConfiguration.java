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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
/**
 * 该类的作用是用于配置{@link ClassBuilder}生成的新类。
 * @version 2010-9-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassConfiguration {
    private ClassBuilder      classBuilder             = null;
    //
    private ArrayList<String> renderMethodList         = null; //所有委托接口的方法数组，是具有顺序的。
    private ArrayList<String> renderDelegateList       = null; //委托接口数组，是具有顺序的。
    private ArrayList<String> renderDelegatePropxyList = null; //委托属性数组，是具有顺序的。
    private ArrayList<String> renderAopMethodList      = null; //具有Aop特性的方法数组，是具有顺序的。
    //
    /**创建ClassConfiguration对象。*/
    ClassConfiguration(ClassBuilder classBuilder, BuilderClassAdapter builderAdapter, AopClassAdapter aopAdapter) {
        this.classBuilder = classBuilder;
        //获取当执行生成新类时候的产出信息。
        this.renderMethodList = builderAdapter.getRenderMethodList();
        this.renderDelegateList = builderAdapter.getRenderDelegateList();
        this.renderDelegatePropxyList = builderAdapter.getRenderDelegatePropxyList();
        //获取aop产出信息。
        if (aopAdapter != null)
            this.renderAopMethodList = aopAdapter.getRenderAopMethodList();
    }
    /**配置Bean对象。*/
    public Object configBean(Object obj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ClassEngine classEngine = classBuilder.getClassEngine();
        Class<?> beanClass = obj.getClass();
        //
        //1.注入DelegateArrayName和DelegateMethodArrayName
        if (this.classBuilder.isAddDelegate() == true) {
            //1.获取MethodDelegate，delegateTypes数组对象，其顺序由renderDelegateList决定。
            Class<?>[] delegateTypes = new Class<?>[renderDelegateList.size()];//根据实际渲染的接口实现数目来创建要注入的代理数组。
            MethodDelegate[] methodDelegates = new MethodDelegate[delegateTypes.length];
            for (Class<?> delType : this.classBuilder.getDelegateType()) {
                int index = renderDelegateList.indexOf(EngineToos.replaceClassName(delType.getName()));
                if (index != -1) {
                    delegateTypes[index] = delType;
                    methodDelegates[index] = classEngine.getDelegate(delType);
                }
            }
            //2.获取method方法数组。
            Method[] methods = new Method[this.renderMethodList.size()];//根据实际渲染的代理方法实现数目来创建数组。
            for (int i = 0; i < delegateTypes.length; i++) {
                methodDelegates[i] = classEngine.getDelegate(delegateTypes[i]);
                for (Method method : delegateTypes[i].getMethods()) {
                    String m_name = method.getName();
                    String m_desc = EngineToos.toAsmType(method.getParameterTypes());
                    String m_return = EngineToos.toAsmType(method.getReturnType());
                    String fullDesc = m_name + "(" + m_desc + ")" + m_return;
                    int index = this.renderMethodList.indexOf(fullDesc);
                    if (index != -1)
                        methods[index] = method;
                }
            }
            //3.执行注入
            Method method_1 = beanClass.getMethod("set" + BuilderClassAdapter.DelegateArrayName, MethodDelegate[].class);
            method_1.invoke(obj, new Object[] { methodDelegates });//注入代理
            Method method_2 = beanClass.getMethod("set" + BuilderClassAdapter.DelegateMethodArrayName, Method[].class);
            method_2.invoke(obj, new Object[] { methods });//注入方法
        }
        //2.注入字段
        if (this.classBuilder.isAddFields() == true) {
            String[] delegateFields = this.classBuilder.getDelegateFields();
            if (delegateFields != null) {
                PropertyDelegate<?>[] delegateProperty = new PropertyDelegate<?>[this.renderDelegatePropxyList.size()];
                for (String field : delegateFields) {
                    int index = this.renderDelegatePropxyList.indexOf(field);
                    if (index != -1)
                        delegateProperty[index] = classEngine.getDelegateProperty(field);
                }
                Method method = beanClass.getMethod("set" + BuilderClassAdapter.PropertyArrayName, PropertyDelegate[].class);
                method.invoke(obj, new Object[] { delegateProperty });//注入代理属性
            }//end if
        }
        //3.注入AOP
        if (this.classBuilder.isRenderAop() == true) {
            AopStrategy aopStrategy = classEngine.getAopStrategy();
            //(1)准备数据
            Method[] aopMethodArray = new Method[this.renderAopMethodList.size()];//根据实际渲染的aop方法数目来创建数组。
            AopFilterChain_Start[] aopFilterChain = new AopFilterChain_Start[this.renderAopMethodList.size()];//根据实际渲染的aop方法数目来创建数组。
            //
            AopBeforeListener[] aopBeforeListener = classEngine.getAopBeforeListeners();
            AopReturningListener[] aopReturningListener = classEngine.getAopReturningListeners();
            AopThrowingListener[] aopThrowingListener = classEngine.getAopThrowingListeners();
            AopInvokeFilter[] aopInvokeFilter = classEngine.getAopFilters();
            //(3)生成注入数据
            for (int i = 0; i < this.renderAopMethodList.size(); i++) {
                ArrayList<Method> methodArrays = EngineToos.findAllMethod(beanClass);
                for (Method m : methodArrays) {
                    String m_name = m.getName();
                    String m_desc = EngineToos.toAsmType(m.getParameterTypes());
                    String m_return = EngineToos.toAsmType(m.getReturnType());
                    String fullDesc = m_name + "(" + m_desc + ")" + m_return;
                    int index = this.renderAopMethodList.indexOf(fullDesc);
                    if (index != -1) {
                        aopMethodArray[index] = m;
                        //执行方法的aop策略。
                        AopBeforeListener[] _aopBeforeListener = aopStrategy.filterAopBeforeListener(obj, m, aopBeforeListener);
                        AopReturningListener[] _aopReturningListener = aopStrategy.filterAopReturningListener(obj, m, aopReturningListener);
                        AopThrowingListener[] _aopThrowingListener = aopStrategy.filterAopThrowingListener(obj, m, aopThrowingListener);
                        AopInvokeFilter[] _aopInvokeFilter = aopStrategy.filterAopInvokeFilter(obj, m, aopInvokeFilter);
                        //构造过滤器链
                        AopFilterChain nextFilterChain = new AopFilterChain_End() {};
                        if (_aopInvokeFilter != null)
                            for (int j = 0; j < _aopInvokeFilter.length; j++)
                                nextFilterChain = new AopFilterChain_Impl(_aopInvokeFilter[j], nextFilterChain);
                        //构造过滤器链最后环节。
                        aopFilterChain[i] = new AopFilterChain_Start(nextFilterChain, _aopBeforeListener, _aopReturningListener, _aopThrowingListener);
                    }
                }
                //
            }
            //(4)注入
            Method method_1 = beanClass.getMethod("set" + AopClassAdapter.AopFilterChainName, AopFilterChain_Start[].class);
            method_1.invoke(obj, new Object[] { aopFilterChain });//注入代理
            Method method_2 = beanClass.getMethod("set" + AopClassAdapter.AopMethodArrayName, Method[].class);
            method_2.invoke(obj, new Object[] { aopMethodArray });//注入方法
        }
        return obj;
    }
}