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
package net.hasor.graphql.dsl;
import net.hasor.graphql.dsl.domain.ValueType;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class ParamBindingBuilderImpl implements ParamBindingBuilder {
    private BindingBuilder inQuery;
    public ParamBindingBuilderImpl(BindingBuilder inQuery) {
        this.inQuery = inQuery;
    }
    //
    @Override
    public GraphParam withNull() {
        return new ValueParam(this.inQuery.getName(), null, ValueType.Null);
    }
    @Override
    public GraphParam withBoolean(boolean value) {
        return new ValueParam(this.inQuery.getName(), value, ValueType.Boolean);
    }
    @Override
    public GraphParam withNumber(Number value) {
        return new ValueParam(this.inQuery.getName(), value, ValueType.Number);
    }
    @Override
    public GraphParam withString(String value) {
        return new ValueParam(this.inQuery.getName(), value, ValueType.String);
    }
    @Override
    public GraphParam withParam(String paramExpression) {
        return new RouteParam(this.inQuery.getName(), paramExpression);
    }
    @Override
    public GraphParam withFragment(GraphQuery graphQuery) {
        return new QueryParam(this.inQuery.getName(), graphQuery.getDomain());
    }
    @Override
    public UDFBindingBuilder withUDF(String udfName) {
        UDFBindingBuilder byUDF = this.inQuery.byUDF(udfName);
        return new ParamUDFBindingBuilderImpl(this.inQuery.getName(), byUDF);
    }
}