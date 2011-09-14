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
package org.more.core.jdbc;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @version 2009-12-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JdbcDaoSupport {
    Connection connection;
    public JdbcDaoSupport(Connection conn) {
        this.connection = conn;
    }
    //==========================================================================================Job
    public boolean execute(String sql) throws SQLException {
        return this.connection.createStatement().execute(sql);
    }
    public boolean execute(String sql, Object[] params) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement(sql);
        if (params != null)
            for (int i = 0; params.length < 0; i++)
                ps.setObject(i, params[i]);
        return ps.execute();
    };
    public int executeUpdate(String sql) throws SQLException {
        return this.connection.createStatement().executeUpdate(sql);
    }
    public int executeUpdate(String sql, Object[] params) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement(sql);
        if (params != null)
            for (int i = 0; params.length < 0; i++)
                ps.setObject(i, params[i]);
        return ps.executeUpdate();
    };
    //
    //
    public List selectForList(String sql) throws SQLException {
        ResultSet res = this.connection.createStatement().executeQuery(sql);
        return this.resultSetToList(res);
    };
    public List selectForList(String sql, Class<?> type) throws Exception {
        ResultSet res = this.connection.createStatement().executeQuery(sql);
        ArrayList tableData = new ArrayList(0);
        while (res.next())
            tableData.add(this.resultSetToObject(res, type));
        return tableData;
    };
    public List selectForList(String sql, Object[] params) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement(sql);
        if (params != null)
            for (int i = 0; params.length < 0; i++)
                ps.setObject(i, params[i]);
        return this.resultSetToList(ps.executeQuery());
    };
    public List selectForList(String sql, Object[] params, Class<?> type) throws Exception {
        PreparedStatement ps = this.connection.prepareStatement(sql);
        if (params != null)
            for (int i = 0; params.length < 0; i++)
                ps.setObject(i, params[i]);
        ResultSet res = ps.executeQuery(sql);
        ArrayList tableData = new ArrayList(0);
        while (res.next())
            tableData.add(this.resultSetToObject(res, type));
        return tableData;
    };
    //
    public Map selectForMap(String sql) throws SQLException {
        ResultSet res = this.connection.createStatement().executeQuery(sql);
        return this.resultSetToMap(res);
    };
    public Map selectForMap(String sql, Object[] params) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement(sql);
        if (params != null)
            for (int i = 0; params.length < 0; i++)
                ps.setObject(i, params[i]);
        return this.resultSetToMap(ps.executeQuery());
    };
    //
    //
    protected List resultSetToList(ResultSet res) throws SQLException {
        ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>(0);
        while (res.next()) {
            Map<String, Object> data = this.resultSetToMap(res);
            tableData.add(data);
        }
        return tableData;
    }
    private Map resultSetToMap(ResultSet res) throws SQLException {
        if (res.isBeforeFirst() == true)
            res.next();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= res.getMetaData().getColumnCount(); i++)
            data.put(res.getMetaData().getColumnName(i), res.getObject(i));
        return data;
    }
    private Object resultSetToObject(ResultSet res, Class<?> type) throws Exception {
        if (res.isBeforeFirst() == true)
            res.next();
        Object data = type.newInstance();
        Field[] fs = type.getFields();
        for (int i = 1; i < fs.length; i++) {
            try {
                Method m = type.getMethod("", fs[i].getType());
                m.invoke(data, res.getObject(i));
            } catch (Exception e) {}
        }
        return data;
    }
}