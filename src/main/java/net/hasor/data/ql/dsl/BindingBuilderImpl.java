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
import net.hasor.data.ql.dsl.domain.*;
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
    public BindingBuilder addField(DataField dataField) {
        if (dataField == null) {
            return this;
        }
        //    ---------------------------------------------------------------------
        if (dataField instanceof RouteField) {
            String routeValue = ((RouteField) dataField).getRouteExpression();
            this.queryDomain.addField(dataField.getName(), new RouteValue(EqType.EQ, routeValue));
            //---------------------------------------------------------------------
        } else if (dataField instanceof ValueField) {
            ValueField valueGraphField = (ValueField) dataField;
            this.queryDomain.addField(valueGraphField.getName(), new FixedValue(EqType.EQ, valueGraphField.getValue(), valueGraphField.getValueType()));
            //---------------------------------------------------------------------
        } else if (dataField instanceof QueryField) {
            QueryDomain subQuery = ((QueryField) dataField).getQueryDomain();
            this.queryDomain.addField(dataField.getName(), new QueryValue(EqType.EQ, subQuery));
        }
        return this;
    }
    @Override
    public UDFBindingBuilder byUDF(String udfName) {
        DataUDF dataUDF = new DataUDF(null, udfName);
        this.queryDomain.setUDF(dataUDF);
        return new UDFBindingBuilderImpl(this, dataUDF);
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
    public DataField asField() {
        return new QueryField(this.getName(), this.queryDomain);
    }
    @Override
    public DataParam asParam() {
        return new QueryParam(this.getName(), this.queryDomain);
    }
    @Override
    public QueryModel buildQuery() {
        return new QueryModelImpl(this.queryDomain);
    }
    @Override
    public String getName() {
        return this.queryDomain.getQueryName();
    }
}