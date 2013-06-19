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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.ListenerHolder;
/**
 * 该类是所有元信息类型的基类，任何元信息类都需要继承该类。AbstractMetadata类型有两个子类。
 * {@link ObjectMetadata}类型是为了描述模型的元信息，而{@link PropertyMetadata}是为了描述模型中属性的元信息。
 * Date : 2010-6-15
 * @author 赵永春
 */
public abstract class AbstractMetadata implements ListenerHolder {
    private String                    metadataID = null;                          //元信息对象ID
    private final List<EventListener> listeners  = new ArrayList<EventListener>(); //保存的事件监听器集合
    /**创建一个元信息对象，参数决定了元信息的ID。这个id可以通过getMetadataID方法获取。*/
    public AbstractMetadata(String metadataID) {
        if (metadataID == null)
            throw new NullPointerException("请指定metadataID参数值。");
        this.metadataID = metadataID;
    };
    /**获取元信息对象ID，该id是在创建AbstractMetadata对象时指定的，并且不可修改。*/
    public String getMetadataID() {
        return this.metadataID;
    };
    @Override
    public Iterator<EventListener> getListeners() {
        return this.listeners.iterator();
    };
    /**增加一个事件监听器。*/
    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    };
    /**删除一个事件监听器。*/
    public void removeListener(EventListener listener) {
        if (this.listeners.contains(listener) == true)
            this.listeners.remove(listener);
    };
};