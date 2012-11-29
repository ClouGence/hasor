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
import java.util.HashMap;
import java.util.Map;
import org.more.core.error.DefineException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.DefineResource;
import org.more.hypha.aop.AopService;
import org.more.hypha.commons.AbstractService;
import org.more.hypha.define.BeanDefine;
import org.more.hypha.define.aop.AopPointcut;
import org.more.hypha.define.aop.AopConfig;
import org.more.util.attribute.IAttribute;
/**
 * 该类的目的是为了扩展{@link DefineResource}接口对象以将aop信息附加到定义资源接口中。
 * @version 2010-10-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopService_Impl extends AbstractService implements AopService {
    public static final String                  ServiceName  = "$more_aop_service";
    private static final String                 AopInfoName  = "$more_aop_info";
    private Map<String, AopPointcut> pointcutList = new HashMap<String, AopPointcut>();
    private Map<String, AopConfig>        configList   = new HashMap<String, AopConfig>();
    //
    private AopBuilder                          aopBuilder   = null;
    //
    private IAttribute<Object> getFungi(BeanDefine define) {
        if (define instanceof BeanDefine)
            return ((BeanDefine) define).getFungi();
        return null;
    }
    //
    public void start() {
        this.aopBuilder = new AopBuilder(this.getContext());
        this.aopBuilder.init();
    };
    public void stop() {
        this.aopBuilder.destroy();//执行销毁
        this.aopBuilder = null;
    };
    public AopBuilder getAopBuilder() {
        return this.aopBuilder;
    }
    //
    /**测试一个{@link BeanDefine}定义对象是否包含Aop配置。*/
    public boolean containAop(BeanDefine define) {
        return this.getFungi(define).contains(AopInfoName);
    }
    /**将一个aop配置携带到{@link BeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(BeanDefine define, String config) {
        AopConfig configDefine = this.configList.get(config);
        if (configDefine != null)
            this.getFungi(define).setAttribute(AopInfoName, configDefine);
    }
    /**将一个aop配置携带到{@link BeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(BeanDefine define, AopConfig config) {
        if (define == null || config == null)
            throw new NullPointerException("define不能为空.");
        if (config != null)
            this.getFungi(define).setAttribute(AopInfoName, config);
    }
    /**移除{@link BeanDefine}对象上的aop配置，如果{@link BeanDefine}没有配置aop那么移除操作将被忽略。*/
    public void removeAop(BeanDefine define) {
        this.getFungi(define).removeAttribute(AopInfoName);
    }
    /**获取{@link BeanDefine}对象上的aop配置，如果目标没有配置aop则返回null。*/
    public AopConfig getAopDefine(BeanDefine define) {
        IAttribute<Object> att = this.getFungi(define);
        if (att.contains(AopInfoName) == true)
            return (AopConfig) att.getAttribute(AopInfoName);
        return null;
    }
    /**获取aop配置定义。*/
    public AopConfig getAopDefine(String name) {
        return this.configList.get(name);
    }
    /**获取一个定义的切入点。*/
    public AopPointcut getPointcutDefine(String name) throws DefineException {
        if (this.pointcutList.containsKey(name) == false)
            throw new DefineException("不存在名称为[" + name + "]的AbstractPointcutDefine定义。");
        return this.pointcutList.get(name);
    }
    /**添加切点定义。*/
    public void addPointcutDefine(AopPointcut define) {
        this.pointcutList.put(define.getName(), define);
    }
    /**删除切点定义。*/
    public void removePointcutDefine(String name) {
        this.pointcutList.remove(name);
    }
    public void addAopDefine(AopConfig define) {
        this.configList.put(define.getName(), define);
    }
    public void removeAopDefine(String name) {
        this.configList.remove(name);
    }
    public boolean containPointcutDefine(String defineName) {
        return this.pointcutList.containsKey(defineName);
    }
    public boolean containAopDefine(String name) {
        return this.configList.containsKey(name);
    }
}