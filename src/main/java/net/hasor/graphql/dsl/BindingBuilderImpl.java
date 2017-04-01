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
import net.hasor.graphql.dsl.domain.*;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class BindingBuilderImpl implements BindingBuilder {
    private QueryDomain queryDomain = new QueryDomain();
    public BindingBuilderImpl(String queryName) {
        this.queryDomain.setQueryName(queryName);
    }
    //
    @Override
    public BindingBuilder addField(GraphField graphField) {
        if (graphField == null) {
            return this;
        }
        //    ---------------------------------------------------------------------
        if (graphField instanceof RouteField) {
            String routeValue = ((RouteField) graphField).getRouteExpression();
            this.queryDomain.addField(graphField.getName(), new RouteValue(routeValue));
            //---------------------------------------------------------------------
        } else if (graphField instanceof ValueField) {
            ValueField valueGraphField = (ValueField) graphField;
            this.queryDomain.addField(valueGraphField.getName(), new FixedValue(valueGraphField.getValue(), valueGraphField.getValueType()));
            //---------------------------------------------------------------------
        } else if (graphField instanceof QueryField) {
            QueryDomain subQuery = ((QueryField) graphField).getQueryDomain();
            this.queryDomain.addField(graphField.getName(), new QueryValue(subQuery));
        }
        return this;
    }
    @Override
    public UDFBindingBuilder byUDF(String udfName) {
        GraphUDF graphUDF = new GraphUDF(null, udfName);
        this.queryDomain.setGraphUDF(graphUDF);
        return new UDFBindingBuilderImpl(this, graphUDF);
    }
    @Override
    public BindingBuilder asListObject() {
        this.queryDomain.setReturnType(ReturnType.ListObject);
        return this;
    }
    @Override
    public BindingBuilder asListValue() {
        this.queryDomain.setReturnType(ReturnType.ListValue);
        return this;
    }
    @Override
    public BindingBuilder asObject() {
        this.queryDomain.setReturnType(ReturnType.Object);
        return this;
    }
    public BindingBuilder asOriginal() {
        this.queryDomain.setReturnType(ReturnType.Original);
        return this;
    }
    @Override
    public GraphField asField() {
        return new QueryField(this.getName(), this.queryDomain);
    }
    @Override
    public GraphParam asParam() {
        return new QueryParam(this.getName(), this.queryDomain);
    }
    @Override
    public GraphQuery buildQuery() {
        return new GraphQueryImpl(this.queryDomain);
    }
    @Override
    public String getName() {
        return this.queryDomain.getQueryName();
    }
}