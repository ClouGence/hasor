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
 * 所要附加实现的接口及其代理bean名称配置。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class BeanInterface extends Prop {
    /**  */
    private static final long serialVersionUID    = -1660901774837550451L;
    private String            typeRefBean         = null;                 //接口类型引用Bean名，配置了typeRefBean属性则可以不配置type属性。
    private String            implDelegateRefBean = null;                 //附加接口实现时接口处理委托bean名。
    //=========================================================================
    /**获取接口类型引用Bean名，配置了typeRefBean属性则可以不配置type属性。*/
    public String getTypeRefBean() {
        return typeRefBean;
    }
    /**设置接口类型引用Bean名，配置了typeRefBean属性则可以不配置type属性。*/
    public void setTypeRefBean(String typeRefBean) {
        this.typeRefBean = typeRefBean;
    }
    /**获取附加接口实现时接口处理委托bean名。*/
    public String getImplDelegateRefBean() {
        return implDelegateRefBean;
    }
    /**设置附加接口实现时接口处理委托bean名。*/
    public void setImplDelegateRefBean(String implDelegateRefBean) {
        this.implDelegateRefBean = implDelegateRefBean;
    }
}