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
package org.more.beans.resource.xml;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
/**
 * 在解析XML时用于表示即将需要处理的一个标签事件，如果开发人员需要在配置文件中自定义一个标签则这个自定义的标签解析器需要继承该类。
 * <br/>Date : 2009-11-21
 * @author 赵永春
 */
public class TagProcess implements XMLStreamConstants {
    /**当发现标签开始。*/
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {};
    /**当发现标签结束。*/
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {}
    /**当解析标签文本内容，注意CDATA和CDATA外层的文本视作两个文本内容。*/
    public void doCharEvent(String xPath, XMLStreamReader reader, ContextStack context) {};
}