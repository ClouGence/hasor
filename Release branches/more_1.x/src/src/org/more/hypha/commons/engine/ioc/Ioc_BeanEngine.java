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
package org.more.hypha.commons.engine.ioc;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.AbstractMethodDefine;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.PropertyBinding;
import org.more.hypha.ValueMetaData;
import org.more.hypha.commons.AbstractExpandPointManager;
import org.more.hypha.commons.engine.AbstractBeanBuilder;
/**
* 该类的职责是负责将{@link AbstractBeanDefine}转换成类型或者Bean实体对象。
* 该类是一个抽象类，在使用时需要通过子类给定其{@link AbstractExpandPointManager}对象。
* @version 2011-1-13
* @author 赵永春 (zyc@byshell.org)
*/
public class Ioc_BeanEngine extends IocEngine {
    public static final String EngineName = "Ioc";
    /**
     * 调用引擎创建过程去生成bean代码。
     * @param define 要装载类型的bean定义。
     * @param params 生成bean时传递的参数。
     * @return 返回创建的bean对象。
     */
    public final Object builderBean(AbstractBeanDefine define, Object[] params) throws Throwable {
        String defineID = define.getID();
        //--------------------------------------------------------------------------------------------------------------
        AbstractBeanBuilder<AbstractBeanDefine> builder = this.beanBuilderMap.get(define.getBeanType());
        Class<?> beanType = this.builderType(define, params);
        //1.创建
        Object obj = this.applicationContext.getExpandPointManager().exePointOnReturn(BeforeCreatePoint.class, //预创建Bean
                new Object[] { beanType, params, define, this.applicationContext });
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
                    Class<?>[] types = transform_toTypes(initParam, params);
                    Object[] objects = transform_toObjects(initParam, params);
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
                    Class<?>[] types = transform_toTypes(initParam, params);
                    Object[] objects = transform_toObjects(initParam, params);
                    //
                    Constructor<?> constructor = beanType.getConstructor(types);
                    obj = constructor.newInstance(objects);
                }
                // create end//
            }
        //2.属性注入
        for (AbstractPropertyDefine propDefine : define.getPropertys()) {
            PropertyDefine prop = (PropertyDefine) propDefine;
            PropertyBinding eval = this.applicationContext.getELContext().getPropertyBinding(prop.getName(), obj);
            eval.setValue(this.transform_toObject(propDefine, params));//EL注入
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
        obj = this.applicationContext.getExpandPointManager().exePointOnSequence(AfterCreatePoint.class,//Bean装饰
                new Object[] { obj, params, define, this.applicationContext });
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
    private Class<?>[] transform_toTypes(Collection<? extends AbstractPropertyDefine> pds, Object[] params) {
        System.out.println();
        return null;//TODO
    };
    /*将一组属性转换成对象。*/
    private Object[] transform_toObjects(Collection<? extends AbstractPropertyDefine> pds, Object[] params) {
        System.out.println();
        return null;//TODO
    };
    /*将属性转换成对象。*/
    private Object transform_toObject(AbstractPropertyDefine prop, Object[] params) {
        System.out.println();
        return null;//TODO
    };
    private Collection<? extends ValueMetaData> transform(Collection<? extends AbstractPropertyDefine> pds) {
        ArrayList<ValueMetaData> vms = new ArrayList<ValueMetaData>(pds.size());
        for (AbstractPropertyDefine pd : pds)
            vms.add(pd.getMetaData());
        return vms;
    }
};