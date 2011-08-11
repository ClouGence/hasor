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
import java.util.Map;
/**
 * 属性访问接口，该接口的功能是提供一组常用的属性设置读取方法。
 * @version 2009-4-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IAttribute {
    /**
     * 测试某个属性是否存在。
     * @param name 被测试的属性名
     * @return 返回某个属性是否存在。
     */
    public boolean contains(String name);
    /**
     * 设置属性，如果属性已经存在则替换原有属性。
     * @param name 要保存的属性名。
     * @param value 要保存的属性值。
     */
    public void setAttribute(String name, Object value);
    /**
     * 从属性集合中获得一个属性，如果企图获得的属性不存在返回null
     * @param name 要获得的属性名。
     * @return 返回属性值如果不存在属性则返回null。
     */
    public Object getAttribute(String name);
    /**
     * 从现有属性集合中删除指定属性。
     * @param name 要删除的属性名称。
     */
    public void removeAttribute(String name);
    /** @return 返回所有属性的名称集合 */
    public String[] getAttributeNames();
    /** 清空所有属性。 */
    public void clearAttribute();
    /**将{@link IAttribute}转换为Map形式。*/
    public Map<String, Object> toMap();
}