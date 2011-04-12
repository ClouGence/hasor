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
package org.more.hypha;
import org.more.core.ognl.NoSuchPropertyException;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
/**
 * 属性绑操作器，通过该类可以对属性进行读写操作。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropertyBinding {
    private String      propertyEL   = null;             //属性EL
    private Node        propertyNode = null;             //编译的属性读取器
    /**子类可以通过该字段来改变属性的可读写状态，如果滞空该字段当setValue或者isReadOnly被调用时都会重新初始化它。*/
    protected Boolean   readOnly     = null;
    private OgnlContext ognlContext  = new OgnlContext();
    private Object      object       = null;             //属性所属的对象
    public PropertyBinding(String propertyEL, Object object) throws OgnlException {
        this.propertyEL = propertyEL;
        this.propertyNode = (Node) Ognl.parseExpression(propertyEL);//编译属性
        this.object = object;
    };
    /**解析属性EL，并且获取解析之后的属性值。*/
    public synchronized Object getValue() throws OgnlException {
        return this.propertyNode.getValue(this.ognlContext, this.object);
    };
    /**
     * 解析属性EL，将一个新的值替换原有属性值。该方法被调用无论成功与否都将
     * 缓存只读属性特征，如果写入成功则isReadOnly方法返回为false，否则返回true。
     */
    public synchronized void setValue(Object value) throws OgnlException {
        try {
            this.propertyNode.setValue(this.ognlContext, this.object, value);
            this.changeReadOnly(false);
        } catch (OgnlException e) {
            this.changeReadOnly(true);
            throw e;
        }
    };
    private void changeReadOnly(boolean readOnly) {
        if (this.readOnly == null)
            this.readOnly = readOnly;
    };
    /**获取用于表示属性的EL表达式。*/
    public String getPropertyEL() {
        return this.propertyEL;
    };
    /**
     * 测试属性是否为只读属性，PropertyBinding会通过调用getValue和setValue方法来测试是否为只读属性。
     * 如果是只读属性则setValue方法会引发NoSuchPropertyException异常。isReadOnly方法就是通过这个异常来
     * 断定该属性是否为只读。值得注意的是在isReadOnly上述方法只会引发一次PropertyBinding会将测试结果保存起来。
     * 如果调用过setValue方法则isReadOnly属性也会被顺便存放起来,已备将来不会做尝试性测试。
     */
    public boolean isReadOnly() throws OgnlException {
        if (this.readOnly != null)
            return readOnly;
        try {
            this.setValue(this.getValue());
            return (this.readOnly = false);
        } catch (NoSuchPropertyException e) {
            return (this.readOnly = true);
        }
    };
};