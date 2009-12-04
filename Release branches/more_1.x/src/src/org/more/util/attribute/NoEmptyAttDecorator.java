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
/**
 * 属性装饰器，该装饰器的目的是限制属性值必须不能为空。
 * Date : 2009-5-1
 * @author 赵永春
 */
public class NoEmptyAttDecorator extends AbstractAttDecorator {
    //==================================================================================Constructor
    /**
     * 创建属性装饰器，该装饰器的目的是限制属性值必须不能为空。
     * @param source 要装饰的目标属性对象。
     * @throws NullPointerException 如果企图设置一个空值到装饰器将引发该异常。
     */
    public NoEmptyAttDecorator(IAttribute source) throws NullPointerException {
        super(source);
    }
    //==========================================================================================Job
    /**
     * 设置属性，该方法装饰了原有属性设置方法，并且限制了属性值必须不能为空。
     * @param name 要保存的属性名。
     * @param value 要保存的属性值。
     * @throws NullPointerException 发生该异常表示企图设置一个空属性值到属性集合中。
     */
    @Override
    public void setAttribute(String name, Object value) throws NullPointerException {
        if (value == null)
            throw new NullPointerException("不允许空属性值。");
        else
            super.setAttribute(name, value);
    }
}