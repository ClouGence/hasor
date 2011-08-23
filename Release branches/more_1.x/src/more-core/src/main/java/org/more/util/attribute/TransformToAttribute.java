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
package org.more.util.attribute;
import java.util.HashMap;
import java.util.Map;
/**
 * 该类的职责是将{@link Map}接口对象转换为{@link IAttribute}接口对象。
 * 但是请注意，{@link Map}的Key必须为String类型否则可能无法通过字符串形式的Key获取到值。
 * Date : 2011-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class TransformToAttribute implements IAttribute {
    private Map<Object, Object> values = null;
    /**创建一个{@link TransformToAttribute}对象，该对象的作用是将{@link Map}转换为{@link IAttribute}接口。*/
    public TransformToAttribute(Map<Object, Object> values) {
        this.values = values;
    };
    public boolean contains(String name) {
        return this.values.containsKey(name);
    };
    public void setAttribute(String name, Object value) {
        this.values.put(name, value);
    };
    public Object getAttribute(String name) {
        return this.values.get(name);
    };
    public void removeAttribute(String name) {
        this.values.remove(name);
    };
    public String[] getAttributeNames() {
        String[] KEYS = new String[this.values.size()];
        int i = 0;
        for (Object k : this.values.keySet()) {
            KEYS[i] = (k != null) ? k.toString() : null;
            i++;
        }
        return KEYS;
    };
    public void clearAttribute() {
        this.values.clear();
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Object key : this.values.keySet())
            if (key != null)
                map.put(key.toString(), this.values.get(key));
            else
                map.put(null, this.values.get(key));
        return map;
    };
};