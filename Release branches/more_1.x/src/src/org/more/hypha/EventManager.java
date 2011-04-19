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
package org.more.hypha;
import org.more.util.attribute.IAttribute;
/**
 * 事件处理器接口，该接口的目的是为了处理{@link Event}类型事件。该接口提供的是一种先进先出的队列的方式处理事件。
 * 事件并不是当压入队列时就得到处理，需要明显的调用弹出事件驱动事件处理。也可以通过doEvent方法明确处理这个事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventManager {
    /**该类是，标志事件被压入事件管理器之后的顺序位置。*/
    public static abstract class Sequence {
        /**返回事件所处的索引位置。*/
        public abstract int getIndex();
        /**获取事件的类型。*/
        public abstract Event getEventType();
        /**获取事件的参数。*/
        public abstract Object[] getParams();
    };
    /**代表事件中参数的抽象类。*/
    public static abstract class Params {};
    /**初始化方法，参数是一个flash。*/
    public void init(IAttribute flash) throws Throwable;
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(Event eventType, EventListener<?> listener);
    /**
     * 将事件压入到队列中等待执行。
     * @param eventType 要压入的事件类型。
     * @param objects 该事件携带的参数信息
     * @return 返回该事件在队列中的位置。
     */
    public Sequence pushEvent(Event eventType, Object... objects);
    /**清空所有在队列中等待处理的事件。*/
    public void clearEvent();
    /**清空队列中指定类型的事件。*/
    public void clearEvent(Event eventType);
    /**删除队列中的某一个待执行的事件。*/
    public boolean removeEvent(Sequence sequence);
    /**弹出所有类型事件，在弹出过程中依次激活每个事件的事件处理器，如果一些事件没有相应的事件处理器那么这些事的处理将被忽略。*/
    public void popEvent();
    /**弹出某种特定类型的事件，在弹出过程中依次激活每个事件的事件处理器，如果这些事件没有相应的事件处理器那么这些事的处理将被忽略。*/
    public void popEvent(Event eventType);
    /**弹出特定顺序位置的事件，在弹出过程中依次激活每个事件的事件处理器，如果这些事件没有相应的事件处理器那么这些事的处理将被忽略。*/
    public void popEvent(Sequence sequence);
    /**绕过事件队列直接通知事件处理器处理这个事件。*/
    public void doEvent(Event eventType, Object... objects);
}