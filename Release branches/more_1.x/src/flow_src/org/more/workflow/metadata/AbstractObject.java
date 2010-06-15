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
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.workflow.event.AbstractHolderListener;
import org.more.workflow.state.AbstractStateHolder;
import org.more.workflow.state.StateCache;
import org.more.workflow.state.StateHolder;
/**
 * 抽象基本类型，该类型对象是流程系统的抽象基类，作为模型对象的基类它还提供了{@link StateHolder}接口
 * 和{@link MetadataHolder}接口的实现。此外该类还提供了{@link IAttribute}接口的实现。某些子类为了兼容POJO而
 * 增加一个代理类，并且与POJO类实现一个公共接口。
 * Date : 2010-5-16
 * @author 赵永春
 */
public abstract class AbstractObject extends AbstractHolderListener implements IAttribute, StateHolder, MetadataHolder, StateCache {
    private String              objectID     = null;         //对象ID
    private AbstractStateHolder stateHolder  = null;         //状态操作接口
    private final AttBase       attributeMap = new AttBase(); //用于保存对象的属性集合。
    /**
    * 创建AbstractObject对象。
    * @param objectID 对象ID。
    * @param objectStateHolder 状态操作接口。
    */
    protected AbstractObject(String objectID, AbstractStateHolder stateHolder) {
        if (objectID == null || stateHolder == null)
            throw new NullPointerException("参数objectID或者参数objectStateHolder为null。");
        this.objectID = objectID;
        this.stateHolder = stateHolder;
    };
    /**获取对象ID。*/
    public String getID() {
        return this.objectID;
    };
    public AbstractStateHolder getStateHolder() {
        return this.stateHolder;
    };
    @Override
    public ObjectMetadata getMetadata() {
        return this.stateHolder.getMetadata();
    };
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