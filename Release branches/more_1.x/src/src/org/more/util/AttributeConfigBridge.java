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
package org.more.util;
import java.util.Enumeration;
import java.util.Vector;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类是IAttribute接口和Config接口之间的桥梁。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class AttributeConfigBridge implements IAttribute, Config {
    //========================================================================================Field
    private static final long serialVersionUID = 5340008420422363045L;
    private Object            context          = null;
    private IAttribute        attMap           = null;
    //==================================================================================Constructor
    public AttributeConfigBridge() {
        this.attMap = new AttBase();
    }
    public AttributeConfigBridge(IAttribute attMap) {
        this.attMap = attMap;
    }
    public AttributeConfigBridge(IAttribute attMap, Object context) {
        this.attMap = attMap;
        this.context = context;
    }
    //=========================================================================================Impl
    @Override
    public Object getContext() {
        return this.context;
    }
    @Override
    public Object getInitParameter(String name) {
        return this.getAttribute(name);
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        Vector<String> v = new Vector<String>();
        for (String key : this.getAttributeNames())
            v.add(key);
        return v.elements();
    }
    //==========================================================================================Job
    @Override
    public void clearAttribute() {
        this.attMap.clearAttribute();
    }
    @Override
    public boolean contains(String name) {
        return this.attMap.contains(name);
    }
    @Override
    public Object getAttribute(String name) {
        return this.attMap.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.attMap.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.attMap.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.attMap.setAttribute(name, value);
    }
}