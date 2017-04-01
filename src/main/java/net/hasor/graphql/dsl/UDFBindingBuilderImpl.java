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
class UDFBindingBuilderImpl extends BindingBuilderWraper implements UDFBindingBuilder {
    private GraphUDF graphUDF;
    public UDFBindingBuilderImpl(BindingBuilder ownerBuilder, GraphUDF graphUDF) {
        super(ownerBuilder);
        this.graphUDF = graphUDF;
    }
    //
    @Override
    public UDFBindingBuilder addParam(GraphParam graphParam) {
        if (graphParam == null) {
            return this;
        }
        //    ---------------------------------------------------------------------
        if (graphParam instanceof RouteParam) {
            String routeValue = ((RouteParam) graphParam).getRouteExpression();
            this.graphUDF.addParam(graphParam.getName(), new RouteValue(routeValue));
            //---------------------------------------------------------------------
        } else if (graphParam instanceof ValueParam) {
            ValueParam valueGraphParam = (ValueParam) graphParam;
            this.graphUDF.addParam(valueGraphParam.getName(), new FixedValue(valueGraphParam.getValue(), valueGraphParam.getValueType()));
            //---------------------------------------------------------------------
        } else if (graphParam instanceof QueryParam) {
            QueryDomain subQuery = ((QueryParam) graphParam).getQueryDomain();
            this.graphUDF.addParam(graphParam.getName(), new QueryValue(subQuery));
        }
        return this;
    }
    @Override
    public String getName() {
        return this.graphUDF.getName();
    }
}