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
package org.more.hypha.commons;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.hypha.PropertyBinding;
/**
 * 属性绑操作器，通过该类可以对属性进行读写操作。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class EL_PropertyBindingImpl implements PropertyBinding {
    private String      propertyEL   = null; //属性EL
    private Node        propertyNode = null; //编译的属性读取器
    /**子类可以通过该字段来改变属性的可读写状态，如果滞空该字段当setValue或者isReadOnly被调用时都会重新初始化它。*/
    protected Boolean   readOnly     = null;
    private OgnlContext ognlContext  = null;
    private Object      object       = null; //属性所属的对象
    public EL_PropertyBindingImpl(AbstractELContext elContext, String propertyEL, Object object) throws OgnlException {
        this.ognlContext = new OgnlContext(elContext.getOgnlContext().toMap());
        this.ognlContext.setCurrentObject(object);//this
        this.object = object;
        this.propertyEL = propertyEL;
        this.propertyNode = (Node) Ognl.parseExpression(propertyEL);//编译属性
    };
    public String getPropertyEL() {
        return this.propertyEL;
    };
    public synchronized Object getValue() throws OgnlException {
        return this.propertyNode.getValue(this.ognlContext, this.object);
    };
    public synchronized void setValue(Object value) throws OgnlException {
        this.propertyNode.setValue(ognlContext, this.object, value);
    };
};