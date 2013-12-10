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
package net.hasor.plugins.dao.simple;
import java.sql.Connection;
import javax.sql.DataSource;
import net.hasor.jdbc.core.JdbcTemplate;
import net.hasor.jdbc.datasource.DataSourceUtils;
/**
 * Convenient super class for JDBC-based data access objects.
 * @author Juergen Hoeller (by 28.07.2003)
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-8
 */
public abstract class JdbcDaoSupport {
    private JdbcTemplate jdbcTemplate = null;
    /**
     * 从当前事务中获取一个新的数据库连接。
     * @return the JDBC Connection
     * @throws CannotGetJdbcConnectionException if the attempt to get a Connection failed
     * @see net.hasor.jdbc.datasource.DataSourceUtils#getConnection(javax.sql.DataSource)
     */
    protected final Connection getConnection() {
        return DataSourceUtils.getConnection(getDataSource());
    }
    /**
     * Close the given JDBC Connection, created via this DAO's DataSource, if it isn't bound to the thread.
     * @param con Connection to close
     * @see net.hasor.jdbc.datasource.DataSourceUtils#releaseConnection(Connection, DataSource)
     */
    protected final void releaseConnection(Connection con) {
        DataSourceUtils.releaseConnection(con, getDataSource());
    }
    /**Set the JdbcTemplate for this DAO explicitly, as an alternative to specifying a DataSource.*/
    public final void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initTemplateConfig();
    }
    /**Return the JdbcTemplate for this DAO, pre-initialized with the DataSource or set explicitly.*/
    public final JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    /**Set the JDBC DataSource to be used by this DAO.*/
    public final void setDataSource(DataSource dataSource) {
        if (this.jdbcTemplate == null || dataSource != this.jdbcTemplate.getDataSource()) {
            this.jdbcTemplate = createJdbcTemplate(dataSource);
            initTemplateConfig();
        }
    }
    /**Return the JDBC DataSource used by this DAO.*/
    public final DataSource getDataSource() {
        return (this.jdbcTemplate != null ? this.jdbcTemplate.getDataSource() : null);
    }
    /**
     * Create a JdbcTemplate for the given DataSource. Only invoked if populating the DAO with a DataSource reference!
     * <p>Can be overridden in subclasses to provide a JdbcTemplate instance with different configuration, or a custom JdbcTemplate subclass.
     * @param dataSource the JDBC DataSource to create a JdbcTemplate for
     * @return the new JdbcTemplate instance
     * @see #setDataSource
     */
    protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    /**
     * Initialize the template-based configuration of this DAO. Called after a new JdbcTemplate has been set, either directly or through a DataSource.
     * <p>This implementation is empty. Subclasses may override this to configure further objects based on the JdbcTemplate.
     * @see #getJdbcTemplate()
     */
    protected void initTemplateConfig() {}
    //
    //    /**依照表名创建一个查询。*/
    //    public QueryState createQueryState(String tableName) {
    //        return new QueryState(tableName, this.getJdbcTemplate());
    //    }
    //    /**依据 Map 内容作为条件参数删除数据*/
    //    public int deleteByMap(String tableName, Map<String, Object> whereData) {
    //        QueryState query = this.createQueryState(tableName);
    //        for (String key : whereData.keySet()) {
    //            Object valueOri = whereData.get(key);
    //            if (valueOri.getClass().isArray()) {
    //                Object[] values = (Object[]) valueOri;
    //                query.addConditions(key, values, ConditionEnum.EQ);
    //            } else {
    //                query.addCondition(key, valueOri, ConditionEnum.EQ);
    //            }
    //        }
    //        return query.doDelete();
    //    }
    //    public void insertByMap(String tableName, Map<String, Object> schemaData) {
    //        this.getJdbcTemplate().insertMap(tableName, schemaData);
    //    }
}