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
package org.more.util.config;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类是IAttribute接口和Config接口之间的桥梁。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class AttributeConfigBridge implements IAttribute<Object>, Config<Object> {
    private Object             context = null;
    private IAttribute<Object> attMap  = null;
    public AttributeConfigBridge() {
        this.attMap = new AttBase<Object>();
    }
    public AttributeConfigBridge(IAttribute<Object> attMap) {
        this.attMap = attMap;
    }
    public AttributeConfigBridge(IAttribute<Object> attMap, Object context) {
        this.attMap = attMap;
        this.context = context;
    }
    public Object getContext() {
        return this.context;
    }
    public Object getInitParameter(String name) {
        return this.getAttribute(name);
    }
    public Enumeration<String> getInitParameterNames() {
        Vector<String> v = new Vector<String>();
        for (String key : this.getAttributeNames())
            v.add(key);
        return v.elements();
    }
    public void clearAttribute() {
        this.attMap.clearAttribute();
    }
    public boolean contains(String name) {
        return this.attMap.contains(name);
    }
    public Object getAttribute(String name) {
        return this.attMap.getAttribute(name);
    }
    public String[] getAttributeNames() {
        return this.attMap.getAttributeNames();
    }
    public void removeAttribute(String name) {
        this.attMap.removeAttribute(name);
    }
    public void setAttribute(String name, Object value) {
        this.attMap.setAttribute(name, value);
    }
    public Map<String, Object> toMap() {
        return this.attMap.toMap();
    }
    public int size() {
        return this.attMap.size();
    }
}