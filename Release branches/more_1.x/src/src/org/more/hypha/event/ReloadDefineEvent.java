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
package org.more.hypha.event;
import org.more.hypha.Event;
/**
 * 重新装载事件，当环境被请求重新装载时会引发该事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class ReloadDefineEvent extends Event {
    /**创建{@link ReloadDefineEvent}对象。*/
    public ReloadDefineEvent(Object target) {
        super(target);
    }
}