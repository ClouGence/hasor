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
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
/**
 * 装载Bean定义配置。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class Config_LoadedXmlEvent extends Event {
    private DefineResource resource = null;
    /**创建{@link Config_LoadedXmlEvent}对象。*/
    public Config_LoadedXmlEvent(Object target, DefineResource resource) {
        super(target);
        this.resource = resource;
    };
    /**获取相关联的{@link DefineResource}对象。*/
    public DefineResource getResource() {
        return this.resource;
    };
};