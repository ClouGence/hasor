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
package org.more.hypha.context.app;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.beans.assembler.BeanEngine;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.AbstractDefineResource;
import org.more.hypha.context.array.ArrayDefineResource;
import org.more.hypha.event.DestroyEvent;
import org.more.hypha.event.InitEvent;
import org.more.util.attribute.IAttribute;
/**
 * 简单的{@link ApplicationContext}接口实现类。
 * Date : 2011-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class HyphaApplicationContext extends AbstractApplicationContext {
    private DefineResource defineResource = null;
    private BeanEngine     engine         = null;
    //
    //
    public HyphaApplicationContext() {
        ArrayDefineResource adr = new ArrayDefineResource();
        this.setAttributeContext(adr);
        this.setFlashContext(adr.getFlash());
        this.defineResource = adr;
    }
    public HyphaApplicationContext(AbstractDefineResource defineResource) {
        this.setAttributeContext(defineResource);
        this.setFlashContext(defineResource.getFlash());
        this.defineResource = defineResource;
    }
    protected HyphaApplicationContext(DefineResource defineResource, IAttribute flash) {
        if (defineResource == null)
            throw new NullPointerException("参数defineResource没有指定一个有效的值，该参数不可以为空。");
        this.setAttributeContext(defineResource);
        this.setFlashContext(flash);
        this.defineResource = defineResource;
    }
    public DefineResource getBeanResource() {
        return this.defineResource;
    }
    public void init() throws Throwable {
        this.engine = new BeanEngine();
        //
        // TODO Auto-generated method stub
        //5.初始化bean
        for (String id : this.getBeanDefinitionIDs()) {
            AbstractBeanDefine define = this.getBeanDefinition(id);
            if (define.isLazyInit() == false)
                this.getBean(id);
        }
        //6.初始化事件 
        this.getEventManager().doEvent(Event.getEvent(InitEvent.class), this);
    }
    public void destroy() throws Throwable {
        this.getEventManager().doEvent(Event.getEvent(DestroyEvent.class), this);
        this.engine.clearBeanCache();
        this.engine.clearSingleBean();
        this.engine = null;
        this.clearAttribute();
        this.getELContext().clearAttribute();
        this.getEventManager().clearEvent();
        //this.getExpandPointManager().
        this.getFlash().clearAttribute();
        //this.getScopeContext().
        //this.getScriptContext().
    }
    public Object getServices(Class<?> servicesType) {
        // TODO Auto-generated method stub
        return null;
    }
    protected Object builderBean(AbstractBeanDefine define, Object[] params) throws Throwable {
        return this.engine.builderBean(define, params);
    }
    protected Class<?> builderType(AbstractBeanDefine define, Object[] params) throws Throwable {
        return this.engine.builderType(define, params);
    }
    //    public static final String              ELConfig           = "/META-INF/resource/hypha/regedit-el.prop";      //HyphaApplicationContext的配置信息
    //    public static final String              MetaDataConfig     = "/META-INF/resource/hypha/regedit-metadata.prop"; //HyphaApplicationContext的配置信息
    //    public static final String              BeanTypeConfig     = "/META-INF/resource/hypha/regedit-beantype.prop"; //HyphaApplicationContext的配置信息
    //    private static final int                InitTimeOut        = 120;                                             //120个500毫秒=1分钟。init超时
    //    /**
    //     * 构造{@link HyphaApplicationContext}对象，这个构造方法会导致{@link HyphaApplicationContext}内部创建一个{@link ArrayDefineResource}对象。
    //     * @param defineResource 指定{@link HyphaApplicationContext}对象一个Bean定义的数据源。
    //     * @param context 构造方法中有一个参数该参数表示一个上下文 。通过el可以访问到这个对象。
    //     */
    //    public HyphaApplicationContext(DefineResource defineResource, Object context) {
    //        if (defineResource == null)
    //            throw new NullPointerException("defineResource参数不能为空");
    //        this.defineResource = defineResource;
    //        this.attributeContext = defineResource;
    //        this.contextObject = context;
    //    };
    //    public synchronized void init() throws Throwable {
    //        //1.环境初始化，如果defineResource没有准备好就一直等待
    //        int i = 0;
    //        while (!this.defineResource.isReady()) {
    //            Thread.sleep(500);
    //            i++;
    //            if (i >= InitTimeOut)
    //                throw new TimeOutException("由于defineResource的isReady()始终返回false，无法完成Context的初始化工作。");
    //        }
    //        //---------------------------------------------------------------------------------------------------
    //        this.rootMetaDataParser = new AbstractRootValueMetaDataParser() {
    //            public void init(ApplicationContext context, IAttribute flash) throws Throwable {
    //                super.init(context, flash);
    //                List<InputStream> ins = ClassPathUtil.getResource(HyphaApplicationContext.MetaDataConfig);
    //                Properties prop = new Properties();
    //                for (InputStream is : ins)
    //                    prop.load(is);
    //                for (Object key : prop.keySet()) {
    //                    String beanBuilderClass = prop.getProperty((String) key);
    //                    Object builder = Class.forName(beanBuilderClass).newInstance();
    //                    this.addParser((String) key, (ValueMetaDataParser) builder);
    //                }
    //            };
    //        };
    //        //---------------------------------------------------------------------------------------------------
    //        this.engine = new AbstractBeanEngine() {
    //            public void init(ApplicationContext context, IAttribute flash) throws Throwable {
    //                super.init(context, flash);
    //                List<InputStream> ins = ClassPathUtil.getResource(HyphaApplicationContext.BeanTypeConfig);
    //                Properties prop = new Properties();
    //                for (InputStream is : ins)
    //                    prop.load(is);
    //                for (Object key : prop.keySet()) {
    //                    String beanBuilderClass = prop.getProperty((String) key);
    //                    Object builder = Class.forName(beanBuilderClass).newInstance();
    //                    this.regeditBeanBuilder((String) key, (AbstractBeanBuilder) builder);
    //                }
    //            };
    //        };
    //        //---------------------------------------------------------------------------------------------------
    //        this.elContext = new AbstractELContext() {
    //            public void init(ApplicationContext context, IAttribute flash) throws Throwable {
    //                super.init(context, flash);
    //                //装载regedit-metadata.prop属性文件。------------------------------------
    //                List<InputStream> ins = ClassPathUtil.getResource(HyphaApplicationContext.ELConfig);
    //                Properties prop = new Properties();
    //                for (InputStream is : ins)
    //                    prop.load(is);
    //                for (Object key : prop.keySet()) {
    //                    String k = (String) key;
    //                    String beanBuilderClass = prop.getProperty(k);
    //                    Object builder = Class.forName(beanBuilderClass).getConstructor().newInstance();
    //                    this.addELObject(k, (ELObject) builder);
    //                }
    //            }
    //        };
    //        //---------------------------------------------------------------------------------------------------
    //        this.rootMetaDataParser.init(this, this.getFlash());//处理“regedit-metadata.prop”配置文件。
    //        this.engine.init(this, this.getFlash());//处理“regedit-beantype.prop”配置文件。
    //        this.elContext.init(this, this.getFlash());//处理“regedit-el.prop”配置文件。
    //        this.scopeContext.init(this, this.getFlash());
    //        this.scriptContext.init(this, this.getFlash());
    //        //---------------------------------------------------------------------------------------------------
    //    };
};