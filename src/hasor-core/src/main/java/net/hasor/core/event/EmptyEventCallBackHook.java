/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.event;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.Hasor;
/**
 * 异步事件回调接口。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
class EmptyEventCallBackHook implements EventCallBackHook {
    @Override
    public void handleException(final String eventType, final Object[] objects, final Throwable e) {
        Hasor.logWarn("During the execution of Event ‘%s’ throw an error.%s", eventType, e);
    }
    @Override
    public void handleComplete(final String eventType, final Object[] objects) {}
}