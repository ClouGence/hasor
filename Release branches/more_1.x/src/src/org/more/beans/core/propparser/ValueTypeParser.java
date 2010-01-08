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
package org.more.beans.core.propparser;
import org.more.DoesSupportException;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.Prop;
import org.more.beans.info.PropVarValue;
import org.more.util.StringConvert;
/**
 * 解析{@link PropVarValue}类型的属性解析器，ValueTypeParser可以处理的基本类型包括: int、byte、char、double、float、long、short、boolean、String。
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class ValueTypeParser implements PropertyParser {
    @Override
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) {
        String propType = this.propType(prop, propContext);
        //==============================================
        PropVarValue p = (PropVarValue) prop;
        String value = p.getValue();
        //
        if (propType == BeanProperty.TS_Integer)
            return StringConvert.parseInt(value);
        else if (propType == BeanProperty.TS_Byte)
            return StringConvert.parseByte(value);
        else if (propType == BeanProperty.TS_Char) {
            if (value == null)
                return (char) 0;
            else
                return value.charAt(0);
        } else if (propType == BeanProperty.TS_Double)
            return StringConvert.parseDouble(value);
        else if (propType == BeanProperty.TS_Float)
            return StringConvert.parseFloat(value);
        else if (propType == BeanProperty.TS_Long)
            return StringConvert.parseLong(value);
        else if (propType == BeanProperty.TS_Short)
            return StringConvert.parseShort(value);
        else if (propType == BeanProperty.TS_Boolean)
            return StringConvert.parseBoolean(value);
        else if (propType == BeanProperty.TS_String)
            return value;
        else
            throw new DoesSupportException(propType + "不是一个有效的基本类型，ValueTypeParser可以处理的基本类型包括: int、byte、char、double、float、long、short、boolean、String。");
    }
    @Override
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception {
        String propType = this.propType(prop, propContext);
        if (propType == Prop.TS_Integer)
            return int.class;
        else if (propType == Prop.TS_Byte)
            return byte.class;
        else if (propType == Prop.TS_Char)
            return char.class;
        else if (propType == Prop.TS_Double)
            return double.class;
        else if (propType == Prop.TS_Float)
            return float.class;
        else if (propType == Prop.TS_Long)
            return long.class;
        else if (propType == Prop.TS_Short)
            return short.class;
        else if (propType == Prop.TS_Boolean)
            return boolean.class;
        else if (propType == Prop.TS_String)
            return String.class;
        else
            throw new DoesSupportException(propType + "不是一个有效的基本类型，ValueTypeParser可以处理的基本类型包括: int、byte、char、double、float、long、short、boolean、String。");
    }
    /*
     * propType属性规则。
     * 1.如果没有配置propType属性则使用value所处的BeanProperty的属性作为配置。
     */
    private String propType(BeanProp prop, BeanProperty propContext) {
        String propType = prop.getPropType();
        if (propType != null)
            return propType;
        else
            return propContext.getPropType();
    }
}