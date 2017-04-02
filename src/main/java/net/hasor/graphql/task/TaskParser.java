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
package net.hasor.graphql.task;
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.dsl.domain.*;
import net.hasor.graphql.task.source.*;
import net.hasor.graphql.task.struts.ListStrutsTask;
import net.hasor.graphql.task.struts.ObjectStrutsTask;
import net.hasor.graphql.task.struts.OriginalStrutsTask;
import net.hasor.graphql.task.struts.ValueStrutsTask;

import java.util.List;
/**
 * 解析查询模型，将其转换成为执行任务树
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TaskParser {
    private TaskContext taskContext;
    public TaskParser(TaskContext taskContext) {
        this.taskContext = taskContext;
    }
    //
    /** 解析查询模型，将其转换成为执行任务树 */
    public QueryTask doParser(QueryDomain domain) {
        AbstractQueryTask task = doParser(null, null, domain).fixRouteDep();
        task.initTask();
        return task;
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
            ObjectStrutsTask ost = new ObjectStrutsTask(this.taskContext);
            for (String name : fieldNames) {
                GraphValue domainField = domain.getField(name);
                SourceQueryTask sourceTask = this.parserSource(name, parentSource, domainField);
                ost.addField(name, sourceTask);
            }
            return ost;
        }
        case ListObject: {
            List<String> fieldNames = domain.getFieldNames();
            ObjectStrutsTask ost = new ObjectStrutsTask(this.taskContext);
            for (String name : fieldNames) {
                GraphValue domainField = domain.getField(name);
                SourceQueryTask sourceTask = this.parserSource(name, parentSource, domainField);
                ost.addField(name, sourceTask);
            }
            return new ListStrutsTask(this.taskContext, ost);
        }
        case ListValue: {
            List<String> fieldNames = domain.getFieldNames();
            String fieldName = fieldNames.get(0);
            GraphValue domainField = domain.getField(fieldName);
            SourceQueryTask sourceTask = this.parserSource(fieldName, parentSource, domainField);
            return new ListStrutsTask(this.taskContext, new ValueStrutsTask(this.taskContext, fieldName, sourceTask));
        }
        case Original:
            return new OriginalStrutsTask(nameOfParent, this.taskContext, parentSource);
        default:
            throw new RuntimeException("");
        }
        //
    }
    private SourceQueryTask parserUDF(String nameOfParent, AbstractQueryTask parentSource, GraphUDF graphUDF) {
        if (graphUDF == null) {
            return null;
        }
        CallerSourceTask caller = new CallerSourceTask(nameOfParent, this.taskContext, graphUDF.getName());
        List<String> paramNames = graphUDF.getParamNames();
        for (String name : paramNames) {
            GraphValue field = graphUDF.getParam(name);
            caller.addParam(name, parserSource(name, parentSource, field));
        }
        return caller;
    }
    private SourceQueryTask parserSource(String nameOfParent, AbstractQueryTask parentSource, GraphValue defSource) {
        if (defSource instanceof QueryValue) {
            //
            QueryValue queryValue = (QueryValue) defSource;
            QueryDomain queryDomain = queryValue.getQueryDomain();
            AbstractQueryTask queryTask = this.doParser(nameOfParent, parentSource, queryDomain);
            return new QuerySourceTask(nameOfParent, this.taskContext, queryTask);
        } else if (defSource instanceof FixedValue) {
            //
            FixedValue varValue = (FixedValue) defSource;
            Object value = varValue.getValue();
            ValueType valueType = varValue.getValueType();
            return new ValueSourceTask(nameOfParent, this.taskContext, value, valueType);
        } else if (defSource instanceof RouteValue) {
            //
            RouteValue routeValue = (RouteValue) defSource;
            return new RouteSourceTask(nameOfParent, this.taskContext, parentSource, routeValue.getRouteExpression());
        }
        throw new RuntimeException("");
    }
}