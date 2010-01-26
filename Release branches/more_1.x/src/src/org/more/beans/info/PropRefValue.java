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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 代表一个引用对象类型属性的定义。引用类型一共分为四类它们是【上下文属性、引用其他bean定义、引用创建参数、元信息属性】。
 * 引用类型的的propType属性值是随着引用对象变化的，因此PropRefValue不需要配置propType属性。
 * <br/><br/>一、上下文属性（PRV_ContextAtt）：<br/>
 *   bean环境对象只有实现了{@link IAttribute}接口时候才具有上下文属性，
 *   使用上下文属性注入时意味着在注入属性时属性值的寻找是到上下文对象的{@link IAttribute}接口中寻找。
 * <br/><br/>二、引用其他bean定义（PRV_Bean）：<br/>
 *   引用类型定义，该类注入主要用于注入一个由其他{@link BeanDefinition}定义的对象。而这个对象通常可以再次被其他{@link BeanDefinition}所引用。
 * <br/><br/>三、引用创建参数（PRV_Param）：<br/>
 *   该类引用注入是指，当调用getBean(name,params)方法时，选择的params参数数组中的某一个对象作为属性值。该类型引用是不具备明确数据类型的。
 * <br/><br/>四、元信息属性（PRV_Mime）：<br/>
 *   元信息属性引用的数据来源是在info配置中可以获得的最贴近的属性值，这些属性值都存放在{@link AttBase}对象中。info软件包中的所有类都已经继承了{@link AttBase}类型。
 *   如果在最近的{@link AttBase}中没有找到相关元信息则系统会自动向上一级结构中查找属性。如果还没找到则再次向上寻找一直寻找到<b>上下文属性</b>为止。
 *   <br/>提示：BeanDefinition的层次结构参看info软件包概述。
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropRefValue extends BeanProp {
    //========================================================================================Field
    /**表示引用的属性是一个bean，这个bean是一个已经定义的bean。*/
    public static final String PRV_Bean         = "bean";
    /**表示属性从Factory容器中获取。*/
    public static final String PRV_ContextAtt   = "context";
    /**表示属性是来自于getBean时传递的环境参数中。*/
    public static final String PRV_Param        = "param";
    /**表示属性从最近的mime配置中获取。*/
    public static final String PRV_Mime         = "mime";
    /**  */
    private static final long  serialVersionUID = -194590250590692070L;
    private String             refValue         = null;                //引用值
    private String             refType          = null;                //引用类型，由PRV_常量定义。
    //==================================================================================Constructor
    /**创建引用对象类型。*/
    public PropRefValue() {
        refType = PropRefValue.PRV_ContextAtt;
    }
    /**创建引用对象类型。*/
    public PropRefValue(String refValue, String refType) {
        this.refValue = refValue;
        this.setRefType(refType);
    }
    //==========================================================================================Job
    private static String find(String pStr, String string) {
        Matcher ma_tem = Pattern.compile(pStr).matcher(string);
        ma_tem.find();
        return ma_tem.group(1);
    }
    public static PropRefValue getPropRefValue(String refValueString) {
        //refBean|{#attName}|{@number}|{$mime}
        String pStr_1 = "\\x20*\\{#(\\w+)\\}\\x20*";// 1.{#PRV_ContextAtt}
        String pStr_2 = "\\x20*\\{@(\\d+)\\}\\x20*";// 2.{@PRV_Param}
        String pStr_3 = "\\x20*\\{\\$(\\w+)\\}\\x20*";// 3.{$PRV_Mime}
        PropRefValue propRef = new PropRefValue();
        String var = refValueString;
        if (isPRV_ContextAtt(refValueString) == true) {
            propRef.setRefType(PropRefValue.PRV_ContextAtt);
            var = find(pStr_1, var);
        } else if (isPRV_Param(refValueString) == true) {
            propRef.setRefType(PropRefValue.PRV_Param);
            var = find(pStr_2, var);
        } else if (isPRV_Mime(refValueString) == true) {
            propRef.setRefType(PropRefValue.PRV_Mime);
            var = find(pStr_3, var);
        } else {
            propRef.setRefType(PropRefValue.PRV_Bean);
        }
        propRef.setRefValue(var);
        return propRef;
    }
    //{#PRV_ContextAtt}
    public static boolean isPRV_ContextAtt(String refValueString) {
        return refValueString.matches("\\x20*\\{#(\\w+)\\}\\x20*");
    }
    //{@PRV_Param}
    public static boolean isPRV_Param(String refValueString) {
        return refValueString.matches("\\x20*\\{@(\\d+)\\}\\x20*");
    }
    //{$PRV_Mime}
    public static boolean isPRV_Mime(String refValueString) {
        return refValueString.matches("\\x20*\\{\\$(\\w+)\\}\\x20*");
    }
    public static boolean isPRV_Bean(String refValueString) {
        return !(isPRV_ContextAtt(refValueString) | isPRV_Param(refValueString) | isPRV_Mime(refValueString));
    }
    /**获取引用值。*/
    public String getRefValue() {
        return refValue;
    }
    /**设置引用值。*/
    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }
    /**获取引用类型，由PRV_常量定义。*/
    public String getRefType() {
        return refType;
    }
    /**设置引用类型，由PRV_常量定义。*/
    public void setRefType(String refType) {
        this.refType = refType;
    }
}