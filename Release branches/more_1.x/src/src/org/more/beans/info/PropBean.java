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
package org.more.beans.info;
/**
 * 表示定义的一个属性中的Bean这类bean定义通常不会被其他bean引用到，这类bean有一个特点就是它们的单态模式通常是失效的。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class PropBean extends BeanProp {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID = -194590250590692070L;
    private BeanDefinition    beanDefinition   = null;                //属性中的bean定义。
    //==================================================================================Constructor
    public PropBean(BeanDefinition beanDefinition) {}
    //==========================================================================================Job
    /**获取属性中的bean定义。*/
    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }
    /**设置属性中的bean定义。*/
    public void setBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }
}