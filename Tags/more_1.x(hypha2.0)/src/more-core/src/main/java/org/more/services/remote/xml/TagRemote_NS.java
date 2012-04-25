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
package org.more.services.remote.xml;
import org.more.hypha.commons.xml.Tag_Abstract;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.services.remote.RemoteService;
/**
 * 用于解析remote命名空间标签解析器基类，主要用于区分不同命名空间。
 * @version 2010-10-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagRemote_NS extends Tag_Abstract {
    private RemoteService service = null;
    public TagRemote_NS(XmlDefineResource configuration, RemoteService service) {
        super(configuration);
        this.service = service;
    }
    /**获取RMI服务。*/
    protected RemoteService getService() {
        return this.service;
    };
}