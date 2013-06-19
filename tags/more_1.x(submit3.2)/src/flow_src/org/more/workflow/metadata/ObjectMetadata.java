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
package org.more.workflow.metadata;
import java.util.HashMap;
import java.util.Map;
import org.more.RepeateException;
import org.more.workflow.el.PropertyBinding;
/**
 * 该类是模型对象的元信息，通过该类可以保存模型的属性map集合。
 * Date : 2010-6-15
 * @author 赵永春
 */
public class ObjectMetadata extends AbstractMetadata {
    private final Map<String, PropertyMetadata> propertyMap = new HashMap<String, PropertyMetadata>(); //保存用于更新模型时使用的El属性集合
    /**创建一个元信息对象，参数决定了元信息的ID。这个id可以通过getMetadataID方法获取。*/
    public ObjectMetadata(String metadataID) {
        super(metadataID);
    };
    /**
    * 添加一个属性元信息，该方法可以指定当前object的一个属性名称，并同时指定其一个表达式。
    * 当调用{@link ModeUpdataHolder}接口方法时{@link AbstractStateHolder}会根据注册的属性列表对预更新模型执行更新操作。
    * 注意不能重复注册同一个属性，否则会引发{@link RepeateException}异常。
    * @param propertyName 要添加的属性名。
    * @param expressionString 该属性对应的{@link PropertyBinding 表达式}。
    */
    public void addProperty(String propertyName, String expressionString) {
        if (this.propertyMap.containsKey(propertyName) == true)
            throw new RepeateException("不能注册重复的属性元信息。 ");
        this.addProperty(new PropertyMetadata(propertyName, expressionString));
    };
    /**
     * 添加一个属性元信息，该方法可以指定当前object的一个属性名称，并同时指定其一个表达式。当调用
     * {@link ModeUpdataHolder}接口方法时{@link AbstractStateHolder}会根据注册的属性列表对预更新模型执行更新操作。
     * 注意不能重复注册同一个属性，否则会引发{@link RepeateException}异常。
     * @param propertyItem 要添加的属性对象该属性对应的{@link PropertyBinding 表达式}。
     */
    public void addProperty(PropertyMetadata propertyItem) {
        if (this.propertyMap.containsKey(propertyItem.getMetadataID()) == true)
            throw new RepeateException("不能注册重复的属性元信息。 ");
        this.propertyMap.put(propertyItem.getMetadataID(), propertyItem);
    };
    /**取消一个属性元信息的添加，这里只需要传递属性名el即可。*/
    public void removeProperty(String propertyName) {
        if (this.propertyMap.containsKey(propertyName) == true)
            this.propertyMap.remove(propertyName);
    };
    /** 获取对象的所有属性元信息对象。 */
    public PropertyMetadata[] getPropertys() {
        PropertyMetadata[] pm = new PropertyMetadata[this.propertyMap.size()];
        pm = this.propertyMap.values().toArray(pm);
        return pm;
    };
    /** 获取某一属性的元信息对象。 */
    public PropertyMetadata getProperty(String name) {
        return this.propertyMap.get(name);
    };
};