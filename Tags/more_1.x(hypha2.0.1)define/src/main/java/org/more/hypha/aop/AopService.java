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
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.Service;
import org.more.hypha.define.aop.AopPointcut;
import org.more.hypha.define.aop.AopConfig;
/**
 * 提供了更为丰富的aop相关方法。
 * @version 2010-10-8
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AopService extends Service {
    /**获取一个定义的切入点。*/
    public AopPointcut getPointcutDefine(String name);
    /**添加切点定义。*/
    public void addPointcutDefine(AopPointcut define);
    /**删除切点定义。*/
    public void removePointcutDefine(String name);
    /**检测是否已经存在某个名称的{@link AopPointcut}。*/
    public boolean containPointcutDefine(String defineName);
    //------------------
    /**测试一个aop配置是否存在。*/
    public boolean containAopDefine(String name);
    /**获取一个aop配置定义。*/
    public AopConfig getAopDefine(String name);
    /**添加aop配置定义。*/
    public void addAopDefine(AopConfig define);
    /**删除aop配置定义。*/
    public void removeAopDefine(String name);
    //------------------
    /**测试一个{@link BeanDefine}定义对象是否包含Aop配置。*/
    public boolean containAop(BeanDefine define);
    /**将一个aop配置携带到{@link BeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(BeanDefine define, String config);
    /**将一个aop配置携带到{@link BeanDefine}对象上，该方法可以在代码级上修改aop配置。*/
    public void setAop(BeanDefine define, AopConfig config);
    /**移除{@link BeanDefine}对象上的aop配置，如果{@link BeanDefine}没有配置aop那么移除操作将被忽略。*/
    public void removeAop(BeanDefine define);
    /**获取{@link BeanDefine}对象上的aop配置，如果目标没有配置aop则返回null。*/
    public AopConfig getAopDefine(BeanDefine define);
}