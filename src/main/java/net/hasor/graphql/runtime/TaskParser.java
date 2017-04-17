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
package net.hasor.graphql.runtime;
import net.hasor.graphql.dsl.domain.*;
import net.hasor.graphql.runtime.task.*;

import java.util.List;
/**
 * 解析查询模型，将其转换成为执行任务树
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TaskParser {
    //
    /** 解析查询模型，将其转换成为执行任务树 */
    public AbstractQueryTask doParser(QueryDomain domain) {
        return doParser(null, null, domain);//.fixRouteDep();
    }
    private AbstractQueryTask doParser(String nameOfParent, AbstractQueryTask parentSource, QueryDomain domain) {
        if (domain.getGraphUDF() != null) {
            parentSource = this.parserUDF(nameOfParent, parentSource, domain.getGraphUDF());
        }
        //
        ReturnType returnType = domain.getReturnType();
        switch (returnType) {
        case Object: {
            List<String> fieldNames = domain.getFieldNames();
            ObjectStrutsTask ost = new ObjectStrutsTask(nameOfParent, TaskType.S, parentSource);
            for (String name : fieldNames) {
                GraphValue domainField = domain.getField(name);
                AbstractQueryTask sourceTask = this.parserSource(name, parentSource, domainField);
                ost.addField(name, sourceTask);
            }
            return ost;
        }
        case ListObject: {
            List<String> fieldNames = domain.getFieldNames();
            ObjectStrutsTask ost = new ObjectStrutsTask(nameOfParent, TaskType.F, parentSource);
            for (String name : fieldNames) {
                GraphValue domainField = domain.getField(name);
                AbstractQueryTask sourceTask = this.parserSource(name, parentSource, domainField);
                ost.addField(name, sourceTask);
            }
            //
            ListStrutsTask lst = new ListStrutsTask(nameOfParent, parentSource);
            lst.setListBody(ost);
            return lst;
        }
        case ListValue: {
            List<String> fieldNames = domain.getFieldNames();
            String fieldName = fieldNames.get(0);
            GraphValue domainField = domain.getField(fieldName);
            AbstractQueryTask sourceTask = this.parserSource(fieldName, parentSource, domainField);
            //
            ListStrutsTask lst = new ListStrutsTask(nameOfParent, parentSource);
            lst.setListBody(sourceTask);
            return lst;
        }
        case Original:
            return new OriginalSourceTask(nameOfParent, parentSource);
        default:
            throw new RuntimeException("");
        }
        //
    }
    private AbstractQueryTask parserUDF(String nameOfParent, AbstractQueryTask parentSource, GraphUDF graphUDF) {
        if (graphUDF == null) {
            return null;
        }
        CallerSourceTask caller = new CallerSourceTask(nameOfParent, graphUDF.getName());
        List<String> paramNames = graphUDF.getParamNames();
        for (String name : paramNames) {
            GraphValue field = graphUDF.getParam(name);
            caller.addParam(name, parserSource(name, parentSource, field));
        }
        return caller;
    }
    private AbstractQueryTask parserSource(String nameOfParent, AbstractQueryTask parentSource, GraphValue defSource) {
        if (defSource instanceof QueryValue) {
            //
            QueryValue queryValue = (QueryValue) defSource;
            QueryDomain queryDomain = queryValue.getQueryDomain();
            return this.doParser(nameOfParent, parentSource, queryDomain);
        } else if (defSource instanceof FixedValue) {
            //
            FixedValue varValue = (FixedValue) defSource;
            Object value = varValue.getValue();
            ValueType valueType = varValue.getValueType();
            return new ValueSourceTask(nameOfParent, value, valueType);
        } else if (defSource instanceof RouteValue) {
            //
            boolean isFormater = parentSource != null;
            RouteSourceTask rst = new RouteSourceTask(nameOfParent, parentSource, isFormater);
            rst.setRouteExpression(((RouteValue) defSource).getRouteExpression());
            return rst;
        }
        throw new RuntimeException("");
    }
}