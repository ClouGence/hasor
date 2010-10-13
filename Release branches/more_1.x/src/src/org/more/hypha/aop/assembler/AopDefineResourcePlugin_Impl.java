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
import org.more.NoDefinitionException;
import org.more.hypha.DefineResource;
import org.more.hypha.aop.AopBeanDefinePlugin;
import org.more.hypha.aop.AopDefineResourcePlugin;
import org.more.hypha.aop.define.AbstractPointcutDefine;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.BeanDefinePlugin;
/**
 * 该类的目的是为了扩展{@link DefineResource}接口对象以将aop信息附加到定义资源接口中。
 * @version 2010-10-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopDefineResourcePlugin_Impl implements AopDefineResourcePlugin {
    /***/
    private DefineResource                      target       = null;
    private Map<String, AbstractPointcutDefine> pointcutList = new HashMap<String, AbstractPointcutDefine>();
    private Map<String, AopConfigDefine>        configList   = new HashMap<String, AopConfigDefine>();
    /**创建{@link AopDefineResourcePlugin_Impl}对象。*/
    public AopDefineResourcePlugin_Impl(DefineResource target) {
        this.target = target;
    }
    //======================================================接口实现
    public DefineResource getTarget() {
        return this.target;
    }
    /**测试一个{@link AbstractBeanDefine}定义对象是否包含Aop配置。*/
    public boolean containAop(AbstractBeanDefine define) {
        return define.getPlugin(AopBeanDefinePlugin.AopPluginName) != null;
    }
    /**将一个aop配置携带到{@link AbstractBeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(AbstractBeanDefine define, String config) {
        AopConfigDefine configDefine = this.configList.get(config);
        if (configDefine != null)
            define.setPlugin(AopBeanDefinePlugin.AopPluginName, new AopBeanDefinePlugin(define, configDefine));
    }
    /**将一个aop配置携带到{@link AbstractBeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(AbstractBeanDefine define, AopConfigDefine config) {
        if (config != null)
            define.setPlugin(AopBeanDefinePlugin.AopPluginName, new AopBeanDefinePlugin(define, config));
    }
    /**移除{@link AbstractBeanDefine}对象上的aop配置，如果{@link AbstractBeanDefine}没有配置aop那么移除操作将被忽略。*/
    public void removeAop(AbstractBeanDefine define) {
        define.removePlugin(AopBeanDefinePlugin.AopPluginName);
    }
    /**获取{@link AbstractBeanDefine}对象上的aop配置，如果目标没有配置aop则返回null。*/
    public AopConfigDefine getAopDefine(AbstractBeanDefine define) {
        BeanDefinePlugin plugin = define.getPlugin(AopBeanDefinePlugin.AopPluginName);
        if (plugin instanceof AopBeanDefinePlugin)
            return ((AopBeanDefinePlugin) plugin).getAopConfig();
        return null;
    }
    /**获取aop配置定义。*/
    public AopConfigDefine getAopDefine(String name) {
        return this.configList.get(name);
    }
    /**获取一个定义的切入点。*/
    public AbstractPointcutDefine getPointcutDefine(String name) throws NoDefinitionException {
        if (this.pointcutList.containsKey(name) == false)
            throw new NoDefinitionException("不存在名称为[" + name + "]的AbstractPointcutDefine定义。");
        return this.pointcutList.get(name);
    }
    /**添加切点定义。*/
    public void addPointcutDefine(AbstractPointcutDefine define) {
        this.pointcutList.put(define.getName(), define);
    }
    /**删除切点定义。*/
    public void removePointcutDefine(String name) {
        this.pointcutList.remove(name);
    }
    public void addAopDefine(AopConfigDefine define) {
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