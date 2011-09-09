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
import org.more.core.error.DefineException;
import org.more.core.error.RepeateException;
/**
 * 扩展的属性操作接口，该接口增强了Attribute接口。该接口解决了属性在设置时重名冲突的问题。
 * 当设置的新属性与原有属性发生重名冲突时可以使用“属性的替换原则”进行替换。具体有如下几个替换原则:
 * ReplaceMode_Replace  (无条件替换)该原则是默认替换原则<br/>
 * ReplaceMode_Original(忽略新属性值保留原始属性)<br/>
 * ReplaceMode_Throw   (如果出现重名则替换过程中抛出异常)。
 * @version 2009-4-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class ExtAttDecorator<T> extends AbstractAttDecorator<T> {
    //========================================================================================Field
    /** 当替换模式(replacMode属性)处于ReplaceMode_Replace时，如果出现重名属性则替换原有属性。该替换原则是默认替换原则。 */
    public static final int ReplaceMode_Replace  = 0;
    /** 
     * 当替换模式(replacMode属性)处于ReplaceMode_Original时，如果出现重名属性则忽略新属性保留原始属性，
     * 在这种模式下可以选择先移除属性在设置属性。 
     */
    public static final int ReplaceMode_Original = 1;
    /** 当替换模式(replacMode属性)处于ReplaceMode_Throw时，如果出现重名属性则抛出RepeateException异常。 */
    public static final int ReplaceMode_Throw    = 2;
    /** 属性替换策略 */
    private int             replaceMode          = ReplaceMode_Replace;
    //==================================================================================Constructor
    /**
     * 扩展属性装饰器，该装饰器实现了IExtAttribute接口的功能。替换策略是由IExtAttribute接口定义。
     * 该方法将采用默认策略IExtAttribute.ReplaceMode_Replace。
     * @param source 要装饰的目标属性对象。
     */
    public ExtAttDecorator(IAttribute<T> source) {
        super(source);
    }
    /**
     * 构造一个属性装饰器，该装饰器的主要功能是增加属性对象对替换策略的支持。
     * 如果使用了一个不存在的值进行定义则会引发NoDefinitionException异常。
     * @param source 要装饰的目标属性对象。
     * @param replaceMode 要更改的替换策略策略值，该值必须是ReplaceMode所定义的。
     * @throws DefineException 定义了一个不存在的属性策略。
     */
    public ExtAttDecorator(IAttribute<T> source, int replaceMode) throws DefineException {
        super(source);
        this.setReplacMode(replaceMode);
    }
    //==========================================================================================Job
    /**
     * 获取属性的替换模式该值是由ExtAttDecorator类的ReplaceMode字段定义。
     * @return 返回属性的替换模式该值是由ExtAttDecorator类的ReplaceMode字段定义。
     */
    public int getReplaceMode() {
        return this.replaceMode;
    }
    /**
     * 改变扩展属性实现类中属性替换策略，如果使用了一个不存在的值进行定义则会引发NoDefinitionException异常。
     * @param replaceMode 要更改的替换策略策略值，该值必须是ReplaceMode所定义的。
     * @throws DefineException 定义了一个不存在的属性策略。
     */
    protected void setReplacMode(int replaceMode) throws DefineException {
        if (replaceMode == ReplaceMode_Original || replaceMode == ReplaceMode_Replace || replaceMode == ReplaceMode_Throw)
            this.replaceMode = replaceMode;
        else
            throw new DefineException("不支持的属性替换策略 " + replaceMode);
    }
    /**
     * 设置属性，当替换模式(replaceMode属性)处于ReplaceMode_Replace时，如果出现重名属性则替换原有属性。<br/>
     * 当替换模式(replaceMode属性)处于ReplaceMode_Original时，如果出现重名属性则忽略新属性保留原始属性，
     * 在这种模式下用户可以选择先移除属性在设置属性。<br/> 当替换模式(replaceMode属性)处于ReplaceMode_Throw时，
     * 如果出现重名属性则抛出RepeateException异常。
     * @param name 要保存的属性名
     * @param value 要保存的属性值
     * @throws RepeateException 当替换模式(replaceMode属性)处于ReplaceMode_Throw时，并且添加了重名属性。
     */
    public void setAttribute(String name, T value) throws RepeateException {
        if (this.getSource().contains(name) == true)
            switch (this.replaceMode) {
            case ReplaceMode_Original://保留原始属性
                break;
            case ReplaceMode_Throw://抛出异常
                throw new RepeateException("已经存在的属性 " + name);
            default://ExtAttribute.ReplaceMode_Replace 无条件替换
                this.getSource().setAttribute(name, value);
            }
        else
            this.getSource().setAttribute(name, value);
    }
}