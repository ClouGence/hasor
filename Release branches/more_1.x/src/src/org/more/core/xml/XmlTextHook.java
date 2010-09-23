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
package org.more.core.xml;
import org.more.core.xml.stream.TextEvent;
/**
 * 当遇到字符数据时使用该接口解析，字符数据类型包括了CDATA，Chars，space。
 * @version 2010-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public interface XmlTextHook extends XmlParserHook {
    /**
     * 当发生一个字符数据事件时，字符数据类型包括了CDATA，Chars，space。
     * @param context 环境上下文。
     * @param xpath 当前标签在所定义的命名空间中的xpath。
     * @param event 事件。
     */
    public void text(XmlStackDecorator context, String xpath, TextEvent event);
}