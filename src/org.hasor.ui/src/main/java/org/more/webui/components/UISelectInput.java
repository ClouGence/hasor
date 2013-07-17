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
package org.more.webui.components;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.more.webui.component.support.NoState;
import org.more.webui.context.ViewContext;
import org.more.webui.render.select.CheckManySelectInputRender;
import org.more.webui.render.select.RadioOnlySelectInputRender;
import org.more.webui.render.select.SelectOnlySelectInputRender;
import com.alibaba.fastjson.JSON;
/**
 * <b>组建模型</b>：用于表述从多个值的输入输出组建（表单元素）。
 * <br><b>服务端事件</b>：无
 * <b>渲染器</b>：{@link CheckManySelectInputRender}、{@link SelectOnlySelectInputRender}、
 * {@link RadioOnlySelectInputRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UISelectInput extends UIInput {
    /**通用属性表*/
    public static enum Propertys {
        /** 数据（R）*/
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
    public Object getListData() {
        return this.getProperty(Propertys.listData.name()).valueTo(Object.class);
    }
    @NoState
    public void setListData(Object listData) {
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
    public List getValueList() {
        List returnData = null;
        //
        Object var = this.getListData();
        if (var == null)
            return new ArrayList<Object>();
        else if (var instanceof CharSequence) {
            String varStr = null;
            if (var instanceof String)
                varStr = (String) var;
            else
                varStr = new StringBuffer((CharSequence) var).toString();
            //
            try {
                returnData = JSON.parseArray(varStr, Map.class);
            } catch (Exception e) {
                returnData = new ArrayList<Object>();
                for (String item : varStr.split(","))
                    returnData.add(item);
            }
        } else if (var.getClass().isArray() == true) {
            returnData = new ArrayList<Object>();
            for (Object item : (Object[]) var)
                returnData.add(item);
        } else if (var instanceof Collection == true) {
            returnData = new ArrayList<Object>();
            Collection coll = (Collection) var;
            Iterator iter = coll.iterator();
            while (iter.hasNext())
                returnData.add(iter.next());
        }
        return returnData;
    }
}