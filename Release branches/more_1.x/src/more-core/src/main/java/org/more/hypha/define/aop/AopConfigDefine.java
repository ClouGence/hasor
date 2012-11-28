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
import org.more.hypha.define.AbstractDefine;
/**
 * aop配置
 * @version 2010-9-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopConfigDefine extends AbstractDefine<AopConfigDefine> {
    /*配置名*/
    private String                         name                  = null;
    /*aop的生成策略Super|Propxy*/
    private String                         aopMode               = "super";
    /*默认切点*/
    private AopAbstractPointcutDefine      defaultPointcutDefine = null;
    /*切点执行器*/
    private ArrayList<AopAbstractInformed> aopInformedList       = new ArrayList<AopAbstractInformed>();
    /*------------------------------------------------------------------*/
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
    public AopAbstractPointcutDefine getDefaultPointcutDefine() {
        return this.defaultPointcutDefine;
    }
    /**设置默认aop切点配置*/
    public void setDefaultPointcutDefine(AopAbstractPointcutDefine defaultPointcutDefine) {
        this.defaultPointcutDefine = defaultPointcutDefine;
    }
    /**获取aop的生成策略Super|Propxy*/
    public String getAopMode() {
        return aopMode;
    }
    /**设置aop的生成策略Super|Propxy*/
    public void setAopMode(String aopMode) {
        this.aopMode = aopMode;
    }
    /**获取切点执行器*/
    public ArrayList<AopAbstractInformed> getAopInformedList() {
        return aopInformedList;
    }
    /**设置切点执行器*/
    public void setAopInformedList(ArrayList<AopAbstractInformed> aopInformedList) {
        this.aopInformedList = aopInformedList;
    }
}