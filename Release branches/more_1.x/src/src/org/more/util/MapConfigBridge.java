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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.more.util.attribute.AttBase;
/**
 * 该类是Map接口和Config接口之间的桥梁。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class MapConfigBridge implements Map<String, Object>, Config {
    //========================================================================================Field
    private static final long   serialVersionUID = 5340008420422363045L;
    private Object              context          = null;
    private Map<String, Object> attMap           = null;
    //==================================================================================Constructor
    public MapConfigBridge() {
        this.attMap = new AttBase();
    }
    public MapConfigBridge(Object context) {
        this.attMap = new AttBase();
        this.context = context;
    }
    public MapConfigBridge(Map<String, Object> attMap, Object context) {
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
        return this.attMap.get(name);
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        Vector<String> v = new Vector<String>();
        for (String key : this.attMap.keySet())
            v.add(key);
        return v.elements();
    }
    //==========================================================================================Job
    @Override
    public void clear() {
        this.attMap.clear();
    }
    @Override
    public boolean containsKey(Object key) {
        return this.attMap.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return this.attMap.containsValue(value);
    }
    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return this.attMap.entrySet();
    }
    @Override
    public Object get(Object key) {
        return this.attMap.get(key);
    }
    @Override
    public boolean isEmpty() {
        return this.attMap.isEmpty();
    }
    @Override
    public Set<String> keySet() {
        return this.attMap.keySet();
    }
    @Override
    public Object put(String key, Object value) {
        return this.attMap.put(key, value);
    }
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        this.attMap.putAll(m);
    }
    @Override
    public Object remove(Object key) {
        return this.attMap.remove(key);
    }
    @Override
    public int size() {
        return this.attMap.size();
    }
    @Override
    public Collection<Object> values() {
        return this.attMap.values();
    }
}