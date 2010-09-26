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
package org.more.beans.define.aop;
import java.util.ArrayList;
import java.util.List;
import org.more.beans.define.AbstractDefine;
/**
 * aop配置
 * @version 2010-9-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopConfigDefine extends AbstractDefine {
    private String                 name                  = null; //配置名
    private AopConfigDefine        useTemplate           = null; //使用的模板
    private AbstractPointcutDefine defaultPointcutDefine = null; //默认切点
    private ArrayList<LinkBean>    aopInformedList       = null; //aop执行者
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
    /**获取aop配置所使用的配置模板。*/
    public AopConfigDefine getUseTemplate() {
        return this.useTemplate;
    }
    /**设置aop配置所使用的配置模板。*/
    public void setUseTemplate(AopConfigDefine useTemplate) {
        this.useTemplate = useTemplate;
    }
    /**获取aop切点执行器*/
    public List<LinkBean> getAopInformedList() {
        return this.aopInformedList;
    }
    /**添加一个切点执行器*/
    public void addInformed(String ref, AbstractPointcutDefine refPointcut) {
        this.aopInformedList.add(new LinkBean(refPointcut, ref));
    };
}