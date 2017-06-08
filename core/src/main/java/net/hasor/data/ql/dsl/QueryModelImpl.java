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
import net.hasor.core.utils.StringUtils;
import net.hasor.data.ql.dsl.domain.*;

import java.util.List;
/**
 * {@link QueryModel} 接口实现类，用于表示查询模型。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class QueryModelImpl implements QueryModel {
    private QueryDomain queryDomain;
    public QueryModelImpl(QueryDomain queryDomain) {
        this.queryDomain = queryDomain;
    }
    @Override
    public String buildQuery() {
        StringBuilder builder = new StringBuilder();
        buildQuery(this.queryDomain, true, builder, 1);
        return builder.toString();
    }
    @Override
    public String buildQueryWithoutFragment() {
        StringBuilder builder = new StringBuilder();
        buildQuery(this.queryDomain, false, builder, 1);
        return builder.toString();
    }
    @Override
    public QueryDomain getDomain() {
        return this.queryDomain;
    }
    //
    // --------------------------------------------------------------------------------------------
    private static String genDepthStr(int depth) {
        return StringUtils.leftPad("", depth * 4, " ");
    }
    public static void buildQuery(final QueryDomain queryDomain, final boolean useFragment, final StringBuilder builder, final int depth) {
        DataUDF graphUDF = queryDomain.getUDF();
        if (graphUDF != null) {
            builder.append(graphUDF.getName());
            builder.append(" (");
            List<String> paramNames = graphUDF.getParamNames();
            for (int i = 0; i < paramNames.size(); i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                String name = paramNames.get(i);
                DValue atParam = graphUDF.getParam(name);
                builder.append("\"");
                builder.append(name);
                builder.append("\" ");
                builder.append(" " + atParam.getEqType().getTypeString() + " ");
                buildParam(builder, useFragment, atParam, depth);
            }
            builder.append(")");
        }
        //
        if (builder.length() > 0) {
            builder.append(" ");
        }
        //
        if (ReturnType.Object == queryDomain.getReturnType()) {
            builder.append("{\n");
            buildFields(queryDomain, useFragment, builder, depth);
            builder.append("\n" + genDepthStr(depth - 1) + "}");
        } else if (ReturnType.ListObject == queryDomain.getReturnType()) {
            builder.append("[\n");
            {
                builder.append(genDepthStr(depth) + "{\n");
                buildFields(queryDomain, useFragment, builder, depth + 1);
                builder.append("\n" + genDepthStr(depth) + "}");
            }
            builder.append("\n" + genDepthStr(depth - 1) + "]");
        } else if (ReturnType.ListValue == queryDomain.getReturnType()) {
            builder.append("[\n");
            List<String> fieldNames = queryDomain.getFieldNames();
            String fieldName = fieldNames.get(0);
            builder.append(genDepthStr(depth));
            buildFieldValue(null, useFragment, queryDomain.getField(fieldName), builder, depth);
            builder.append("\n" + genDepthStr(depth - 1) + "]");
        }
        return;
    }
    private static void buildParam(final StringBuilder builder, final boolean useFragment, final DValue param, final int depth) {
        if (param instanceof RouteValue) {
            RouteValue val = (RouteValue) param;
            builder.append(val.getRouteExpression());
            return;
        }
        if (param instanceof FixedValue) {
            buildBaseValue(builder, (FixedValue) param);
            return;
        }
        if (param instanceof QueryValue) {
            QueryValue val = (QueryValue) param;
            QueryDomain queryDomain = val.getQueryDomain();
            buildQuery(queryDomain, useFragment, builder, depth + 1);
        }
    }
    private static void buildBaseValue(StringBuilder builder, FixedValue param) {
        FixedValue val = param;
        if (ValueType.Null == val.getValueType()) {
            builder.append("null");
        }
        if (ValueType.Boolean == val.getValueType()) {
            Boolean boolVal = (Boolean) val.getValue();
            builder.append(boolVal.toString());
        }
        if (ValueType.Number == val.getValueType()) {
            Number numVal = (Number) val.getValue();
            builder.append(numVal.toString());
        }
        if (ValueType.String == val.getValueType()) {
            String strVal = (String) val.getValue();
            StringUtils.quote(builder, strVal);
        }
    }
    //
    private static void buildFields(final QueryDomain queryDomain, final boolean useFragment, final StringBuilder builder, final int depth) {
        List<String> fieldNames = queryDomain.getFieldNames();
        for (int i = 0; i < fieldNames.size(); i++) {
            if (i != 0) {
                builder.append(",\n");
            }
            builder.append(genDepthStr(depth));
            String fieldName = fieldNames.get(i);
            buildField(fieldName, useFragment, queryDomain.getField(fieldName), builder, depth);
        }
    }
    private static void buildField(final String fieldName, final boolean useFragment, final DValue field, final StringBuilder builder, final int depth) {
        buildFieldKey(fieldName, field, builder);
        buildFieldValue(fieldName, useFragment, field, builder, depth);
    }
    private static void buildFieldKey(final String fieldName, final DValue field, final StringBuilder builder) {
        builder.append("\"" + fieldName.replace("\"", "\\\"") + "\"");
        if (field instanceof RouteValue) {
            RouteValue val = (RouteValue) field;
            if (!fieldName.equals(val.getRouteExpression())) {
                builder.append(" : ");
            }
            return;
        }
        if (field instanceof FixedValue) {
            builder.append(" : ");
        }
        if (field instanceof QueryValue) {
            builder.append(" : ");
        }
    }
    private static void buildFieldValue(final String fieldName, final boolean useFragment, final DValue field, final StringBuilder builder, final int depth) {
        if (field instanceof FixedValue) {
            buildBaseValue(builder, (FixedValue) field);
            return;
        }
        if (field instanceof QueryValue) {
            QueryValue val = (QueryValue) field;
            QueryDomain queryDomain = val.getQueryDomain();
            if (fieldName.equals(queryDomain.getQueryName()) || !useFragment) {
                buildQuery(queryDomain, useFragment, builder, depth + 1);
            } else {
                buildFragment(queryDomain, useFragment, builder, depth + 1);
                builder.append(queryDomain.getQueryName());
            }
        }
        if (field instanceof RouteValue) {
            RouteValue val = (RouteValue) field;
            if (fieldName == null || !fieldName.equals(val.getRouteExpression())) {
                builder.append(val.getRouteExpression());
            }
        }
        return;
    }
    //
    private static void buildFragment(QueryDomain queryDomain, boolean useFragment, StringBuilder builder, int depth) {
        StringBuilder fragment = new StringBuilder();
        fragment.append("fragment " + queryDomain.getQueryName() + " on ");
        buildQuery(queryDomain, useFragment, fragment, 1);
        fragment.append("\n\n");
        builder.insert(0, fragment.toString());
    }
}