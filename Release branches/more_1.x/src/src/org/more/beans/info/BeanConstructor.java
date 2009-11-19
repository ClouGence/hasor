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
 * 该bean是用于配置构造方法的bean，注意对于BeanConstructor类型对象其propType属性是不起任何作用的。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class BeanConstructor extends Prop {
    /**  */
    private static final long serialVersionUID  = 3461453713657581453L;
    private BeanProperty[]    constructorParams = null;                //构造方法参数表。
    //=========================================================================
    /**获取创建bean时使用的构造参数列表。*/
    public BeanProperty[] getConstructorParams() {
        return constructorParams;
    }
    /**设置创建bean时使用的构造参数列表。*/
    public void setConstructorParams(BeanProperty[] constructorParams) {
        this.constructorParams = constructorParams;
    }
}