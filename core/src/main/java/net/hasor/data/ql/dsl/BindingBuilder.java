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
public interface BindingBuilder {
    /** 查询结果中增加一个字段 */
    public BindingBuilder addField(DataField dataField);

    /** 使用一个函数调用来充当数据源 */
    public UDFBindingBuilder byUDF(String udfName);

    /** 返回结果作为 List&lt;Object&gt; */
    public BindingBuilder asListObject();

    /** 返回结果作为 List&lt;Value&gt;，其中 Value 为单值 */
    public BindingBuilder asListValue();

    /** 返回结果作为 Object */
    public BindingBuilder asObject();

    /** 不加任何处理直接将原始值作为结果 */
    public BindingBuilder asOriginal();

    /** 查询作为字段 */
    public DataField asField();

    /** 查询作为参数 */
    public DataParam asParam();

    /** 查询作为结果 */
    public QueryModel buildQuery();

    /** 查询名称，也可用于表达 */
    public String getName();
}