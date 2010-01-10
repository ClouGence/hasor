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
import org.more.NoDefinitionException;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.PropRefValue;
import org.more.beans.info.PropVarValue;
import org.more.beans.resource.xml.XmlContextStack;
import org.more.beans.resource.xml.TagProcess;
/**
 * 该类负责解析property标签<br/>
 * id="" name="a" value="12" refValue="refBean|{#attName}|{@number}|{$mime}" type="int|byte|char|double|float|long|short|boolean|String"
 * @version 2009-11-22
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class Tag_Property extends TagProcess {
    protected String tagName = "property";
    protected BeanProperty createProperty() {
        return new BeanProperty();
    }
    private String find(String pStr, String string) {
        Matcher ma_tem = Pattern.compile(pStr).matcher(string);
        ma_tem.find();
        return ma_tem.group(1);
    }
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {
        //一、解析property标签属性。
        BeanProperty bp = this.createProperty();
        int attCount = xmlReader.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            String key = xmlReader.getAttributeLocalName(i);
            String var = xmlReader.getAttributeValue(i);
            if (key.equals("id") == true)
                bp.setId(var);
            else if (key.equals("name") == true)
                bp.setName(var);
            else if (key.equals("value") == true) {
                PropVarValue propVar = new PropVarValue();
                propVar.setValue(var);
                bp.setRefValue(propVar);
            } else if (key.equals("type") == true)
                bp.setPropType(var);
            else if (key.equals("refValue") == true) {
                PropRefValue propRef = new PropRefValue();
                //refBean|{#attName}|{@number}|{$mime}
                String pStr_1 = "\\x20*\\{#(\\w+)\\}\\x20*";// 1.{#PRV_ContextAtt}
                String pStr_2 = "\\x20*\\{@(\\d+)\\}\\x20*";// 2.{@PRV_Param}
                String pStr_3 = "\\x20*\\{\\$(\\w+)\\}\\x20*";// 3.{$PRV_Mime}
                //判断是何种引用方式
                if (var.matches(pStr_1) == true) {
                    propRef.setRefType(PropRefValue.PRV_ContextAtt);
                    var = this.find(pStr_1, var);
                } else if (var.matches(pStr_2) == true) {
                    propRef.setRefType(PropRefValue.PRV_Param);
                    var = this.find(pStr_2, var);
                } else if (var.matches(pStr_3) == true) {
                    propRef.setRefType(PropRefValue.PRV_Mime);
                    var = this.find(pStr_3, var);
                } else
                    propRef.setRefType(PropRefValue.PRV_Bean);
                //
                propRef.setRefValue(var);
                bp.setRefValue(propRef);
            } else
                throw new NoDefinitionException(tagName + "标签出现未定义属性[" + key + "]");
        }
        //二、将标签属性对象作为当前堆栈的值保存到堆栈值上。
        context.context = bp;
    }
    @Override
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {
        //一、获取堆栈的父堆栈，bean标签堆栈。
        ArrayList elementList = (ArrayList) context.get("tag_element");
        if (elementList == null || elementList.size() == 0) {} else {
            BeanProp bp = (BeanProp) elementList.get(0);
            BeanProperty prop = (BeanProperty) context.context;
            prop.setRefValue(bp);
        }
        //二、加入到bean的属性中。
        XmlContextStack parent = context.getParent();
        ArrayList propertyList = (ArrayList) parent.get("tag_Property");
        if (propertyList == null) {
            propertyList = new ArrayList();
            parent.put("tag_Property", propertyList);
        }
        propertyList.add(context.context);
    }
}