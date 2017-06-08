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
import net.hasor.data.ql.dsl.domain.ValueType;
/**
 * {@link FieldBindingBuilder} 接口实现类，用于协助构造 DataQL 查询模型。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class FieldBindingBuilderImpl implements FieldBindingBuilder {
    private BindingBuilder inQuery;
    public FieldBindingBuilderImpl(BindingBuilder inQuery) {
        this.inQuery = inQuery;
    }
    @Override
    public UDFBindingBuilder withUDF(String udfName) {
        return this.inQuery.byUDF(udfName);
    }
    @Override
    public DataField withMapping(String fieldName) {
        return new RouteField(this.inQuery.getName(), fieldName);
    }
    @Override
    public DataField withFragment(QueryModel queryModel) {
        return new QueryField(this.inQuery.getName(), queryModel.getDomain());
    }
    @Override
    public DataField withNull() {
        return new ValueField(this.inQuery.getName(), null, ValueType.Null);
    }
    @Override
    public DataField withBoolean(boolean value) {
        return new ValueField(this.inQuery.getName(), value, ValueType.Boolean);
    }
    @Override
    public DataField withNumber(Number value) {
        return new ValueField(this.inQuery.getName(), value, ValueType.Number);
    }
    @Override
    public DataField withString(String value) {
        return new ValueField(this.inQuery.getName(), value, ValueType.String);
    }
    @Override
    public BindingBuilder asObject() {
        return this.inQuery.asObject();
    }
    @Override
    public BindingBuilder asListObject() {
        return this.inQuery.asListObject();
    }
    @Override
    public BindingBuilder asListValue() {
        return this.inQuery.asListValue();
    }
}