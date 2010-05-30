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
import org.more.DoesSupportException;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.el.ValueExpression;
/**
 * 属性元信息对象，AbstractMetadata类是用于描述一个模型的信息，而PropertyMetadata则是用于描述这个模型的属性信息
 * workFlow系统功过PropertyMetadata类的描述信息来对模型执行属性注入操作。PropertyMetadata定义属性犹如一个路径例：
 * <b>form.role.name。</b>因此在AbstractMetadata中注册的属性不会出现属性的属性情况，PropertyMetadata类会解析
 * 这个属性导航并且完成对其的值更改。不过如果在对属性导航中途遇到空值情况将会引发Ognl异常。<br/>属性被分为两个组成部分：
 * (1)属性EL；(2)属性值EL。第一个表达式要是一个属性导航路径。而第二个表达式可以是一个合法的ognl语法表达式。
 * 提示：在定义属性值EL时可以通过使用 this关键字来确定模型本身对象。例如：<br/>
 * propertyEL="account"<br/>
 * valueEL="this.account + 'hello Word'"
 * Date : 2010-5-20
 * @author 赵永春
 */
public class PropertyMetadata extends AbstractMetadata {
    //========================================================================================Field
    private String          propertyEL   = null; //属性EL
    private String          valueEL      = null; //属性值EL
    private PropertyBinding bindingCache = null;
    //==================================================================================Constructor
    public PropertyMetadata(String propertyEL, String valueEL) {
        super(propertyEL);
        this.propertyEL = propertyEL;
        this.valueEL = valueEL;
    };
    //==========================================================================================Job
    /**FormPropertyMetadata类型不支持该方法，如果调用该方法将会获得一个DoesSupportException异常。*/
    @Override
    public Object newInstance(RunContext runContext) {
        throw new DoesSupportException("FormPropertyMetadata类型不支持该方法。");
    };
    @Override
    public void updataMode(Object mode, ELContext elContext) throws Throwable {
        super.updataMode(mode, elContext);
        //根据属性表达式获取ValueBinding
        if (this.bindingCache == null)
            this.bindingCache = this.getPropertyBinding(this.propertyEL, mode);
        if (this.bindingCache.isReadOnly() == true)
            return;
        //根据elContext计算值表达式并且设置到属性中。
        ValueExpression ve = new ValueExpression(this.valueEL);
        elContext.putLocalThis(mode);
        this.bindingCache.setValue(ve.eval(elContext));
        elContext.putLocalThis(null);
    };
};