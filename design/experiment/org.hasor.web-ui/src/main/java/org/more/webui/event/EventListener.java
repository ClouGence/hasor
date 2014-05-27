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
package org.more.webui.event;
import org.more.webui.component.UIComponent;
import org.more.webui.context.ViewContext;
/**
* 用于处理事件的事件监听器。
* @version 2010-10-10
* @author 赵永春 (zyc@byshell.org)
*/
public interface EventListener {
    /**处理事件的处理方法，参数是要处理的事件。*/
    public void onEvent(Event event, UIComponent component, ViewContext viewContext) throws Throwable;
};