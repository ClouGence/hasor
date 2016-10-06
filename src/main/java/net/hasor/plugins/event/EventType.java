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
package net.hasor.plugins.event;
/**
 * 事件监听类型。
 *
 * @version : 2016年2月7日
 * @author 赵永春(zyc@hasor.net)
 */
public enum EventType {
    /** 注册一个常规的事件监听器，当遇到该事件则会调用事件监听器代码。 */
    Listener,
    /** 注册一个特殊的事件监听器，该监听器只会响应一次事件调用。 */
    Once
}