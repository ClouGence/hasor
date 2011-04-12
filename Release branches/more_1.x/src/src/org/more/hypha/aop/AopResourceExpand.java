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
package org.more.hypha.aop;
import org.more.NoDefinitionException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.DefineResource;
import org.more.hypha.Plugin;
import org.more.hypha.aop.define.AbstractPointcutDefine;
import org.more.hypha.aop.define.AopConfigDefine;
/**
 * 该接口通过{@link Plugin}插件形式增强了{@link DefineResource}接口，以提供了更为丰富的aop相关方法。
 * @version 2010-10-8
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AopResourceExpand extends Plugin<DefineResource> {
    /**要注册的插件名*/
    public static final String AopDefineResourcePluginName = "$more_aop_ResourcePlugin";
    /**获取一个定义的切入点，如果找不到则会引发{@link NoDefinitionException}异常。*/
    public AbstractPointcutDefine getPointcutDefine(String name) throws NoDefinitionException;
    /**添加切点定义。*/
    public void addPointcutDefine(AbstractPointcutDefine define);
    /**删除切点定义。*/
    public void removePointcutDefine(String name);
    /**检测是否已经存在某个名称的{@link AbstractPointcutDefine}。*/
    public boolean containPointcutDefine(String defineName);
    //------------------
    /**测试一个aop配置是否存在。*/
    public boolean containAopDefine(String name);
    /**获取一个aop配置定义。*/
    public AopConfigDefine getAopDefine(String name);
    /**添加aop配置定义。*/
    public void addAopDefine(AopConfigDefine define);
    /**删除aop配置定义。*/
    public void removeAopDefine(String name);
    //------------------
    /**测试一个{@link AbstractBeanDefine}定义对象是否包含Aop配置。*/
    public boolean containAop(AbstractBeanDefine define);
    /**将一个aop配置携带到{@link AbstractBeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(AbstractBeanDefine define, String config);
    /**将一个aop配置携带到{@link AbstractBeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(AbstractBeanDefine define, AopConfigDefine config);
    /**移除{@link AbstractBeanDefine}对象上的aop配置，如果{@link AbstractBeanDefine}没有配置aop那么移除操作将被忽略。*/
    public void removeAop(AbstractBeanDefine define);
    /**获取{@link AbstractBeanDefine}对象上的aop配置，如果目标没有配置aop则返回null。*/
    public AopConfigDefine getAopDefine(AbstractBeanDefine define);
}