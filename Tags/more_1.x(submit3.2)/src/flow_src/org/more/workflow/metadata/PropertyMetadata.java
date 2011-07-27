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
import java.util.Iterator;
import org.more.core.ognl.OgnlException;
import org.more.workflow.context.ELContext;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.el.PropertyBindingHolder;
import org.more.workflow.el.ValueExpression;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.EventPhase;
import org.more.workflow.event.object.UpdataPropertyEvnet;
/**
 * 属性元信息对象，该类是{@link AbstractMetadata}类的一个子类是用于描述一个模型的属性信息，workFlow系统功过PropertyMetadata的描述来对模型执行属性注入操作。
 * PropertyMetadata定义属性犹如一个路径例：<b>form.role.name。</b>因此在workFlow中注册的属性不会出现属性的属性情况。<br/>
 * PropertyMetadata类会解析这个属性导航并且完成对其的值更改。不过如果在对属性导航中途遇到空值情况将会引发Ognl异常。<br/>属性被分为两个组成部分：
 * (1)属性EL；(2)属性值EL。第一个表达式要是一个属性导航路径。而第二个表达式可以是一个合法的ognl语法表达式,用于确定属性的值。
 * 提示：在定义属性值EL时可以通过使用 this关键字来确定模型本身对象。例如：<br/>
 * propertyEL="account"<br/>
 * valueEL="this.account + 'hello Word'"
 * Date : 2010-6-15
 * @author 赵永春
 */
public final class PropertyMetadata extends AbstractMetadata implements PropertyBindingHolder {
    private String          propertyEL   = null; //属性EL
    private String          valueEL      = null; //属性值EL
    private PropertyBinding bindingCache = null; //被缓存的属性操作器
    /**创建一个属性元信息对象，propertyEL参数表述属性的导航路径，valueEL参数决定了属性的值。*/
    public PropertyMetadata(String propertyEL, String valueEL) {
        super(propertyEL);
        if (propertyEL == null)
            throw new NullPointerException("propertyEL值为空,PropertyMetadata不能表述这个属性元信息。");
        this.propertyEL = propertyEL;
        this.valueEL = valueEL;
    };
    /**在当前属性身上引发一个事件。*/
    protected void event(EventPhase event) {
        Iterator<EventListener> iterator = this.getListeners();
        while (iterator.hasNext())
            iterator.next().doListener(event);
    };
    /**使用属性元信息更新模型的属性信息。*/
    public void updataProperty(Object mode, ELContext elContext) throws Throwable {
        //根据属性表达式获取ValueBinding
        if (this.bindingCache == null)
            this.bindingCache = this.getPropertyBinding(this.propertyEL, mode);
        if (this.bindingCache.isReadOnly() == true)
            return;
        //根据elContext计算值表达式并且设置到属性中。
        Object oldValue = null;
        Object newValue = null;
        oldValue = this.bindingCache.getValue();
        elContext.putLocalThis(mode);
        ValueExpression ve = new ValueExpression(this.valueEL);
        newValue = ve.eval(elContext);
        elContext.putLocalThis(null);
        //
        UpdataPropertyEvnet event = new UpdataPropertyEvnet(mode, this.propertyEL, oldValue, newValue, this);
        this.event(event.getEventPhase()[0]);//before
        this.bindingCache.setValue(event.getNewValue());
        this.event(event.getEventPhase()[1]);//after
    };
    @Override
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws OgnlException {
        return new PropertyBinding(propertyEL, object);
    }
};