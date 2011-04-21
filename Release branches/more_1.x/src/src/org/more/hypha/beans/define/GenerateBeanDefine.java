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
package org.more.hypha.beans.define;
/**
 * GenerateBeanDefine类用于定义一个自动生成的bean。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class GenerateBeanDefine extends TemplateBeanDefine {
    private String nameStrategy     = null; //类名称生成策略Bean名
    private String aopStrategy      = null; //aop生成策略Bean名
    private String delegateStrategy = null; //委托生成策略Bean名
    private String methodStrategy   = null; //方法生成策略Bean名
    private String propertyStrategy = null; //属性生成策略Bean名
    /**返回“GenerateBean”。*/
    public String getBeanType() {
        return "GenerateBean";
    }
    /**获取类名称生成策略。*/
    public String getNameStrategy() {
        return this.nameStrategy;
    }
    /**设置类名称生成策略。*/
    public void setNameStrategy(String nameStrategy) {
        this.nameStrategy = nameStrategy;
    }
    /**获取aop生成策略。*/
    public String getAopStrategy() {
        return this.aopStrategy;
    }
    /**设置aop生成策略。*/
    public void setAopStrategy(String aopStrategy) {
        this.aopStrategy = aopStrategy;
    }
    /**获取委托生成策略。*/
    public String getDelegateStrategy() {
        return this.delegateStrategy;
    }
    /**设置委托生成策略。*/
    public void setDelegateStrategy(String delegateStrategy) {
        this.delegateStrategy = delegateStrategy;
    }
    /**获取方法生成策略。*/
    public String getMethodStrategy() {
        return this.methodStrategy;
    }
    /**设置方法生成策略。*/
    public void setMethodStrategy(String methodStrategy) {
        this.methodStrategy = methodStrategy;
    }
    /**获取属性生成策略。*/
    public String getPropertyStrategy() {
        return this.propertyStrategy;
    }
    /**设置属性生成策略。*/
    public void setPropertyStrategy(String propertyStrategy) {
        this.propertyStrategy = propertyStrategy;
    }
}