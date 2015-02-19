/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.orm.ar.dialect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.db.orm.Paginator;
import net.hasor.db.orm.Paginator.Order;
import net.hasor.db.orm.Paginator.Order.OrderBy;
import net.hasor.db.orm.ar.Column;
import net.hasor.db.orm.ar.SQLBuilder;
import net.hasor.db.orm.ar.Sechma;
/**
 * 
 * @version : 2015年2月13日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractSQLBuilder implements SQLBuilder {
    private Map<Dialect, String> dialectData = new HashMap<Dialect, String>();
    //
    public AbstractSQLBuilder() {
        this.fillDialect(dialectData);
    }
    protected String dia(Dialect dia) {
        return this.dialectData.get(dia);
    }
    protected abstract void fillDialect(Map<Dialect, String> dialectData);
    //
    private BuilderData buildWhere(Column[] whereColumn, Object[] whereParams, Oper forOper) {
        if (whereColumn.length != whereParams.length) {
            throw new IllegalArgumentException("parameters and column ,param count no same.");
        }
        //
        StringBuffer sqlWhere = new StringBuffer("");
        int colCount = whereColumn.length;
        ArrayList<Object> paramArray = new ArrayList<Object>(whereParams.length);
        for (int i = 0; i < colCount; i++) {
            Column col = whereColumn[i];
            Object val = whereParams[i];
            //
            if (Oper.Delete == forOper && !col.allowDeleteWhere()) {
                continue;
            }
            if (Oper.Update == forOper && !col.allowUpdateWhere()) {
                continue;
            }
            //
            if (i > 0) {
                sqlWhere.append(dia(Dialect.AND));
            }
            sqlWhere.append(dia(Dialect.LEFT_QUOTE) + col.getName() + dia(Dialect.RIGHT_QUOTE));
            if (val == null) {
                sqlWhere.append(dia(Dialect.IS) + dia(Dialect.NULL));
            } else {
                sqlWhere.append(dia(Dialect.EQ) + dia(Dialect.PARAM));
            }
            if (val != null) {
                paramArray.add(val);
            }
        }
        if (sqlWhere.length() <= 1) {
            sqlWhere.append("1" + dia(Dialect.EQ) + "1");
        }
        return new ArrayBuilderData(sqlWhere.toString(), paramArray.toArray());
    }
    //
    @Override
    public BuilderData buildDelete(Sechma sechma, Column[] whereColumn, Object[] whereParams) {
        BuilderData whereData = buildWhere(whereColumn, whereParams, Oper.Delete);
        //
        StringBuffer deleteSQL = new StringBuffer();
        deleteSQL.append(dia(Dialect.DELETE));
        deleteSQL.append(dia(Dialect.FROM));
        deleteSQL.append(dia(Dialect.LEFT_QUOTE) + sechma.getName() + dia(Dialect.RIGHT_QUOTE));
        deleteSQL.append(dia(Dialect.WHERE));
        deleteSQL.append(whereData.getSQL());
        return new ArrayBuilderData(deleteSQL.toString(), whereData.getData());
    }
    @Override
    public BuilderData buildSelect(final Sechma sechma, final Column[] whereColumn, final Object[] whereParams) {
        StringBuffer sqlSelect = new StringBuffer("");
        sqlSelect.append(dia(Dialect.SELECT));
        //
        Column[] allColumn = sechma.getColumns();
        for (int i = 0; i < allColumn.length; i++) {
            if (i > 0) {
                sqlSelect.append(dia(Dialect.SEPARATOR));
            }
            sqlSelect.append(dia(Dialect.LEFT_QUOTE) + allColumn[i].getName() + dia(Dialect.RIGHT_QUOTE));
        }
        //
        sqlSelect.append(dia(Dialect.FROM));
        sqlSelect.append(dia(Dialect.LEFT_QUOTE) + sechma.getName() + dia(Dialect.RIGHT_QUOTE));
        BuilderData whereData = buildWhere(whereColumn, whereParams, Oper.Select);
        sqlSelect.append(dia(Dialect.WHERE) + whereData.getSQL());
        return new ArrayBuilderData(sqlSelect.toString(), whereData.getData());
    }
    @Override
    public BuilderData buildUpdate(Sechma sechma, Column[] whereColumn, Object[] whereParams, Column[] dataColumn, Object[] dataParams) {
        if (dataColumn.length != dataParams.length) {
            throw new IllegalArgumentException("parameters and column ,param count no same.");
        }
        //
        ArrayList<Object> paramArray = new ArrayList<Object>(dataParams.length + whereParams.length);
        StringBuffer updateSet = new StringBuffer("");
        int colCount = dataColumn.length;
        for (int i = 0; i < colCount; i++) {
            Column col = dataColumn[i];
            Object val = dataParams[i];
            if (col.allowUpdate() == false) {
                continue;
            }
            updateSet.append(dia(Dialect.SEPARATOR));
            updateSet.append(dia(Dialect.LEFT_QUOTE) + col.getName() + dia(Dialect.RIGHT_QUOTE));
            updateSet.append(dia(Dialect.EQ));
            updateSet.append((val == null) ? dia(Dialect.NULL) : dia(Dialect.PARAM));
            if (val != null) {
                paramArray.add(val);
            }
        }
        if (updateSet.length() > 1) {
            updateSet.delete(0, dia(Dialect.SEPARATOR).length());
        }
        //
        StringBuffer updateSQL = new StringBuffer("");
        updateSQL.append(dia(Dialect.UPDATE));
        updateSQL.append(dia(Dialect.LEFT_QUOTE) + sechma.getName() + dia(Dialect.RIGHT_QUOTE));
        updateSQL.append(dia(Dialect.SET));
        updateSQL.append(updateSet);
        //
        BuilderData whereData = buildWhere(whereColumn, whereParams, Oper.Update);
        paramArray.addAll(Arrays.asList(whereData.getData()));
        updateSQL.append(dia(Dialect.WHERE));
        updateSQL.append(whereData.getSQL());
        //
        return new ArrayBuilderData(updateSQL.toString(), paramArray.toArray());
    }
    @Override
    public BuilderData buildCount(Sechma sechma, Column[] whereColumn, Object[] whereParams) {
        BuilderData whereData = buildWhere(whereColumn, whereParams, Oper.Select);
        StringBuffer countSQL = new StringBuffer("");
        countSQL.append(dia(Dialect.SELECT));
        countSQL.append(dia(Dialect.COUNT_1));
        countSQL.append(dia(Dialect.FROM));
        countSQL.append(dia(Dialect.LEFT_QUOTE) + sechma.getName() + dia(Dialect.RIGHT_QUOTE));
        countSQL.append(dia(Dialect.WHERE));
        countSQL.append(whereData.getSQL());
        //
        return new ArrayBuilderData(countSQL.toString(), whereData.getData());
    }
    @Override
    public BuilderData buildInsert(Sechma sechma, Column[] dataColumn, Object[] dataParams) {
        StringBuffer insertColumn = new StringBuffer("");
        StringBuffer insertValue = new StringBuffer("");
        ArrayList<Object> paramArray = new ArrayList<Object>(dataParams.length);
        //
        int colCount = dataColumn.length;
        for (int i = 0; i < colCount; i++) {
            Column col = dataColumn[i];
            Object val = dataParams[i];
            if (!col.allowInsert()) {
                continue;
            }
            insertColumn.append(dia(Dialect.SEPARATOR));
            insertColumn.append(dia(Dialect.LEFT_QUOTE) + col.getName() + dia(Dialect.RIGHT_QUOTE));
            //
            insertValue.append(dia(Dialect.SEPARATOR));
            insertValue.append((val == null) ? dia(Dialect.NULL) : dia(Dialect.PARAM));
            if (val != null) {
                paramArray.add(val);
            }
        }
        if (insertColumn.length() > 1) {
            String SEPARATOR = dia(Dialect.SEPARATOR);
            insertColumn.delete(0, SEPARATOR.length());
            insertValue.delete(0, SEPARATOR.length());
        }
        //
        StringBuffer insertSQL = new StringBuffer("");
        insertSQL.append(dia(Dialect.INSERT));
        insertSQL.append(dia(Dialect.LEFT_QUOTE) + sechma.getName() + dia(Dialect.RIGHT_QUOTE));
        insertSQL.append(dia(Dialect.LEFT_ANGLE) + insertColumn + dia(Dialect.RIGHT_ANGLE));
        insertSQL.append(dia(Dialect.VALUES));
        insertSQL.append(dia(Dialect.LEFT_ANGLE) + insertValue + dia(Dialect.RIGHT_ANGLE));
        return new ArrayBuilderData(insertSQL.toString(), paramArray.toArray());
    }
    @Override
    public BuilderData buildEmptySelect(String tableName) {
        StringBuffer selectSQL = new StringBuffer("");
        selectSQL.append(dia(Dialect.SELECT));
        selectSQL.append(dia(Dialect.SPACE) + dia(Dialect.ALL) + dia(Dialect.SPACE));
        selectSQL.append(dia(Dialect.FROM));
        selectSQL.append(dia(Dialect.LEFT_QUOTE) + tableName + dia(Dialect.RIGHT_QUOTE));
        selectSQL.append(dia(Dialect.WHERE));
        selectSQL.append("1" + dia(Dialect.EQ) + "2");
        return new ArrayBuilderData(selectSQL.toString(), null);
    }
    @Override
    public BuilderData buildPaginator(String selectSQL, Paginator paginator, Object[] whereParams) {
        return new ArrayBuilderData(orderBySQL(selectSQL, paginator).toString(), whereParams);
    }
    @Override
    public BuilderMapData buildPaginator(String selectSQL, Paginator paginator, Map<String, ?> whereParams) {
        return new MapBuilderData(orderBySQL(selectSQL, paginator).toString(), whereParams);
    }
    protected StringBuffer orderBySQL(String selectSQL, Paginator paginator) {
        StringBuffer pageSQL = new StringBuffer("");
        pageSQL.append(dia(Dialect.SELECT));
        pageSQL.append(dia(Dialect.SPACE) + dia(Dialect.ALL) + dia(Dialect.SPACE));
        pageSQL.append(dia(Dialect.FROM));
        pageSQL.append(dia(Dialect.LEFT_ANGLE) + selectSQL + dia(Dialect.RIGHT_ANGLE));
        pageSQL.append(dia(Dialect.AS) + "temp");
        //order
        if (paginator != null) {
            List<Order> orderList = paginator.getOrderBy();
            StringBuffer orderSQL = new StringBuffer("");
            if (orderList != null && !orderList.isEmpty()) {
                orderSQL.append(dia(Dialect.ORDER_BY));
                for (int i = 0; i < orderList.size(); i++) {
                    Order order = orderList.get(i);
                    OrderBy orderBy = order.getOrderBy();
                    orderSQL.append(dia(Dialect.SEPARATOR));
                    orderSQL.append(dia(Dialect.LEFT_QUOTE) + order.getSortField() + dia(Dialect.RIGHT_QUOTE));
                    orderSQL.append((OrderBy.ASC == orderBy) ? dia(Dialect.ASC) : dia(Dialect.DESC));
                }
            }
            if (orderSQL.length() > 1) {
                orderSQL.delete(0, dia(Dialect.SEPARATOR).length());
                pageSQL.append(orderSQL);
            }
        }
        return pageSQL;
    }
}