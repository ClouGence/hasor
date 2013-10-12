/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.jdbc.jdbc;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.hasor.Hasor;
import net.hasor.jdbc.dao.DataAccessException;
import net.hasor.jdbc.jdbc.parameter.SqlParameter;
import net.hasor.jdbc.jdbc.rowset.SqlRowSet;
/**
 * 
 * @version : 2013-10-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class JdbcTemplate2 implements JdbcOperations {
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public void execute(String sql) throws DataAccessException {
        // TODO Auto-generated method stub
    }
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        // TODO Auto-generated method stub
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public Map<String, Object> queryForMap(String sql) throws DataAccessException {
        return queryForObject(sql, getColumnMapRowMapper());
    }
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, getSingleColumnRowMapper(requiredType));
    }
    public long queryForLong(String sql) throws DataAccessException {
        Number number = queryForObject(sql, Long.class);
        return (number != null ? number.longValue() : 0);
    }
    public int queryForInt(String sql) throws DataAccessException {
        Number number = queryForObject(sql, Integer.class);
        return (number != null ? number.intValue() : 0);
    }
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        return query(sql, getSingleColumnRowMapper(elementType));
    }
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        return query(sql, getColumnMapRowMapper());
    }
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public int update(String sql) throws DataAccessException {
        // TODO Auto-generated method stub
        return 0;
    }
    public int[] batchUpdate(String[] sql) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        return execute(new SimplePreparedStatementCreator(sql), action);
    }
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(psc, null, rse);
    }
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(new SimplePreparedStatementCreator(sql), pss, rse);
    }
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(sql, newArgTypePreparedStatementSetter(args, argTypes), rse);
    }
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(sql, newArgPreparedStatementSetter(args), rse);
    }
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
        return query(sql, newArgPreparedStatementSetter(args), rse);
    }
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        query(psc, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        query(sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        query(sql, newArgTypePreparedStatementSetter(args, argTypes), rch);
    }
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
        query(sql, newArgPreparedStatementSetter(args), rch);
    }
    public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException {
        query(sql, newArgPreparedStatementSetter(args), rch);
    }
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, args, argTypes, getSingleColumnRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
        return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
    }
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return queryForObject(sql, args, argTypes, getColumnMapRowMapper());
    }
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
        return queryForObject(sql, args, getColumnMapRowMapper());
    }
    public long queryForLong(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Number number = queryForObject(sql, args, argTypes, Long.class);
        return (number != null ? number.longValue() : 0);
    }
    public long queryForLong(String sql, Object... args) throws DataAccessException {
        Number number = queryForObject(sql, args, Long.class);
        return (number != null ? number.longValue() : 0);
    }
    public int queryForInt(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Number number = queryForObject(sql, args, argTypes, Integer.class);
        return (number != null ? number.intValue() : 0);
    }
    public int queryForInt(String sql, Object... args) throws DataAccessException {
        Number number = queryForObject(sql, args, Integer.class);
        return (number != null ? number.intValue() : 0);
    }
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        return query(sql, args, argTypes, getSingleColumnRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
        return query(sql, args, getSingleColumnRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        return query(sql, args, getSingleColumnRowMapper(elementType));
    }
    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return query(sql, args, argTypes, getColumnMapRowMapper());
    }
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
        return query(sql, args, getColumnMapRowMapper());
    }
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public int update(PreparedStatementCreator psc) throws DataAccessException {
        return update(psc, (PreparedStatementSetter) null);
    }
    public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        return update(new SimplePreparedStatementCreator(sql), pss);
    }
    public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return update(sql, newArgTypePreparedStatementSetter(args, argTypes));
    }
    public int update(String sql, Object... args) throws DataAccessException {
        return update(sql, newArgPreparedStatementSetter(args));
    }
    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException {
        return execute(new SimpleCallableStatementCreator(callString), action);
    }
    public Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    //
    //
    //
    //
    //
    protected int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss) throws DataAccessException {
        // TODO Auto-generated method stub
        return 0;
    }
    public <T> T query(PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    /** Create a new RowMapper for reading columns as key-value pairs. */
    protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        // TODO Auto-generated method stub
        return null;//new ColumnMapRowMapper();
    }
    /** Create a new RowMapper for reading result objects from a single column.*/
    protected <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType) {
        // TODO Auto-generated method stub
        return null;//new SingleColumnRowMapper<T>(requiredType);
    }
    protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) {
        // TODO Auto-generated method stub
        return null;// new ArgPreparedStatementSetter(args);
    }
    /**Create a new ArgTypePreparedStatementSetter using the args and argTypes passed in.
     * This method allows the creation to be overridden by sub-classes.
     */
    protected PreparedStatementSetter newArgTypePreparedStatementSetter(Object[] args, int[] argTypes) {
        // TODO Auto-generated method stub
        return null;// new ArgTypePreparedStatementSetter(args, argTypes);
    }
    //
    //
    //
    //
    //
    /**Simple adapter for PreparedStatementCreator, allowing to use a plain SQL statement. */
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
        private final String sql;
        public SimplePreparedStatementCreator(String sql) {
            Hasor.assertIsNotNull(sql, "SQL must not be null");
            this.sql = sql;
        }
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }
        public String getSql() {
            return this.sql;
        }
    }
    /**Simple adapter for CallableStatementCreator, allowing to use a plain SQL statement.*/
    private static class SimpleCallableStatementCreator implements CallableStatementCreator, SqlProvider {
        private final String callString;
        public SimpleCallableStatementCreator(String callString) {
            Hasor.assertIsNotNull(callString, "Call string must not be null");
            this.callString = callString;
        }
        public CallableStatement createCallableStatement(Connection con) throws SQLException {
            return con.prepareCall(this.callString);
        }
        public String getSql() {
            return this.callString;
        }
    }
    /**
     * Adapter to enable use of a RowCallbackHandler inside a ResultSetExtractor.
     * <p>Uses a regular ResultSet, so we have to be careful when using it:
     * We don't use it for navigating since this could lead to unpredictable consequences.
     */
    private static class RowCallbackHandlerResultSetExtractor implements ResultSetExtractor<Object> {
        private final RowCallbackHandler rch;
        public RowCallbackHandlerResultSetExtractor(RowCallbackHandler rch) {
            this.rch = rch;
        }
        public Object extractData(ResultSet rs) throws SQLException {
            while (rs.next()) {
                this.rch.processRow(rs);
            }
            return null;
        }
    }
}