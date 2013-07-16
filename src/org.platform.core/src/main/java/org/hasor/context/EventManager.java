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
package org.hasor.context;
/**
 * 标准事件处理器。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventManager {
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(String eventType, HasorEventListener hasorEventListener);
    /**删除某个监听器的注册。*/
    public void removeAllEventListener(String eventType);
    /**删除某个监听器的注册。*/
    public void removeEventListener(String eventType, HasorEventListener hasorEventListener);
    /**获取某种特定类型的事件监听器集合。*/
    public HasorEventListener[] getEventListener(String eventType);
    /**获取所有事件监听器类型。*/
    public String[] getEventTypes();
    //
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。*/
    public void doSyncEvent(String eventType, Object... objects);
    /**异步方式抛出事件。asynEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。*/
    public void doAsynEvent(String eventType, Object... objects);
    //
    /**清空未完成的事件等待执行队列*/
    public void clean();
}