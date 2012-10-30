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
package org.more.webui.component;
import java.util.List;
import org.more.webui.component.support.NoState;
import org.more.webui.context.ViewContext;
/**
 * 用于表述从多个值中进行选择的组建模型（表单元素）。
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UISelectInput extends UIInput {
    /**通用属性表*/
    public static enum Propertys {
        /** 数据（-）*/
        listData,
        /**显示名称字段（R）*/
        keyField,
        /**值字段（R）*/
        varField,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.listData.name(), null);
        this.setPropertyMetaValue(Propertys.keyField.name(), "key");
        this.setPropertyMetaValue(Propertys.varField.name(), "value");
    }
    @NoState
    public List<?> getListData() {
        return this.getProperty(Propertys.listData.name()).valueTo(List.class);
    }
    @NoState
    public void setListData(List<?> listData) {
        this.getProperty(Propertys.listData.name()).value(listData);
    }
    public String getKeyField() {
        return this.getProperty(Propertys.keyField.name()).valueTo(String.class);
    }
    @NoState
    public void setKeyField(String keyField) {
        this.getProperty(Propertys.keyField.name()).value(keyField);
    }
    public String getVarField() {
        return this.getProperty(Propertys.varField.name()).valueTo(String.class);
    }
    @NoState
    public void setVarField(String varField) {
        this.getProperty(Propertys.varField.name()).value(varField);
    }
    /**选择的唯一值，(R)<br/>getSelectValue是对{@link #getSelectValues()}方法的延伸。
     * 该方法只会返回{@link #getSelectValues()}方法返回值的第一个元素，如果不存在这个元素则返回null。*/
    public Object getSelectValue() {
        Object[] returnData = getSelectValues();
        if (returnData != null && returnData.length != 0)
            return returnData[0];
        return null;
    }
    /**选择的值，(R)<br/>SelectValue属性是对value属性增强解释。
     * 当value值为Object时selectvalue是一个只有一个元素的数组。
     * 如果value为String则selectvalue会根据“，”对字符串拆分。
     * 如果value为数组或集合则selectValue返回集合的数组形式。*/
    public Object[] getSelectValues() {
        Object var = this.getValue();
        if (var == null || var.getClass().isArray() == false) {
            if (var instanceof String)
                return ((String) var).split(",");
            return new Object[] { var };
        }
        Class<?> varType = var.getClass();
        if (varType.isArray() == true)
            return (Object[]) var;
        else if (var instanceof List == true)
            return ((List) var).toArray();
        else
            return null;
    }
}