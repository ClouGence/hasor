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
package org.more.hypha.define;
import java.util.Map;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类是所有描述信息需要集成的父类，该类提供了{@link IAttribute}接口实现。
 * 这意味着可以在这些定义信息上附加自定义属性。{@link #getAppendAttribute()}方法是提供了一个内部的属性集。
 * 在hypha中不会直接访问到这个属性集合，它将为定义结构起到扩展的目的。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractDefine<T> implements IAttribute {
    private IAttribute attribute = null; //属性，为了提供IAttribute接口功能。
    private IAttribute flash     = null; //附加属性
    //
    /**获取用于存放临时定义属性的附加的属性集。*/
    public IAttribute getFlash() {
        if (this.flash == null)
            this.flash = new AttBase();
        return this.flash;
    };
    protected IAttribute getAttribute() {
        if (this.attribute == null)
            this.attribute = new AttBase();
        return this.attribute;
    }
    public boolean contains(String name) {
        return this.getAttribute().contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.getAttribute().setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.getAttribute().getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.getAttribute().removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getAttribute().getAttributeNames();
    };
    public void clearAttribute() {
        this.getAttribute().clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.getAttribute().toMap();
    };
}