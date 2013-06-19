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
package org.more.workflow.event;
import java.util.Iterator;
/**
 * 该接口对象用于保存事件监听器，可以通过该接口的方法获取到所有事件监听器并引发它们的事件处理方法。
 * Date : 2010-5-16
 * @author 赵永春
 */
public interface ListenerHolder {
    /**获取事件监听器的迭代器对象，通过这个迭代器可以引发它们的事件处理方法。*/
    public Iterator<EventListener> getListeners();
};