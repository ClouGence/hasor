/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.jdbc.template.core;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import net.hasor.jdbc.template.ResultSetExtractor;
import net.hasor.jdbc.template.SqlRowSet;
import net.hasor.jdbc.template.core.rowset.ResultSetWrappingSqlRowSet;
import com.sun.rowset.CachedRowSetImpl;
/**
 * 接口 {@link ResultSetExtractor} 实现类，返回一个 SqlRowSet 数据集对象。
 * 
 * <p>默认实现使用 JDBC 标准  CachedRowSet.需要保证在运行时
 * <code>com.sun.rowset.CachedRowSetImpl</code> 类是可用的。
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see #newCachedRowSet
 * @see javax.sql.rowset.CachedRowSet
 */
public class SqlRowSetResultSetExtractor implements ResultSetExtractor<SqlRowSet> {
    public SqlRowSet extractData(ResultSet rs) throws SQLException {
        return createSqlRowSet(rs);
    }
    /**
     * Create a SqlRowSet that wraps the given ResultSet,
     * representing its data in a disconnected fashion.
     * <p>This implementation creates a Spring ResultSetWrappingSqlRowSet
     * instance that wraps a standard JDBC CachedRowSet instance.
     * Can be overridden to use a different implementation.
     * @param rs the original ResultSet (connected)
     * @return the disconnected SqlRowSet
     * @throws SQLException if thrown by JDBC methods
     * @see #newCachedRowSet
     * @see org.noe.platform.modules.db.jdbcorm.jdbc.support.rowset.ResultSetWrappingSqlRowSet
     */
    protected SqlRowSet createSqlRowSet(ResultSet rs) throws SQLException {
        CachedRowSet rowSet = newCachedRowSet();
        rowSet.populate(rs);
        return new ResultSetWrappingSqlRowSet(rowSet);
    }
    /**
     * Create a new CachedRowSet instance, to be populated by
     * the <code>createSqlRowSet</code> implementation.
     * <p>The default implementation creates a new instance of
     * Sun's <code>com.sun.rowset.CachedRowSetImpl</code> class.
     * @return a new CachedRowSet instance
     * @throws SQLException if thrown by JDBC methods
     * @see #createSqlRowSet
     * @see com.sun.rowset.CachedRowSetImpl
     */
    protected CachedRowSet newCachedRowSet() throws SQLException {
        return new CachedRowSetImpl();
    }
}