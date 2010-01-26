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
package org.more.beans.resource.annotation.core;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.Prop;
import org.more.beans.info.PropRefValue;
import org.more.beans.info.PropVarValue;
import org.more.beans.resource.annotation.Param;
import org.more.beans.resource.annotation.util.AnnoContextStack;
import org.more.beans.resource.annotation.util.AnnoProcess;
import org.more.beans.resource.annotation.util.AnnoScopeEnum;
/**
 * 解析方法参数包含构造方法中的参数。
 * @version 2010-1-16
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class Anno_Param implements AnnoProcess {
    @Override
    public void beginAnnotation(Annotation anno, Object atObject, AnnoContextStack context) {
        Object[] obj = (Object[]) atObject;
        Class<?> p_type = (Class<?>) obj[1];
        String p_typeName = (p_type == String.class) ? "String" : p_type.getName();
        Param p_anno = (Param) anno;
        /*---------------Param---------------*/
        BeanProperty property = new BeanProperty();
        //property.setPropType(p_typeName);
        property.setPropType(p_typeName);
        if (Prop.isBaseType(p_type) == true)
            /*---value---*/
            property.setRefValue(new PropVarValue(p_anno.value(), p_typeName));
        else {
            /*---refValue---*/
            PropRefValue propRef = PropRefValue.getPropRefValue(p_anno.refValue());
            property.setRefValue(propRef);
        }
        /*-------------Param End-------------*/
        AnnoContextStack parent = context.getParent();
        if (parent.getScope() == AnnoScopeEnum.Anno_Constructor) {
            //构造方法中的参数
            ArrayList<BeanProperty> constructorProp = (ArrayList<BeanProperty>) parent.getParent().getAttribute("constructor");
            constructorProp.add(property);
        } else if (parent.getScope() == AnnoScopeEnum.Anno_Method) {
            //普通方法中的参数
            //   ArrayList<BeanProperty> propertyProp = (ArrayList<BeanProperty>) parent.getAttribute("property");
            //   propertyProp.add(property);
        }
    }
    @Override
    public void endAnnotation(Annotation anno, Object atObject, AnnoContextStack context) {}
}