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
package org.more.hypha.el;
/**
 * EL执行环境，EL在执行期间的运行环境和环境对象均通过该接口实现。
 * @version : 2011-4-19
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ELContext {
    /**将一段el字符串创建为{@link EvalExpression}类型对象，如果参数为空则返回空。*/
    public EvalExpression getExpression(String elString) throws ELException;
    /** 计算一段字符串的EL值，并且返回计算结果。如果参数为空则返回空。 */
    public Object evalExpression(String elString) throws ELException;
    /**
     * 根据属性访问符获取一个属性读写器。使用这个属性读写器可以方便的设置一个属性或者读写该属性。
     * @param propertyEL 属性访问符可以是简单的java字段名或者“abc.def”形式的属性描述，如果参数为空则返回空。
     * @param object 属性所在的宿主对象。
     */
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws ELException;
    /**
     * 添加一个EL对象，当视图对该对象读或者写的时候会调用特定的接口方法。
     * @param name EL表达式通过该参数所表示的名称来访问这个EL对象。如果名称重复则新的会替换原有名称的对象。
     * @param elObject 要添加的{@link ELObject}对象，如果传入的是一个空值则表示删除这个名称的对象(替换)。
     */
    public void addELObject(String name, ELObject elObject);
};