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
/**
 * 创建{@link AopExpandDefine}对象。
 * @version 2010-9-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopExpandDefine implements org.more.beans.define.ExpandDefine {
    private Object    target    = null; //扩展目标
    private AopConfigDefine aopConfig = null; //扩展的aop策略
    /**获取扩展的aop策略。*/
    public AopConfigDefine getAopConfig() {
        return aopConfig;
    }
    /**设置扩展的aop策略。*/
    public void setAopConfig(AopConfigDefine aopConfig) {
        this.aopConfig = aopConfig;
    }
    /**获取扩展目标。*/
    public Object getTarget() {
        return this.target;
    }
    /**设置扩展目标。*/
    public void setTarget(Object target) {
        this.target = target;
    }
}