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
import org.more.RepeateException;
import org.more.core.ognl.OgnlException;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.el.PropertyBindingHolder;
import org.more.workflow.event.AbstractHolderListener;
import org.more.workflow.event.object.UpdataModeEvnet;
/**
 * 抽象的元信息类，任何元信息类都需要继承该类。该类提供了元信息更新模型的接口。其子类可以通过重写相关方法来改写具体实现。
 * AbstractMetadata提供updataMode和newInstance两个方法来更新和创建元信息所代表的模型。<br/>
 * 另子类可以通过{@link PropertyBindingHolder}接口来创建一个ognl表达式操作对象{@link PropertyBinding}。
 * 通过{@link PropertyBinding}这个表达式执行ognl表达式，模型的ioc操作需要依赖此接口来完成。
 * 此外AbstractMetadata类型提供了注册移除属性元信息的方法。通过这些方法可以修改AbstractMetadata元信息对象中的属性。
 * Date : 2010-5-16
 * @author 赵永春
 */
public abstract class AbstractMetadata extends AbstractHolderListener implements IAttribute, ModeUpdataHolder, PropertyBindingHolder {
    //========================================================================================Field
    private String                              metadataID           = null;                                   //元信息对象ID
    private final Map<String, PropertyMetadata> propertyMap          = new HashMap<String, PropertyMetadata>(); //保存用于更新模型时使用的El属性集合
    private final Map<String, PropertyBinding>  propertyBindingCache = new HashMap<String, PropertyBinding>(); //用于保存事件的属性对象。
    private final AttBase                       attributeMap         = new AttBase();                          //用于保存事件的属性对象。
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
    /**根据属性EL获取其{@link PropertyBinding}对象，如果企图获取的属性不存在则返回值为null。*/
    @Override
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws OgnlException {
        PropertyBinding pb = null;
        if (this.propertyBindingCache.containsKey(propertyEL) == false) {
            pb = new PropertyBinding(propertyEL, object);
            this.propertyBindingCache.put(propertyEL, pb);
        } else
            pb = this.propertyBindingCache.get(propertyEL);
        return pb;
    };
    /**更新模型信息，在更新模型信息时AbstractMetadata会依次调用每个PropertyMetadata元信息的updataMode接口，另外该方法会引发UpdataModeEvnet事件。*/
    @Override
    public void updataMode(Object mode, ELContext elContext) throws Throwable {
        UpdataModeEvnet event = new UpdataModeEvnet(mode, this);
        this.event(event.getEventPhase()[0]);//before
        for (PropertyMetadata item : this.propertyMap.values())
            item.updataMode(mode, elContext);
        this.event(event.getEventPhase()[1]);//after
    };
    /**创建元信息所描述的对象，其子类决定了创建的具体类型对象。用户可以通过扩展该方法来自定义对象创建过程。 */
    public abstract Object newInstance(RunContext runContext) throws Throwable;
    /**
     * 添加一个属性元信息，该方法可以指定当前form的一个属性名称，并同时指定其一个表达式。
     * 当调用{@link ModeUpdataHolder}接口方法时FormMetadata会根据注册的属性列表对预更新模型执行更新操作。
     * 注意不能重复注册同一个属性，否则会引发{@link RepeateException}异常。
     * @param propertyName 要添加的属性名。
     * @param expressionString 该属性对应的{@link PropertyBinding 表达式}。
     */
    public void addProperty(String propertyName, String expressionString) {
        if (this.propertyMap.containsKey(propertyName) == true)
            throw new RepeateException("不能注册重复的属性元信息。 ");
        this.addProperty(new PropertyMetadata(propertyName, expressionString));
    };
    /**
     * 添加一个属性元信息，该方法可以指定当前form的一个属性名称，并同时指定其一个表达式。当调用
     * {@link ModeUpdataHolder}接口方法时FormMetadata会根据注册的属性列表对预更新模型执行更新操作。
     * 注意不能重复注册同一个属性，否则会引发{@link RepeateException}异常。
     * @param propertyItem 要添加的属性对象该属性对应的{@link PropertyBinding 表达式}。
     */
    public void addProperty(PropertyMetadata propertyItem) {
        if (this.propertyMap.containsKey(propertyItem.getMetadataID()) == true)
            throw new RepeateException("不能注册重复的属性元信息。 ");
        this.propertyMap.put(propertyItem.getMetadataID(), propertyItem);
    };
    /**取消一个属性元信息的添加，这里只需要传递属性名即可，对于FormItemMetadata类型属性名就是其metadataID。*/
    public void removeProperty(String propertyName) {
        if (this.propertyMap.containsKey(propertyName) == true)
            this.propertyMap.remove(propertyName);
    };
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