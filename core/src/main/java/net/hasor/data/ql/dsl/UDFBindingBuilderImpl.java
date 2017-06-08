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
 * {@link UDFBindingBuilder} 接口实现，协助 DSL 构造查询模型。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class UDFBindingBuilderImpl extends BindingBuilderWraper implements UDFBindingBuilder {
    private DataUDF graphUDF;
    public UDFBindingBuilderImpl(BindingBuilder ownerBuilder, DataUDF graphUDF) {
        super(ownerBuilder);
        this.graphUDF = graphUDF;
    }
    //
    @Override
    public UDFBindingBuilder addParam(DataParam dataParam) {
        return this.addParam(dataParam, EqType.EQ);
    }
    @Override
    public UDFBindingBuilder addParam(DataParam dataParam, EqType eqType) {
        if (dataParam == null) {
            return this;
        }
        //    ---------------------------------------------------------------------
        if (dataParam instanceof RouteParam) {
            String routeValue = ((RouteParam) dataParam).getRouteExpression();
            this.graphUDF.addParam(dataParam.getName(), new RouteValue(eqType, routeValue));
            //---------------------------------------------------------------------
        } else if (dataParam instanceof ValueParam) {
            ValueParam valueGraphParam = (ValueParam) dataParam;
            this.graphUDF.addParam(valueGraphParam.getName(), new FixedValue(eqType, valueGraphParam.getValue(), valueGraphParam.getValueType()));
            //---------------------------------------------------------------------
        } else if (dataParam instanceof QueryParam) {
            QueryDomain subQuery = ((QueryParam) dataParam).getQueryDomain();
            this.graphUDF.addParam(dataParam.getName(), new QueryValue(eqType, subQuery));
        }
        return this;
    }
    @Override
    public String getName() {
        return this.graphUDF.getName();
    }
}