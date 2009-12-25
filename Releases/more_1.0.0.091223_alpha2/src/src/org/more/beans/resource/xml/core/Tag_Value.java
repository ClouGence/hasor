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
package org.more.beans.resource.xml.core;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.info.BeanProp;
import org.more.beans.info.PropRefValue;
import org.more.beans.info.PropVarValue;
import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
/**
 * 该类负责解析value标签。<br/>
 * id="" value="12" refValue="refBean|{#attName}|{@number}|{$mime}" type="int|byte|char|double|float|long|short|boolean|String"
 * <br/>Date : 2009-11-23
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class Tag_Value extends TagProcess {
    private String find(String pStr, String string) {
        Matcher ma_tem = Pattern.compile(pStr).matcher(string);
        ma_tem.find();
        return ma_tem.group(1);
    }
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        String var_id = xmlReader.getAttributeValue(null, "id");
        String var_type = xmlReader.getAttributeValue(null, "type");
        String refValue = xmlReader.getAttributeValue(null, "refValue");
        String value = xmlReader.getAttributeValue(null, "value");
        String valueCDATA = null;// xmlReader.getElementText();
        BeanProp p = null;
        //==============================================================
        if (refValue == null) {
            context.setAttribute("isValue", true);
            PropVarValue prop = new PropVarValue();
            prop.setValue((value != null) ? value : valueCDATA);
            p = prop;
        } else {
            context.setAttribute("isValue", false);
            PropRefValue propRef = new PropRefValue();
            //refBean|{#attName}|{@number}|{$mime}
            String pStr_1 = "\\x20*\\{#(\\w+)\\}\\x20*";// 1.{#PRV_ContextAtt}
            String pStr_2 = "\\x20*\\{@(\\d+)\\}\\x20*";// 2.{@PRV_Param}
            String pStr_3 = "\\x20*\\{\\$(\\w+)\\}\\x20*";// 3.{$PRV_Mime}
            //判断是何种引用方式
            if (refValue.matches(pStr_1) == true) {
                propRef.setRefType(PropRefValue.PRV_ContextAtt);
                refValue = this.find(pStr_1, refValue);
            } else if (refValue.matches(pStr_2) == true) {
                propRef.setRefType(PropRefValue.PRV_Param);
                refValue = this.find(pStr_2, refValue);
            } else if (refValue.matches(pStr_3) == true) {
                propRef.setRefType(PropRefValue.PRV_Mime);
                refValue = this.find(pStr_3, refValue);
            } else
                propRef.setRefType(PropRefValue.PRV_Bean);
            //
            propRef.setRefValue(refValue);
            p = propRef;
        }
        //==============================================================
        p.setId(var_id);
        if (var_type != null)
            p.setPropType(var_type);
        //==============================================================
        context.context = p;
    }
    @Override
    public void doCharEvent(String xPath, XMLStreamReader reader, ContextStack context) {
        Boolean bool = (Boolean) context.get("isValue");
        if (bool == null || bool == false) {} else {
            PropVarValue prop = (PropVarValue) context.context;
            if (prop.getValue() == null)
                prop.setValue(reader.getText());
        }
    }
    @Override
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        /*---------------------*/
        ContextStack parent = context.getParent();
        //一、向父容器增加自身对象
        ArrayList elementList = (ArrayList) parent.get("tag_element");
        if (elementList == null) {
            elementList = new ArrayList();
            parent.put("tag_element", elementList);
        }
        elementList.add(context.context);
    }
}