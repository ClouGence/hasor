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
package org.platform.event;
import org.platform.event.Event.Sequence;
/**
* 异常处理器，该接口是为了处理{@link EventListener}事件在执行过程中引发的异常。
* @version : 2011-5-18
* @author 赵永春 (zyc@byshell.org)
*/
public interface EventExceptionHandler<T extends Event> {
    /**处理该事件异常。*/
    public void processException(Throwable exception, Sequence sequence, EventListener<T> listener);
}