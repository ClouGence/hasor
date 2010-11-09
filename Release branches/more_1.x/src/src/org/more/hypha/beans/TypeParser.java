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
package org.more.hypha.beans;
import org.more.hypha.beans.support.TagBeans_AbstractPropertyDefine;
import org.more.util.attribute.IAttribute;
/**
 * 属性值解析器。负责将可表述的字符串信息解析成相应的类型数据。该接口的目的是为了
 * 辅助{@link TagBeans_AbstractPropertyDefine}解析器解析属性值元信息。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public interface TypeParser {
    /** 解析一个字符串值类型。*/
    public ValueMetaData parser(String value, IAttribute attribute, AbstractPropertyDefine property);
}