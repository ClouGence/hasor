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
package org.more.workflow.el;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.workflow.context.ELContext;
/**
 * 表达式计算器，通过该类可以解析workflow中出现的EL表达式，并且取得这个表达式的值。
 * workflow的EL表达式需要遵循Ognl表达式语法。
 * Date : 2010-5-21
 * @author 赵永春
 */
public class ValueExpression {
    //========================================================================================Field
    private String expressionString = null;
    private Object thisValue        = null;
    //==================================================================================Constructor
    /**创建一个表达式计算器。*/
    public ValueExpression(String expressionString) throws OgnlException {
        this.expressionString = expressionString;
    };
    //==========================================================================================Job
    /**获取ValueExpression所操作的el字符串。*/
    public String getExpressionString() {
        return this.expressionString;
    };
    /**计算并且返回当前表达式的值。如果elContext参数为空则会引发NullPointerException类型异常。*/
    public Object eval(ELContext elContext) throws OgnlException, NullPointerException {
        if (elContext == null)
            throw new NullPointerException("请指定ELContext类型参数。");
        //
        OgnlContext ognlContext = elContext.getOgnlContext();
        ognlContext.setCurrentObject(this.thisValue);
        Object result = Ognl.getValue(this.expressionString, ognlContext);
        ognlContext.setCurrentObject(null);
        return result;
    };
    /**将一个对象加入到计算表达式中，如果这个对象名在ELContext中重复则由putLocal方法输出的对象优先级大于ELContext。*/
    public void putLocalThis(Object value) {
        this.thisValue = value;
    };
};