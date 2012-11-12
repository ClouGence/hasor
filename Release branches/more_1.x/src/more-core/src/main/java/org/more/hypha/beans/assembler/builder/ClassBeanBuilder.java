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
package org.more.hypha.beans.assembler.builder;
import java.util.Collection;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractMethodDefine;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.commons.logic.AbstractBeanBuilder;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.define.ClassPathBeanDefine;
import org.more.hypha.define.ConstructorDefine;
import org.more.util.ConstructorPropxy;
import org.more.util.PropxyObject;
/**
 * {@link ClassPathBeanDefine}类型定义的解析器。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassBeanBuilder extends AbstractBeanBuilder<ClassPathBeanDefine> {
    private static Log log = LogFactory.getLog(ClassBeanBuilder.class);
    /*------------------------------------------------------------------------------*/
    public Class<?> loadType(ClassPathBeanDefine define, Object[] params) throws Throwable {
        String className = define.getSource();
        ClassLoader loader = this.getApplicationContext().getClassLoader();
        Class<?> type = loader.loadClass(className);
        log.debug("load ClassBean type {%0}.", type);
        return type;
    }
    public Object loadBean(ClassPathBeanDefine define, Object[] params) throws Throwable {
        String defineID = define.getID();
        log.debug("loadBean bean Object defineID is {%0} ...", defineID);
        //
        Class<?> defineType = this.getApplicationContext().getBeanType(defineID, params);
        Object obj = null;
        if (define.factoryMethod() != null)
            obj = this.tryCreateByFactory(define, params);
        else {
            log.debug("use builder create {%0}...", defineID);
            Collection<ConstructorDefine> cdColl = define.getInitParams();
            Object[] objects = this.transform_toObjects(null, cdColl, params);
            ConstructorPropxy cp = new ConstructorPropxy(defineType);
            for (Object o : objects)
                cp.put(o);
            obj = cp.newInstance();
        }
        return obj;
    };
    private Object tryCreateByFactory(ClassPathBeanDefine define, Object[] params) throws Throwable {
        AbstractMethodDefine factory = define.factoryMethod();
        if (factory == null)
            return null;
        String defineID = define.getID();
        Object obj = null;
        /*开始工厂方式创建*/
        String factoryBeanID = define.factoryBean().getID();
        log.debug("use factoryBean create {%0}, factoryBeanID = {%1}...", defineID, factoryBeanID);
        Collection<? extends AbstractPropertyDefine> initParamDefine = factory.getParams();
        Object[] initParam_objects = transform_toObjects(null, initParamDefine, params);//null此时还没有建立对象。
        if (factory.isStatic() == true) {
            //静态工厂方法创建
            log.debug("create by static ,function is static....");
            Class<?> factoryType = this.getApplicationContext().getBeanType(factoryBeanID);
            PropxyObject op = this.findMethodByC(factoryType, initParam_objects);
            obj = op.invokeMethod(factory.getCodeName());
        } else {
            //工厂方法创建
            log.debug("create by factory ....");
            Object factoryObject = this.getApplicationContext().getBean(factoryBeanID, params);/*params参数会被顺势传入工厂bean中。*/
            PropxyObject op = this.findMethodByO(factoryObject, initParam_objects);
            obj = op.invokeMethod(factory.getCodeName());
        }
        return obj;
    }
    private PropxyObject findMethodByC(Class<?> parentClass, Object[] params) {
        PropxyObject po = new PropxyObject(parentClass);
        for (Object o : params)
            po.put(o);
        return po;
    }
    private PropxyObject findMethodByO(Object parent, Object[] params) {
        PropxyObject po = new PropxyObject(parent);
        for (Object o : params)
            po.put(o);
        return po;
    }
    /*将一组属性转换成对象。*/
    private Object[] transform_toObjects(Object object, Collection<? extends AbstractPropertyDefine> pds, Object[] params) throws Throwable {
        if (pds == null)
            return new Object[0];
        //
        AbstractApplicationContext app = this.getApplicationContext();
        ValueMetaDataParser<ValueMetaData> rootParser = app.getEngineLogic().getRootParser();
        int size = pds.size();
        int index = 0;
        Object[] res = new Object[size];
        for (AbstractPropertyDefine apd : pds) {
            res[index] = rootParser.parser(object, apd.getMetaData(), null/*该参数无效*/, app);
            index++;
        }
        return res;
    }
};
///*------------------------------------------------------------------------------*/
////1.
//AbstractBeanBuilder<AbstractBeanDefine> builder = this.builderMap.get(defineType);
//if (builder == null) {
//  log.error("bean {%0} Type {%1} is doesn`t support!", defineID, defineType);
//  throw new SupportException("bean " + defineID + " Type " + defineType + " is doesn`t support!");
//}
////2.创建bean
//
//Object obj = this.applicationContext.getExpandPointManager().exePointOnReturn(BeforeCreatePoint.class, null,//预创建Bean
//      define, params, this.applicationContext);
