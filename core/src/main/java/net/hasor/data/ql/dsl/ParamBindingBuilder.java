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
public interface ParamBindingBuilder {
    /** 参数值为空 */
    public DataParam withNull();

    /** 参数值为 boolean 值 */
    public DataParam withBoolean(boolean value);

    /** 参数值为数值 */
    public DataParam withNumber(Number value);

    /** 参数值为字符串 */
    public DataParam withString(String value);

    /** 参数值为寻值 */
    public DataParam withParam(String paramExpression);

    /** 参数值为另一个查询 */
    public DataParam withFragment(QueryModel queryModel);

    /** 参数值来源为服务调用 */
    public UDFBindingBuilder withUDF(String udfName);
}