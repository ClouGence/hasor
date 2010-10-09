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
 * VariableBeanDefine类用于定义一个值作为bean。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class VariableBeanDefine extends TemplateBeanDefine {
    //------------------------------------------------------------------
    private Class<?> type  = null; //值类型
    private Object   value = null; //值
    /**返回“VariableBean”。*/
    public String getBeanType() {
        return "VariableBean";
    }
    /**获取值类型。*/
    public Class<?> getType() {
        return this.type;
    }
    /**设置值类型。*/
    public void setType(Class<?> type) {
        this.type = type;
    }
    /**获取值*/
    public Object getValue() {
        return this.value;
    }
    /**设置值*/
    public void setValue(Object value) {
        this.value = value;
    }
    public TemplateBeanDefine getUseTemplate() {
        return null;
    }
    public void setUseTemplate(TemplateBeanDefine useTemplate) {}
}