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
import java.util.HashMap;
import java.util.Map;
import org.more.core.ognl.OgnlException;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.workflow.context.ELContext;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.el.PropertyBindingHolder;
import org.more.workflow.event.AbstractHolderListener;
import org.more.workflow.event.EventType;
import org.more.workflow.event.object.UpdataModeEvnet;
/**
 * 基本元信息对象，模型的元信息对象需要集成该类。该类提供了更新模型的接口其子类决定更新模型的具体实现。
 * 子类可以通过元信息实现的{@link PropertyBindingHolder}接口来创建一个ognl表达式操作对象{@link PropertyBinding}。
 * 通过{@link PropertyBinding}这个表达式执行ognl表达式，模型的ioc操作需要依赖此接口来完成。
 * Date : 2010-5-16
 * @author 赵永春
 */
public abstract class AbstractMetadata extends AbstractHolderListener implements IAttribute, ModeUpdataHolder, PropertyBindingHolder {
    //========================================================================================Field
    private String                             metadataID   = null;                                  //元信息对象ID
    private final Map<String, PropertyBinding> propertyMap  = new HashMap<String, PropertyBinding>(); //用于保存事件的属性对象。
    private final AttBase                      attributeMap = new AttBase();                         //用于保存事件的属性对象。
    //==================================================================================Constructor
    /**创建一个元信息对象，参数决定了元信息的ID。这个id可以通过getMetadataID方法获取。*/
    public AbstractMetadata(String metadataID) {
        this.metadataID = metadataID;
    };
    //==========================================================================================Job
    /**获取元信息对象ID，该id是在创建AbstractMetadata对象时指定的，并且不可修改。*/
    public String getMetadataID() {
        return this.metadataID;
    };
    @Override
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws OgnlException {
        PropertyBinding pb = null;
        if (this.propertyMap.containsKey(propertyEL) == false) {
            pb = new PropertyBinding(propertyEL, object);
            this.propertyMap.put(propertyEL, pb);
        } else
            pb = this.propertyMap.get(propertyEL);
        return pb;
    };
    @Override
    public void updataMode(Object mode, ELContext elContext) throws Throwable {
        this.event(new UpdataModeEvnet(EventType.UpdataModeEvnet, mode));
    };
    /**创建元信息所描述的对象，其子类决定了创建的具体类型对象。用户可以通过扩展该方法来自定义对象创建过程。 */
    public abstract Object newInstance(ELContext elContext) throws Throwable;
    //==========================================================================================Job
    @Override
    public void clearAttribute() {
        this.attributeMap.clearAttribute();
    };
    @Override
    public boolean contains(String name) {
        return this.attributeMap.contains(name);
    };
    @Override
    public Object getAttribute(String name) {
        return this.attributeMap.getAttribute(name);
    };
    @Override
    public String[] getAttributeNames() {
        return this.attributeMap.getAttributeNames();
    };
    @Override
    public void removeAttribute(String name) {
        this.attributeMap.removeAttribute(name);
    };
    @Override
    public void setAttribute(String name, Object value) {
        this.attributeMap.setAttribute(name, value);
    };
};