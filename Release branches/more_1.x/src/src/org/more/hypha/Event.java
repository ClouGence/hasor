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
import org.more.InitializationException;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 事件是一种通知机制，使用事件不能控制主控流程的执行。不过却可以通过事件得知内部的工作状态。
 * 该接口表示的是一个{@link EventManager}可以被识别处理的事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Event {
    protected static ILog log = LogFactory.getLog(Event.class);
    /**该类是，标志事件被压入事件管理器之后的顺序位置。*/
    public static abstract class Sequence {
        /**返回事件所处的索引位置。*/
        public abstract int getIndex();
        /**获取事件的类型。*/
        public abstract Event getEventType();
        /**获取事件的参数。*/
        public abstract Object[] getParams();
        /**获取该事件使用的异常处理器。*/
        public abstract EventExceptionHandler<Event> getHandler();
    };
    /**代表事件中参数的抽象类。*/
    public static abstract class Params {};
    //----------------------------------------
    private static HashMap<Class<? extends Event>, Event> eventMap = new HashMap<Class<? extends Event>, Event>();
    /**获取指定类型事件对象，如果参数为空则直接返回空值。事件对象在hypha中是全程唯一的，这样做的目的是为了减少new的数量。*/
    public static Event getEvent(Class<? extends Event> eventType) throws InitializationException {
        if (eventType == null) {
            log.warning("getEvent an error , eventType is null.", eventType);
            return null;
        }
        Event event = null;
        if (eventMap.containsKey(eventType) == false)
            //创建并且注册这个事件.
            try {
                log.debug("not found {%0} Event Object.", eventType);
                Event eventObj = eventType.newInstance();
                eventMap.put(eventType, eventObj);
                log.debug("created Event object {%0} and regeidt it.", eventObj);
            } catch (Exception e) {
                log.warning("create Event {%0} error :", eventType, e);
            }
        event = eventMap.get(eventType);
        //返回
        log.debug("return {%0} Event Object.", event);
        return event;
    }
    /**将事件序列转换为{@link Params}类型对象。*/
    public abstract Params toParams(Sequence eventSequence);
};