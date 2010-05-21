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
package org.more.workflow.form;
import org.more.DoesSupportException;
import org.more.core.ognl.OgnlException;
import org.more.workflow.context.ELContext;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.el.ValueExpression;
import org.more.workflow.metadata.AbstractMetadata;
/**
 * 流程表单属性元信息对象，流程属性可以是流程表单的一个基本类型字段也可以是复合类型字段。区别于ioc特性的是
 * FormPropertyMetadata类可以表示一个属性的寻找路径例如：form.role.name。<br/>
 * 因此在FormMetadata中注册的属性不会出现子级属性，FormPropertyMetadata类会自动解析这个属性导航并且完成对其的
 * 值更改。不过如果在对属性导航中途遇到空值情况将会引发Ognl异常。<br/>
 * 属性被分为两个组成部分：(1)属性EL；(2)属性值EL。第一个表达式要是一个属性导航路径。而第二个表达式可以是一个合法的ognl语法表达式。
 * Date : 2010-5-20
 * @author 赵永春
 */
public class FormPropertyMetadata extends AbstractMetadata {
    //========================================================================================Field
    private String propertyEL = null; //属性EL
    private String valueEL    = null; //属性值EL
    //==================================================================================Constructor
    public FormPropertyMetadata(String propertyEL, String valueEL) {
        super(propertyEL);
        this.propertyEL = propertyEL;
        this.valueEL = valueEL;
    };
    //==========================================================================================Job
    /**FormPropertyMetadata类型不支持该方法，如果调用该方法将会获得一个DoesSupportException异常。*/
    @Override
    public Object newInstance(ELContext elContext) {
        throw new DoesSupportException("FormPropertyMetadata类型不支持该方法。");
    };
    @Override
    public void updataMode(Object mode, ELContext elContext) throws OgnlException {
        //根据属性表达式获取ValueBinding
        PropertyBinding vp = this.getPropertyBinding(this.propertyEL, mode);
        if (vp.isReadOnly() == true)
            return;
        //根据elContext计算值表达式并且设置到属性中。
        ValueExpression ve = new ValueExpression(this.valueEL);
        ve.putLocalThis(mode);
        vp.setValue(ve.eval(elContext));
    };
};