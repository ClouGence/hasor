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
package net.hasor.data.ql.dsl;
/**
 * 用于协助构造 DataQL 查询模型。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface FieldBindingBuilder {
    /** 字段的值来自于UDF */
    public UDFBindingBuilder withUDF(String udfName);

    /** 字段的值来通过取值表达式表达 */
    public DataField withMapping(String fieldName);

    /** 字段值来自于另一个查询模型 */
    public DataField withFragment(QueryModel queryModel);

    /** 字段值为空 */
    public DataField withNull();

    /** 字段值为boolean */
    public DataField withBoolean(boolean value);

    /** 字段值为一个数字 */
    public DataField withNumber(Number value);

    /** 字段值为一个字符串 */
    public DataField withString(String value);

    /** 字段的结果作为 Object 形式 */
    public BindingBuilder asObject();

    /** 字段结果作为 List&lt;Object&gt;形式 */
    public BindingBuilder asListObject();

    /** 字段结果作为 List&lt;Value&gt;形式，其中 Value 为单值 */
    public BindingBuilder asListValue();
}