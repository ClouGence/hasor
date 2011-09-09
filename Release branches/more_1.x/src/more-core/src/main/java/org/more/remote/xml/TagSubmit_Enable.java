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
package org.more.remote.xml;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.remote.RemoteService;
import org.more.util.StringConvertUtil;
/**
 * 用于解析remote:remoteEnable标签。
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagSubmit_Enable extends TagRemote_NS implements XmlElementHook {
    /**创建{@link TagSubmit_Enable}对象 */
    public TagSubmit_Enable(XmlDefineResource configuration, RemoteService service) {
        super(configuration, service);
    }
    /**开始标签解析属性。*/
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        //1.取值
        String enable = event.getAttributeValue("enable");
        boolean _enable = StringConvertUtil.parseBoolean(enable, false);
        this.getService().setEnable(_enable);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
}