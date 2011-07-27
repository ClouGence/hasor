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
package org.more.workflow.event.object;
import org.more.workflow.event.Event;
import org.more.workflow.event.EventPhase;
import org.more.workflow.metadata.PropertyMetadata;
/**
 * 当模型属性被更新时。该事件将会分为两个阶段一个是before另一个是after。
 * Date : 2010-5-21
 * @author 赵永春
 */
public class UpdataPropertyEvnet extends Event {
    /**  */
    private static final long serialVersionUID = 5010075302608463391L;
    private PropertyMetadata  propertyMetadata = null;
    private String            propertyEL       = null;
    private Object            oldValue         = null;
    private Object            newValue         = null;
    /**当模型属性被更新时。*/
    public UpdataPropertyEvnet(Object targetMode, String propertyEL, Object oldValue, Object newValue, PropertyMetadata propertyMetadata) {
        super("UpdataPropertyEvnet", targetMode);
        this.propertyEL = propertyEL;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.propertyMetadata = propertyMetadata;
    };
    @Override
    protected EventPhase[] createEventPhase() {
        return new EventPhase[] { new Event.BeforeEventPhase(), new Event.AfterEventPhase() };
    };
    /**获取属性元信息对象。*/
    public PropertyMetadata getPropertyMetadata() {
        return propertyMetadata;
    };
    /**获取属性的EL索引*/
    public String getPropertyEL() {
        return this.propertyEL;
    };
    /**获取原有属性值*/
    public Object getOldValue() {
        return this.oldValue;
    };
    /**获取要更新的属性值*/
    public Object getNewValue() {
        return this.newValue;
    };
    /**设置新值，可以通过该方法来改变最终更新的新属性值。*/
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    };
};