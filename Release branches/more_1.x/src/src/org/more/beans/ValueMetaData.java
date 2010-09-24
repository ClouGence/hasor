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
package org.more.beans;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类用于表示{@link AbstractPropertyDefine}属性中定义的属性值描述信息。
 * {@link PropertyMetaTypeEnum}枚举可以更详细的描述{@link ValueMetaData}
 * 类所描述的数据内容。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ValueMetaData implements IAttribute, DefineConfig {
    private IAttribute attribute    = new AttBase(); //属性
    private IAttribute defineconfig = new AttBase(); //扩展配置描述
    /**该枚举定义了{@link ValueMetaData}属性元信息表述的属性类型范畴，该类型中包含了基本类型范畴各种集合类型范畴以及一些其他类型范畴。*/
    public enum PropertyMetaTypeEnum {
        /**
         * 一个简单类型属性类型，包括字符串在内的所有java基本类型都可以由SimpleType来表示。
         * 被列为简单类型的有：null,boolean,byte,short,int,long,float,double,char,string。
         */
        SimpleType,
        /**表示一个枚举类型*/
        Enum,
        /**表示对一个bean的引用*/
        RelationBean,
        /**表示一个数组类型集合。*/
        ArrayCollection,
        /**表示一个List类型集合。*/
        ListCollection,
        /**表示一个Set类型集合。*/
        SetCollection,
        /**表示一个Map类型集合。*/
        MapCollection,
        /**表示一个文件对象。*/
        File,
        /**表示一个超级连接资源。*/
        URI,
        /**表示一个大文本对象。*/
        BigText,
        /**表示一个日期对象。*/
        Date,
    };
    /**返回这个属性的属性类型，该类型用于描述属性的类型特征。*/
    public abstract PropertyMetaTypeEnum getPropertyType();
    /**返回扩展Define配置描述。*/
    public IAttribute getDefineConfig() {
        return this.defineconfig;
    };
    //-------------------------------------------------------------
    public boolean contains(String name) {
        return this.attribute.contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.attribute.setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.attribute.getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.attribute.removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.attribute.getAttributeNames();
    };
    public void clearAttribute() {
        this.attribute.clearAttribute();
    };
}