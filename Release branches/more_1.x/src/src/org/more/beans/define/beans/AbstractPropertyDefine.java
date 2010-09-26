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
package org.more.beans.define.beans;
import org.more.beans.define.AbstractDefine;
import org.more.beans.define.ValueMetaData;
/**
 * 表示一个bean定义中的一种属性或参数
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractPropertyDefine extends AbstractDefine implements org.more.beans.define.AbstractPropertyDefine {
    private Class<?>      classType     = null; //属性类型
    private String        description   = null; //属性描述
    private ValueMetaData valueMetaData = null; //值描述
    //-------------------------------------------------------------
    /**返回这个属性的Java类型。*/
    public Class<?> getClassType() {
        return this.classType;
    };
    /**返回属性的描述信息。*/
    public String getDescription() {
        return this.description;
    };
    /**获取对该属性的值信息描述。*/
    public ValueMetaData getMetaData() {
        return this.valueMetaData;
    };
    //------------------------------------------------------------- 
    /**设置属性值的描述信息*/
    public void setValueMetaData(ValueMetaData valueMetaData) {
        this.valueMetaData = valueMetaData;
    }
    /**设置属性类型*/
    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }
    /**设置属性描述*/
    public void setDescription(String description) {
        this.description = description;
    }
}