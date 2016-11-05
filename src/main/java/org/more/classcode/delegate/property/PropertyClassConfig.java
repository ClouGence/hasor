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
package org.more.classcode.delegate.property;
import java.util.LinkedHashMap;
import java.util.Map;

import org.more.asm.ClassVisitor;
import org.more.classcode.AbstractClassConfig;
import org.more.util.BeanUtils;
/**
 *
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class PropertyClassConfig extends AbstractClassConfig {
    private Map<String, InnerPropertyDelegateDefine> newPropertyMap = null; //属性委托
    //
    /**创建{@link PropertyClassConfig}类型对象。 */
    public PropertyClassConfig() {
        super(DefaultSuperClass);
    }
    /**创建{@link PropertyClassConfig}类型对象。 */
    public PropertyClassConfig(Class<?> superClass) {
        super(superClass);
    }
    /**创建{@link PropertyClassConfig}类型对象。 */
    public PropertyClassConfig(Class<?> superClass, ClassLoader parentLoader) {
        super(superClass, parentLoader);
    }
    //
    protected String initClassName() {
        return this.getSuperClass().getName() + "$P_" + index();
    }
    @Override
    protected ClassVisitor buildClassVisitor(ClassVisitor parentVisitor) {
        return new PropertyDelegateClassAdapter(parentVisitor, this);
    }
    //
    /**是否包含改变*/
    public boolean hasChange() {
        return (this.newPropertyMap == null) ? false : (!this.newPropertyMap.isEmpty());
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, Class<?> propertyType) {
        this.addProperty(propertyName, propertyType, true, true);
    }
    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法。
     * @param readOnly 是否为只读属性
     */
    public void addProperty(final String propertyName, Class<?> propertyType, boolean readOnly) {
        this.addProperty(propertyName, propertyType, !readOnly, true);
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, Class<?> propertyType, boolean canRead, boolean canWrite) {
        if (propertyName == null || propertyName.equals("") || propertyType == null) {
            throw new NullPointerException("参数 propertyName 或 propertyType 为空。");
        }
        this.addProperty(propertyName, new SimplePropertyDelegate(propertyType), canRead, canWrite);
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, final PropertyDelegate<?> delegate) {
        this.addProperty(propertyName, delegate, true, true);
    }
    /**
     * 动态添加一个属性，并且生成可以属性的get/set方法。
     * @param readOnly 是否为只读属性
     */
    public void addProperty(final String propertyName, final PropertyDelegate<?> delegate, boolean readOnly) {
        this.addProperty(propertyName, delegate, !readOnly, true);
    }
    /**动态添加一个属性，并且生成可以属性的get/set方法。*/
    public void addProperty(final String propertyName, final PropertyDelegate<?> delegate, boolean canRead, boolean canWrite) {
        if (propertyName == null || propertyName.equals("") || delegate == null) {
            throw new NullPointerException("参数 propertyName 或 delegate 为空。");
        }
        //如果存在这个属性，则抛出异常
        boolean readMark = BeanUtils.canReadProperty(propertyName, this.getSuperClass());
        boolean writeMark = BeanUtils.canWriteProperty(propertyName, this.getSuperClass());
        if (readMark || writeMark) {
            //throw new IllegalStateException(propertyName + " 已存在的属性。"); //TODO
        }
        //
        if (this.newPropertyMap == null) {
            this.newPropertyMap = new LinkedHashMap<String, InnerPropertyDelegateDefine>();
        }
        //
        InnerPropertyDelegateDefine inner = new InnerPropertyDelegateDefine(propertyName, delegate, canRead, canWrite);
        this.newPropertyMap.put(propertyName, inner);
    }
    public PropertyDelegate<Object> getPropertyDelegate(String propertyName) {
        if (this.newPropertyMap != null) {
            return this.newPropertyMap.get(propertyName);
        }
        return null;
    }
    InnerPropertyDelegateDefine[] getNewPropertyList() {
        if (this.newPropertyMap == null) {
            return new InnerPropertyDelegateDefine[0];
        }
        InnerPropertyDelegateDefine[] newProperty = this.newPropertyMap.values()//
                .toArray(new InnerPropertyDelegateDefine[this.newPropertyMap.size()]);
        return newProperty;
    }
}