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
package org.more.hypha.annotation.assembler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.more.NoDefinitionException;
import org.more.hypha.DefineResource;
import org.more.hypha.annotation.AnnotationDefineResourcePlugin;
import org.more.hypha.annotation.Bean;
import org.more.hypha.annotation.KeepWatchParser;
import org.more.hypha.annotation.Param;
import org.more.hypha.beans.define.ClassBeanDefine;
import org.more.hypha.beans.define.ConstructorDefine;
import org.more.hypha.configuration.DefineResourceImpl;
/**
 * 该类用于解析Bean的注解使其成为ClassBeanDefine定义对象。
 * @version 2010-10-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Watch_Bean implements KeepWatchParser {
    public void process(Class<?> beanType, DefineResource resource, AnnotationDefineResourcePlugin plugin) {
        DefineResourceImpl resourceImpl = (DefineResourceImpl) resource;
        Bean bean = beanType.getAnnotation(Bean.class);
        ClassBeanDefine define = new ClassBeanDefine();
        String var = null;
        // ID
        var = bean.id();
        if (var.equals("") == false)
            define.setId(var);
        var = bean.name();
        if (var.equals("") == false)
            define.setName(var);
        else
            define.setName(beanType.getSimpleName());
        var = bean.logicPackage();
        if (var.equals("") == false)
            define.setLogicPackage(var);
        else
            define.setLogicPackage(beanType.getPackage().getName());
        // Scope
        var = bean.scope();
        if (var.equals("") == false)
            define.setScope(var);
        // Boolean
        define.setBoolSingleton(bean.singleton());
        define.setBoolLazyInit(bean.lazyInit());
        define.setBoolAbstract(Modifier.isAbstract(beanType.getModifiers()));
        define.setBoolInterface(Modifier.isInterface(beanType.getModifiers()));
        // Factory
        var = bean.factoryName();
        if (var.equals("") == false)
            define.setFactoryName(var);
        var = bean.factoryMethod();
        if (var.equals("") == false)
            define.setFactoryMethod(var);
        // Desc
        var = bean.description();
        if (var.equals("") == false)
            define.setDescription(var);
        // useTemplate
        var = bean.useTemplate();
        if (var.equals("") == false) {
            if (resourceImpl.containsBeanDefine(var) == true)
                define.setUseTemplate(var);
            else
                throw new NoDefinitionException("没有找到id为[" + var + "]的Bean定义作为模板。");
        }
        // Source
        define.setSource(beanType);
        // 构造方法
        Constructor<?>[] cs = beanType.getConstructors();
        for (Constructor<?> c : cs) {
            org.more.hypha.annotation.Constructor annoC = c.getAnnotation(org.more.hypha.annotation.Constructor.class);
            if (annoC == null)
                continue;
            //1.参数相关信息
            Class<?>[] cparamType = c.getParameterTypes();
            Annotation[][] cparamAnno = c.getParameterAnnotations();
            int length = cparamType.length;
            //2.参数
            for (int i = 0; i < length; i++) {
                //1)准备一个参数的数据
                Class<?> cpt = cparamType[i];
                Param cpa = null;
                for (Annotation cpa_temp : cparamAnno[i])
                    if (cpa_temp instanceof Param == true) {
                        cpa = (Param) cpa_temp;
                        break;
                    }
                //2)创建ConstructorDefine
                ConstructorDefine cDefine = new ConstructorDefine();
                cDefine.setIndex(i);
                cDefine.setClassType(cpt);
                //
                //
                //                //3)注释
                //                var = cpa.desc();
                //                if (var.equals("") == false)
                //                    cDefine.setDescription(var);
                //                //4)解析值
                //                if (cpt.isPrimitive()==true){
                //                    
                //                    cpa.value();
                //                    
                //                    //resourceImpl.getTypeManager().parserType(cpa.value(), att, property);
                //                     
                //                    
                //                    
                //                }
                //5)添加
                define.addInitParam(cDefine);
            }
            break;
        }
        // 方法
        // 属性
        //
        // C
        //
        resourceImpl.addBeanDefine(define);
        System.out.println("解析Bean:" + beanType);
    }
}