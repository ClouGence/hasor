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
 * 新插件的注册事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AddPluginEvent extends Event {
    private Object plugin = null;
    /**创建{@link AddPluginEvent}对象。*/
    public AddPluginEvent(Object target, Object plugin) {
        super(target);
        this.plugin = plugin;
    }
    /**获取添加的新插件。*/
    public Object getPlugin() {
        return this.plugin;
    }
}