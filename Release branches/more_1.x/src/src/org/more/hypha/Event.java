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
import java.util.HashMap;
/**
 * 事件是一种通知机制，使用事件不能控制主控流程的执行。不过却可以通过事件得知内部的工作状态。
 * 该接口表示的是一个{@link EventManager}可以被识别处理的事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Event {
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
    //----------------------------------------
    private static HashMap<Class<?>, Event> eventMap = new HashMap<Class<?>, Event>();
    public Event() {
        if (eventMap.containsKey(this.getClass()) == false)
            eventMap.put(this.getClass(), this);
    }
    public static Event getEvent(Class<? extends Event> eventType) {
        if (eventMap.containsKey(eventType) == false)
            try {
                eventType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("不能注册事件类型" + eventType);
            }
        return eventMap.get(eventType);
    }
    /**将事件序列转换为{@link Params}类型对象。*/
    public abstract Params toParams(Sequence eventSequence);
};