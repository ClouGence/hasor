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
package org.more.core.global;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.SequenceStack;
/**
* 
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public class Global {
    private Map<String, IAttribute<String>> listConfigAtt = null;
    private SequenceStack<String>           allConfigAtt  = null;
    private Boolean                         enableEL      = true;
    private Object                          context       = null;
    /*------------------------------------------------------------------------*/
    private Global(Object context, boolean enableEL) {
        this.listConfigAtt = new HashMap<String, IAttribute<String>>();
        this.allConfigAtt = new SequenceStack<String>();
        this.enableEL = enableEL;
        this.context = context;
    };
    /*------------------------------------------------------------------------*/
    public Object getObject(Enum<?> name) {
        return this.getToType(name, Object.class);
    };
    public Object getObject(String name) {
        return this.getToType(name, Object.class);
    };
    public Character getChar(Enum<?> name) {
        return this.getToType(name, Character.class);
    };
    public Character getChar(String name) {
        return this.getToType(name, Character.class);
    };
    public String getString(Enum<?> name) {
        return this.getToType(name, String.class);
    };
    public String getString(String name) {
        return this.getToType(name, String.class);
    };
    public Boolean getBoolean(Enum<?> name) {
        return this.getToType(name, Boolean.class);
    };
    public Boolean getBoolean(String name) {
        return this.getToType(name, Boolean.class);
    };
    public Short getShort(Enum<?> name) {
        return this.getToType(name, Short.class);
    };
    public Short getShort(String name) {
        return this.getToType(name, Short.class);
    };
    public Integer getInteger(Enum<?> name) {
        return this.getToType(name, Integer.class);
    };
    public Integer getInteger(String name) {
        return this.getToType(name, Integer.class);
    };
    public Long getLong(Enum<?> name) {
        return this.getToType(name, Long.class);
    };
    public Long getLong(String name) {
        return this.getToType(name, Long.class);
    };
    public Float getFloat(Enum<?> name) {
        return this.getToType(name, Float.class);
    };
    public Float getFloat(String name) {
        return this.getToType(name, Float.class);
    };
    public Double getDouble(Enum<?> name) {
        return this.getToType(name, Double.class);
    };
    public Double getDouble(String name) {
        return this.getToType(name, Double.class);
    };
    public Date getDate(Enum<?> name) {
        return this.getToType(name, Date.class);
    };
    public Date getDate(String name) {
        return this.getToType(name, Date.class);
    };
    /*------------------------------------------------------------------------*/
    /**视图访问以该枚举名称命名的属性文件或绑定在该枚举上的属性文件，如果尝试访问的目标则规则与{@link #getOriginalString(String)}相同。*/
    public String getOriginalString(Enum<?> name) {
        if (name == null)
            return null;
        //
        String resourceName = name.getClass().getSimpleName();
        //
        IAttribute<String> att = null;
        if (this.listConfigAtt.containsKey(resourceName) == false)
            att = this.allConfigAtt;
        else
            att = this.listConfigAtt.get(resourceName);
        return att.getAttribute(name.name());
    };
    /**按照序列顺序在所有加入的全局配置文件中寻找指定名称的属性值，并且将原始信息返回。*/
    public String getOriginalString(String name) {
        if (name == null)
            return null;
        return this.allConfigAtt.getAttribute(name);
    };
    public void addEnum(Class<Enum<?>> enumType) {}
    public void addResource(String resource) {}
    public <T> T getToType(Enum<?> enumItem, Class<?> toType) {
        return null;
    };
    public <T> T getToType(String enumName, Class<?> toType) {
        return null;
    };
    /*------------------------------------------------------------------------*/
    public static Global createForXml(String xmlPath) {
        return createForXml(xmlPath, null);
    };
    public static Global createForFile(String propPath) {
        return createForFile(propPath, null);
    };
    //
    public static Global createForXml(String xmlPath, Object context) {
        return null;
    };
    public static Global createForFile(String propPath, Object context) {
        return null;
    };
};