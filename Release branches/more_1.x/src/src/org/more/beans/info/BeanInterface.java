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
import org.more.util.attribute.AttBase;
/**
 * 
 * Date : 2009-11-4
 * @author Administrator
 */
public class BeanInterface extends AttBase {
    /**  */
    private static final long serialVersionUID    = -1660901774837550451L;
    private String            id                  = null;                 //
    private String            type                = null;                 //
    private String            typeRefBean         = null;                 //配置了typeRefBean属性之后可以不配置type属性，type属性要优先于typeRefBean
    private String            implDelegateRefBean = null;                 //
    //=================================================================
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTypeRefBean() {
        return typeRefBean;
    }
    public String getImplDelegateRefBean() {
        return implDelegateRefBean;
    }
    public void setTypeRefBean(String typeRefBean) {
        this.typeRefBean = typeRefBean;
    }
    public void setImplDelegateRefBean(String implDelegateRefBean) {
        this.implDelegateRefBean = implDelegateRefBean;
    }
}