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
package org.more.beans.resource;
import org.more.beans.ValueMetaData;
import org.more.beans.define.QuickProperty_ValueMetaData;
import org.more.beans.resource.namespace.beans.TagBeans_AbstractPropertyDefine;
/**
 * 属性值解析器。负责将{@link QuickProperty_ValueMetaData}解析成对应的值描述。
 * 该接口的目的是为了辅助{@link TagBeans_AbstractPropertyDefine}解析器解析属性值元信息。
 * 属性值元信息的解析分为两个部分一个是由标签解析直接生成另一个是由{@link QuickPropertyParser}接口完成。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public interface QuickPropertyParser {
    /** 当遇到一个{@link QuickProperty_ValueMetaData}描述时。*/
    public ValueMetaData parser(QuickParserEvent event) throws Throwable;
}