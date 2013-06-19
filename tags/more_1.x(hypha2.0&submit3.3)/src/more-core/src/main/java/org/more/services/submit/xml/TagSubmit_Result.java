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
package org.more.services.submit.xml;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.services.submit.ResultProcess;
/**
 * 用于解析submit:result标签。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagSubmit_Result extends TagSubmit_NS implements XmlElementHook {
    public static final String ResultProcess_Name = "ResultProcess";
    private B_Config           config             = null;
    /**创建{@link TagSubmit_Result}对象*/
    public TagSubmit_Result(XmlDefineResource configuration, B_Config config) {
        super(configuration);
        this.config = config;
    };
    /**开始标签解析expression属性。*/
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        String match = event.getAttributeValue("match");
        String type = event.getAttributeValue("class");
        ResultProcess<?> rp = this.config.build.addResult(match, type);
        context.setAttribute(ResultProcess_Name, rp);
    };
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {};
};