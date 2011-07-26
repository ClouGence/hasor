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
package org.more.hypha.aop.define;
import org.more.hypha.commons.define.AbstractDefine;
/**
 * 抽象的informed，无论是何种形式的informed他们都只能绑定一个切入点。这个切入点可是以组的形式存在也可以单体存在。
 * @version 2010-9-27
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractInformed extends AbstractDefine<AbstractInformed> {
    private AbstractPointcutDefine refPointcut  = null; //连接的切点声明
    private PointcutType           pointcutType = null; //aop切点类型
    private String                 refBean      = null; //连接的aopBean
    /**获取关联的refBean。*/
    public String getRefBean() {
        return this.refBean;
    }
    /**设置关联的RefBean。*/
    public void setRefBean(String refBean) {
        this.refBean = refBean;
    }
    /**获取连接的切点*/
    public AbstractPointcutDefine getRefPointcut() {
        return this.refPointcut;
    };
    /**设置连接的切点*/
    public void setRefPointcut(AbstractPointcutDefine refPointcut) {
        this.refPointcut = refPointcut;
    };
    /**获取aop切点类型*/
    public PointcutType getPointcutType() {
        return this.pointcutType;
    };
    /**设置aop切点类型*/
    public void setPointcutType(PointcutType pointcutType) {
        this.pointcutType = pointcutType;
    };
};