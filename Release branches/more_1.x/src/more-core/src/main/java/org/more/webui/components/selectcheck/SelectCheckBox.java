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
package org.more.webui.components.selectcheck;
import java.util.List;
import org.more.webui.component.UIInput;
import org.more.webui.component.support.NoState;
import org.more.webui.component.support.UICom;
import org.more.webui.context.ViewContext;
/**
 * 选择框多选组建。
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_SelectCheck")
public class SelectCheckBox extends UIInput {
    /**通用属性表*/
    public static enum Propertys {
        /** 数据（-）*/
        listData,
        /**显示名称字段（R）*/
        keyField,
        /**值字段（R）*/
        varField,
        /**是否将标题列于选择框之前，默认false（R）*/
        titleFirst,
    }
    @Override
    public String getComponentType() {
        return "ui_SelectCheck";
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setProperty(Propertys.keyField.name(), "key");
        this.setProperty(Propertys.varField.name(), "value");
        this.setProperty(Propertys.titleFirst.name(), false);
    }
    /**选择的值，(R)<br/>SelectValue属性是对value属性增强解释。
     * 当value值为Object时selectvalue是一个只有一个元素的数组。
     * 如果value为String则selectvalue会根据“，”对字符串拆分。
     * 如果value为数组或集合则selectValue返回集合的数组形式。*/
    public Object[] getSelectValue() {
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
    public boolean isTitleFirst() {
        return this.getProperty(Propertys.titleFirst.name()).valueTo(Boolean.TYPE);
    }
    @NoState
    public void setTitleFirst(boolean titleFirst) {
        this.getProperty(Propertys.titleFirst.name()).value(titleFirst);
    }
}