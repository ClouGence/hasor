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
package org.more.workflow.state;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.AbstractHolderListener;
import org.more.workflow.metadata.AbstractMetadata;
import org.more.workflow.metadata.MetadataHolder;
import org.more.workflow.metadata.ModeUpdataHolder;
/**
 * 抽象的状态操作接口，其子类可以扩展各种操作。该模型提供{@link ModeUpdataHolder}接口实现，该对象的实现
 * 是通过获取其{@link AbstractMetadata}对象然后在该对象上调用其updataMode方法以实现。该类另外还提供了
 * {@link MetadataHolder} 接口的实现。
 * Date : 2010-5-16
 * @author 赵永春
 */
public abstract class AbstractStateHolder extends AbstractHolderListener implements IAttribute, ModeUpdataHolder, MetadataHolder {
    //========================================================================================Field
    private AbstractMetadata metadataObject;              //元信息对象
    private final AttBase    attributeMap = new AttBase(); //用于保存事件的属性对象。
    //==================================================================================Constructor
    /***/
    protected AbstractStateHolder(AbstractMetadata metadataObject) {
        this.metadataObject = metadataObject;
    };
    //==========================================================================================Job
    @Override
    public AbstractMetadata getMetadata() {
        return this.metadataObject;
    };
    @Override
    public void updataMode(Object mode, ELContext elContext) throws Throwable {
        this.metadataObject.updataMode(mode, elContext);
    };
    /**从临时持久化数据中，装载模型状态。*/
    public abstract void loadState(Object mode, RunContext runContext);
    /**像临时持久化数据中，保存模型状态。*/
    public abstract void saveState(Object mode, RunContext runContext);
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