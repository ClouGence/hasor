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
package net.hasor.core;
/**
 * 应用程序事件监听器
 * @version : 2013-7-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface EventListener<T> extends java.util.EventListener {
    /**
     * 处理事件的处理方法，参数是要处理的事件。
     * @param event 事件类型
     * @param eventData 事件参数
     * @throws Throwable 执行事件期间引发的异常。
     */
    public void onEvent(String event, T eventData) throws Throwable;
}