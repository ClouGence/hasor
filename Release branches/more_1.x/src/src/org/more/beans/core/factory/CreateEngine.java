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
import java.util.HashMap;
import org.more.beans.BeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanInterface;
import org.more.core.classcode.AOPInvokeFilter;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.MethodDelegate;
/**
 * CreateEngine的子类分别实现了{@link org.more.beans.info.CreateTypeEnum}枚举中定义的创建方式。
 * @version 2009-11-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class CreateEngine {
    /***/
    public abstract Object newInstance(BeanDefinition definition, Object[] params, BeanFactory context) throws Exception;
    /** 根据{@link BeanDefinition}获取其类的Class对象，如果配置了AOP或者附加接口。则返回编译之后的Class对象。 */
    protected Class<?> getClassType(BeanDefinition definition, Object[] params, BeanFactory context) throws Exception {
        ClassLoader loader = context.getBeanClassLoader();
        ClassEngine engine = new ClassEngine(loader);
        engine.setSuperClass(loader.loadClass(definition.getPropType()));
        engine.setEnableAOP(true);
        {
            //---------------------------------------------------------------Impl
            BeanInterface[] implS = definition.getImplImplInterface();
            if (implS != null) {
                for (int i = 0; i < implS.length; i++) {
                    BeanInterface beanI = implS[i];
                    Class<?> typeClass = null;
                    String type = beanI.getPropType();
                    //获取附加的类型，该段代码可以支持引用方式引用其他接口bean。
                    if (type != null)
                        typeClass = loader.loadClass(beanI.getPropType());
                    else
                        typeClass = context.getBeanType(beanI.getRefType());
                    //附加接口实现
                    engine.appendImpl(typeClass, (MethodDelegate) context.getBean(beanI.getDelegateRefBean(), params));
                }
            }
        }
        {
            //---------------------------------------------------------------AOP
            String[] aopFilters = definition.getAopFiltersRefBean();
            if (aopFilters != null) {
                AOPInvokeFilter[] filters = new AOPInvokeFilter[aopFilters.length];
                for (int i = 0; i < aopFilters.length; i++)
                    filters[i] = (AOPInvokeFilter) context.getBean(aopFilters[i], params);
                engine.setCallBacks(filters);
            }
        }
        return engine.toClass();
    }
    /** 配置这个新Bean对象，如果{@link ClassEngine}支持在配置bean时提供新代理对象的创建根据 */
    protected Object configurationBean(ClassLoader loader, Object newObject, BeanDefinition definition, Object[] params, BeanFactory context) throws Exception {
        if (loader instanceof ClassEngine == false)
            return newObject;
        //=====================================================================
        BeanInterface[] implS = definition.getImplImplInterface();
        String[] aopFilters = definition.getAopFiltersRefBean();
        HashMap<Class<?>, MethodDelegate> replaceDelegateMap = null;
        AOPInvokeFilter[] filters = null;
        //
        //一、决定附加接口代理
        if (implS != null) {
            replaceDelegateMap = new HashMap<Class<?>, MethodDelegate>();
            for (int i = 0; i < implS.length; i++) {
                BeanInterface beanI = implS[i];
                MethodDelegate delegate = (MethodDelegate) context.getBean(beanI.getDelegateRefBean(), params);
                Class<?> typeClass = null;
                String type = beanI.getPropType();
                if (type != null)
                    typeClass = loader.loadClass(beanI.getPropType());
                else
                    typeClass = context.getBeanType(beanI.getRefType());
                replaceDelegateMap.put(typeClass, delegate);
            }
        }
        //二、决定附加接口代理
        if (aopFilters != null) {
            filters = new AOPInvokeFilter[aopFilters.length];
            for (int i = 0; i < aopFilters.length; i++)
                filters[i] = (AOPInvokeFilter) context.getBean(aopFilters[i], params);
        }
        //三、装配
        ClassEngine loaderClassEngine = (ClassEngine) loader;
        return loaderClassEngine.configuration(newObject, replaceDelegateMap, filters); //AOP代理的
    }
}