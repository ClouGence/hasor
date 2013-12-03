/*
 * Copyright 2002-2006 the original author or authors.
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
package net.hasor.jdbc.dao;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.hasor.jdbc.opface.RowMapper;
import net.hasor.jdbc.opface.core.JdbcTemplate;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-11-25
 * @author 赵永春(zyc@hasor.net)
 */
public class QueryState {
    private JdbcTemplate          jdbcTemplate;
    private String                tableName;
    private int                   pageSize;
    private int                   pageStart;
    private String                sortName;
    private SortEnum              sortType;
    private LinkedList<Condition> conditionList = new LinkedList<Condition>();
    private LinkedList<Where>     whereList     = new LinkedList<Where>();
    //
    public QueryState(JdbcTemplate jdbcTemplate, String tableName) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
    }
    /**清除排序标志*/
    public void clearSort() {
        this.sortName = null;
        this.sortType = null;
    }
    /**清除分页标志*/
    public void clearPage() {
        this.pageSize = Integer.MIN_VALUE;
        this.pageStart = Integer.MIN_VALUE;
    }
    /**清除分页标志*/
    public void clearWhere() {
        this.whereList.clear();
    }
    public void addWhere(String whereString, Object... params) {
        if (whereString == null)
            return;
        this.whereList.add(new Where(whereString, params));
    }
    public void addSort(String sortName, SortEnum sortType) {
        this.sortName = sortName;
        this.sortType = sortType;
    };
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    };
    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }
    public void addConditions(String fieldName, Object[] values, ConditionPatternEnum pattern) {
        if (values == null)
            return;
        for (Object value : values)
            this.addCondition(fieldName, value, pattern);
    }
    public void addCondition(String fieldName, Object value, ConditionPatternEnum pattern) {
        for (Condition cond : this.conditionList) {
            if (!fieldName.equalsIgnoreCase(cond.getFieldName()))
                continue;
            if (!cond.getPattern().equals(pattern))
                continue;
            cond.addValue(value);
            return;
        }
        this.conditionList.add(new Condition(fieldName, value, pattern));
    }
    public List<Map<String, Object>> queryListForMap() {
        final EntityInfo entityInfo = EntityHelper.getEntityInfo(this.entityType);
        return queryList(new RowMapper<Map<String, Object>>() {
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> obj = new HashMap<String, Object>();
                ResultSetMetaData rsmd = rs.getMetaData();
                int colCount = rsmd.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    String colName = rsmd.getColumnName(i);
                    if (!entityInfo.getColumns().containsKey(colName))
                        continue;
                    obj.put(colName, rs.getObject(i));
                }
                return obj;
            }
        });
    }
    public List<?> queryListForObject() {
        final EntityInfo entityInfo = EntityHelper.getEntityInfo(this.entityType);
        return queryList(new RowMapper<Object>() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                Object obj = null;
                try {
                    obj = entityType.newInstance();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int colCount = rsmd.getColumnCount();
                    for (int i = 1; i <= colCount; i++) {
                        String colName = rsmd.getColumnName(i);
                        if (!entityInfo.getColumns().containsKey(colName))
                            continue;
                        Field field = entityInfo.getColumns().get(colName);
                        Object val = ConverterUtils.convert(field.getType(), rs.getObject(i));
                        BeanUtils.writePropertyOrField(obj, field.getName(), val);
                    }
                } catch (Exception e) {
                    throw new SQLException(e);
                }
                return obj;
            }
        });
    }
    public int queryCount() {
        QueryData data = createSelectQueryData();
        int queryCount = 0;
        String queryString = data.queryString;
        if (this.pageSize > 0 && this.pageStart >= 0) {
            int startSub = queryString.indexOf('(') + 1;
            int endSub = queryString.lastIndexOf(')');
            queryString = queryString.substring(startSub, endSub);
        }
        queryString = "select count(*) from (" + queryString + ") as tab";
        //        
        if (data.params == null || data.params.length == 0)
            queryCount = this.jdbcTemplate.queryForInt(queryString);
        else
            queryCount = this.jdbcTemplate.queryForInt(queryString, data.params);
        return queryCount;
    };
    private <T> List<T> queryList(RowMapper<T> rowMapper) {
        QueryData data = createSelectQueryData();
        List<T> resData = null;
        if (data.params == null || data.params.length == 0)
            resData = this.jdbcTemplate.query(data.queryString, rowMapper);
        else
            resData = this.jdbcTemplate.query(data.queryString, rowMapper, data.params);
        return resData;
    }
    public int doDelete() {
        QueryData data = createDeleteQueryData();
        int resData = 0;
        if (data.params == null || data.params.length == 0)
            resData = this.jdbcTemplate.update(data.queryString);
        else
            resData = this.jdbcTemplate.update(data.queryString, data.params);
        return resData;
    }
    public int doUpdate(Map<String, Object> newData) {
        if (newData == null || newData.isEmpty())
            return 0;
        //
        QueryData data = createUpdateQueryData(newData);
        if (data == null)
            return 0;
        int resData = 0;
        if (data.params == null || data.params.length == 0)
            resData = this.jdbcTemplate.update(data.queryString);
        else
            resData = this.jdbcTemplate.update(data.queryString, data.params);
        return resData;
    }
    //
    private static class QueryData {
        public String   queryString;
        public Object[] params;
    }
    private QueryData createUpdateQueryData(Map<String, Object> newData) {
        StringBuilder queryBuilder = new StringBuilder();
        ArrayList<Object> params = new ArrayList<Object>();
        final EntityInfo entityInfo = EntityHelper.getEntityInfo(this.entityType);
        //1. set ...
        String[] columnNames = entityInfo.getColumnNames();
        for (String col : columnNames) {
            if (newData.containsKey(col)) {
                queryBuilder.insert(0, "," + col + "=? ");
                params.add(0, newData.get(col));
            }
        }
        if (queryBuilder.length() == 0)
            return null;
        queryBuilder.deleteCharAt(0);
        queryBuilder.insert(0, "set ");
        //2.update from
        queryBuilder.insert(0, "update " + entityInfo.getTableName() + " ");
        //3. where
        if (this.conditionList.isEmpty() == false)
            queryBuilder.append("where ");
        for (Condition cond : this.conditionList) {
            queryBuilder.append(cond.toWereString());
            queryBuilder.append(" and");
            Object[] vars = cond.getValues();
            if (vars != null && vars.length != 0) {
                for (Object obj : vars)
                    params.add(obj);
            }
        }
        if (this.conditionList.isEmpty() && this.whereList.isEmpty() == false)
            queryBuilder.append("where ");
        for (Where where : this.whereList) {
            queryBuilder.append(" (");
            queryBuilder.append(where.getWhereString());
            queryBuilder.append(") and");
            Object[] objs = where.getParams();
            if (objs != null) {
                for (Object obj : objs)
                    params.add(obj);
            }
        }
        if (!this.conditionList.isEmpty() || !this.whereList.isEmpty())
            queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());
        //
        QueryData data = new QueryData();
        data.queryString = queryBuilder.toString();
        data.params = params.toArray();
        return data;
    }
    private QueryData createDeleteQueryData() {
        StringBuilder queryBuilder = new StringBuilder();
        ArrayList<Object> params = new ArrayList<Object>();
        final EntityInfo entityInfo = EntityHelper.getEntityInfo(this.entityType);
        //1. where
        if (this.conditionList.isEmpty() == false)
            queryBuilder.append("where ");
        for (Condition cond : this.conditionList) {
            queryBuilder.append(cond.toWereString());
            queryBuilder.append(" and");
            Object[] vars = cond.getValues();
            if (vars != null && vars.length != 0) {
                for (Object obj : vars)
                    params.add(obj);
            }
        }
        if (this.conditionList.isEmpty() && this.whereList.isEmpty() == false)
            queryBuilder.append("where ");
        for (Where where : this.whereList) {
            queryBuilder.append(" (");
            queryBuilder.append(where.getWhereString());
            queryBuilder.append(") and");
            Object[] objs = where.getParams();
            if (objs != null) {
                for (Object obj : objs)
                    params.add(obj);
            }
        }
        if (!this.conditionList.isEmpty() || !this.whereList.isEmpty())
            queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());
        //3. select * from ....
        queryBuilder.insert(0, "delete from " + entityInfo.getTableName() + " ");
        //
        QueryData data = new QueryData();
        data.queryString = queryBuilder.toString();
        data.params = params.toArray();
        return data;
    }
    private QueryData createSelectQueryData() {
        StringBuilder queryBuilder = new StringBuilder();
        ArrayList<Object> params = new ArrayList<Object>();
        final EntityInfo entityInfo = EntityHelper.getEntityInfo(this.entityType);
        final String[] columnNames = entityInfo.getColumnNames();
        //1. where
        for (Condition cond : this.conditionList) {
            queryBuilder.append(" and ");
            queryBuilder.append(cond.toWereString());
            Object[] vars = cond.getValues();
            if (vars != null && vars.length != 0) {
                for (Object obj : vars)
                    params.add(obj);
            }
        }
        for (Where where : this.whereList) {
            queryBuilder.append(" and (");
            queryBuilder.append(where.getWhereString());
            queryBuilder.append(")");
            Object[] objs = where.getParams();
            if (objs != null) {
                for (Object obj : objs)
                    params.add(obj);
            }
        }
        if (queryBuilder.length() > 4) {
            queryBuilder.delete(0, 4);
            queryBuilder.insert(0, " where");
        }
        //2. order by
        String orderBy = "";
        if (!StringUtils.isBlank(this.sortName) && this.sortType == null) {
            orderBy = "order by " + this.sortName + " " + this.sortType.getVal();
            queryBuilder.append(orderBy);
        } else {
            this.sortType = SortEnum.Asc;
            orderBy = "order by " + columnNames[0] + " " + SortEnum.Asc;
        }
        //3. select * from ....
        queryBuilder.insert(0, entityInfo.getTableName());
        queryBuilder.insert(0, " from ");
        for (String colName : columnNames)
            queryBuilder.insert(0, "," + colName);
        queryBuilder.insert(0, "select ROW_NUMBER() over (" + orderBy + ") as ROWID");
        //
        if (this.pageSize > 0 && this.pageStart >= 0) {
            int beginRange = this.pageStart;
            int endRange = beginRange + this.pageSize;
            queryBuilder.insert(0, "select * from (");
            queryBuilder.append(") as tab where tab.ROWID>=" + beginRange + " and tab.ROWID<" + endRange + " order by tab.ROWID " + this.sortType.getVal());
        }
        //
        QueryData data = new QueryData();
        data.queryString = queryBuilder.toString();
        data.params = params.toArray();
        return data;
    }
}