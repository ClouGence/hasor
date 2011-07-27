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
package org.more.hypha.aop.assembler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.more.core.classcode.AopBeforeListener;
import org.more.core.classcode.AopInvokeFilter;
import org.more.core.classcode.AopReturningListener;
import org.more.core.classcode.AopThrowingListener;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.RootClassLoader;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.aop.define.AbstractInformed;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.aop.define.PointcutType;
import org.more.util.BeanUtil;
/**
 * 该类是用来生成和配置aop的类。
 * @version : 2011-7-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopBuilder {
    private ApplicationContext         context   = null;
    private RootClassLoader            loader    = null; //该Loader是负责装载aop对象的
    private Map<Class<?>, ClassEngine> engineMap = null; //缓存ClassEngine
    //
    public AopBuilder(ApplicationContext context) {
        this.context = context;
    };
    public ApplicationContext getContext() {
        return this.context;
    };
    /*初始化方法*/
    public void init() {
        this.loader = new RootClassLoader(this.context.getClassLoader());
        this.engineMap = new HashMap<Class<?>, ClassEngine>();
    };
    /*销毁方法*/
    public void destroy() {
        this.loader = null;
        this.engineMap.clear();
        this.engineMap = null;
    };
    /**/
    private AopPropxyInformed passerInformed(AbstractInformed informed) {
        return new AopPropxyInformed(this.context, informed);
    };
    private void configAop(ClassEngine engine, AopConfigDefine aopDefine) {
        engine.resetAop();
        for (AbstractInformed informed : aopDefine.getAopInformedList()) {
            PointcutType type = informed.getPointcutType();
            Object informedObject = this.passerInformed(informed);
            if (type == PointcutType.Before)//注册before通知
                engine.addListener((AopBeforeListener) informedObject);
            else if (type == PointcutType.Returning)//注册return通知
                engine.addListener((AopReturningListener) informedObject);
            else if (type == PointcutType.Throwing)//注册throw通知
                engine.addListener((AopThrowingListener) informedObject);
            else if (type == PointcutType.Filter)//注册filter
                engine.addAopFilter((AopInvokeFilter) informedObject);
            else {//注册auto，全部注册上。但是AopPropxyInformed会自动判断是否执行。
                engine.addListener((AopBeforeListener) informedObject);
                engine.addListener((AopReturningListener) informedObject);
                engine.addListener((AopThrowingListener) informedObject);
                engine.addAopFilter((AopInvokeFilter) informedObject);
            }
        }
    };
    /**获取一个aop bean类型。*/
    public Class<?> builderType(Class<?> beanType, AopConfigDefine aopDefine, AbstractBeanDefine define) throws ClassNotFoundException, IOException {
        if (aopDefine.getAopMode() == BuilderMode.Propxy)
            return beanType;
        //
        if (this.engineMap.containsKey(beanType) == true)
            return this.engineMap.get(beanType).toClass();
        //
        ClassEngine engine = new ClassEngine();
        engine.setRootClassLoader(this.loader);
        engine.setBuilderMode(BuilderMode.Super);
        engine.setSuperClass(beanType);
        engine.generateName();
        engineMap.put(beanType, engine);//缓存要生成aop的类型。
        //
        BeanUtil.writePropertyOrField(define, "boolCheckType", false);//关闭类型检查
        //
        return engine.builderClass().toClass();
    }
    /**生称一个aop配置的bean，如果bean是工厂方式创建的则在这里将使用代理方式实现其aop功能。*/
    public Object builderBean(Object beanObject, AopConfigDefine aopDefine, AbstractBeanDefine define) throws ClassNotFoundException, IOException {
        ClassEngine engine = null;
        Object aopBean = null;
        //获取engine，和beanObject
        ClassLoader loader = beanObject.getClass().getClassLoader();
        if (loader instanceof RootClassLoader == true) {
            //1.Super方式aop
            RootClassLoader rootLoader = (RootClassLoader) loader;
            engine = rootLoader.getRegeditEngine(beanObject.getClass().getName());
            this.configAop(engine, aopDefine);//每次都重新config为了保证每个Bean中间
            aopBean = beanObject;
        } else {
            //2.Propxy方式Aop
            Class<?> beanType = beanObject.getClass();
            engine = new ClassEngine();
            engine.setRootClassLoader(this.loader);
            engine.setBuilderMode(BuilderMode.Propxy);
            engine.setSuperClass(beanType);
            engine.generateName();
            this.configAop(engine, aopDefine);//
            engineMap.put(beanType, engine);//缓存要生成aop的类型。
            aopBean = engine.newInstance(beanObject);
        }
        //2.执行aop配置注入
        if (engine.isConfig(aopBean) == false)
            engine.configBean(aopBean);//如果没有配置过则进行配置，该目的是为了减少重复的配置。
        return aopBean;
    }
};