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
package org.more.hypha.define.aop;
import java.util.ArrayList;
import java.util.List;
import org.more.core.classcode.BuilderMode;
import org.more.hypha.define.AbstractDefine;
/**
 * aop配置
 * @version 2010-9-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopConfigDefine extends AbstractDefine<AopConfigDefine> {
    private String                      name                  = null; //配置名
    private BuilderMode                 aopMode               = null; //aop的生成策略
    private AbstractPointcutDefine      defaultPointcutDefine = null; //默认切点
    //
    private ArrayList<AbstractInformed> aopInformedList       = null;
    //
    /**创建{@link AopConfigDefine}对象。*/
    public AopConfigDefine() {
        this.aopMode = BuilderMode.Super;
        this.aopInformedList = new ArrayList<AbstractInformed>();
    }
    //
    /**获取aop配置名。*/
    public String getName() {
        return this.name;
    }
    /**设置aop配置名。*/
    public void setName(String name) {
        this.name = name;
    }
    /**获取默认aop切点配置*/
    public AbstractPointcutDefine getDefaultPointcutDefine() {
        return this.defaultPointcutDefine;
    }
    /**设置默认aop切点配置*/
    public void setDefaultPointcutDefine(AbstractPointcutDefine defaultPointcutDefine) {
        this.defaultPointcutDefine = defaultPointcutDefine;
    }
    /**获取aop切点执行器*/
    public List<AbstractInformed> getAopInformedList() {
        return this.aopInformedList;
    }
    /**添加一个切点执行器*/
    public void addInformed(AbstractInformed informed, AbstractPointcutDefine refPointcut) {
        informed.setRefPointcut(refPointcut);
        this.aopInformedList.add(informed);
    };
    /**添加一个切点执行器*/
    public void addInformed(AbstractInformed informed) {
        if (informed.getRefPointcut() == null)
            informed.setRefPointcut(this.defaultPointcutDefine);
        this.aopInformedList.add(informed);
    }
    /**获取aop的生成策略。*/
    public BuilderMode getAopMode() {
        return aopMode;
    }
    /**设置aop的生成策略。*/
    public void setAopMode(BuilderMode aopMode) {
        this.aopMode = aopMode;
    };
}