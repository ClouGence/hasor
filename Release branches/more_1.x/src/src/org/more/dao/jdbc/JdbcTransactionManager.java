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
package org.more.dao.jdbc;
import java.sql.Connection;
import javax.sql.DataSource;
import org.more.CastException;
import org.more.core.classcode.AOPFilterChain;
import org.more.core.classcode.AOPInvokeFilter;
import org.more.core.classcode.AOPMethods;
/**
 * JobSupport的事务控制器。
 * @version 2009-12-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class JdbcTransactionManager implements AOPInvokeFilter {
    //========================================================================================Field
    private DataSource           dataSource;
    private boolean              autoCommit = false;
    private ThreadLocal<Boolean> running    = new ThreadLocal<Boolean>(); //当业务层出现嵌套事务管理时该对象确保以顶层的事务开始到结束为一个原子。
    //==========================================================================================Job
    public boolean isAutoCommit() {
        return autoCommit;
    }
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public Object doFilter(Object target, AOPMethods methods, Object[] args, AOPFilterChain chain) throws Throwable {
        String mn = methods.getPropxyMethod().getName();
        if (mn.equals("getDataSource") == true || mn.equals("setDataSource") == true || //
                mn.equals("getJdbcDaoSupport") == true || mn.equals("setJdbcDaoSupport") == true)
            return chain.doInvokeFilter(target, methods, args);
        //----------
        if (target instanceof JobSupport == false)
            throw new CastException("类型" + target.getClass() + "没有继承JobSupport父类无法实现事务控制。");
        JobSupport ds = (JobSupport) target;
        //
        Boolean isRunning = this.running.get();
        if (isRunning == null || isRunning == false) {
            this.running.set(true);
            /*---------------------------*/
            if (ds.getDataSource() == null)
                ds.setDataSource(dataSource);
            Connection conn = null;
            if (ds.getJdbcDaoSupport() == null) {
                conn = this.dataSource.getConnection();
                ds.setJdbcDaoSupport(new JdbcDaoSupport(conn));
            } else
                conn = ds.getJdbcDaoSupport().connection;
            //
            try {
                this.begin(conn);
                Object obj = chain.doInvokeFilter(target, methods, args);
                this.commit(conn);
                return obj;
            } catch (Exception e) {
                this.rollBack(conn);
                throw e;
            } finally {
                ds.setJdbcDaoSupport(null);
                this.running.remove();
            }
            /*---------------------------*/
        } else
            return chain.doInvokeFilter(target, methods, args);
    }
    //==========================================================================================Job
    private void begin(Connection conn) throws Throwable {
        if (autoCommit == false)
            conn.setAutoCommit(false);
    };
    private void commit(Connection conn) throws Throwable {
        if (autoCommit == false)
            conn.commit();
    };
    private void rollBack(Connection conn) throws Throwable {
        if (autoCommit == false)
            conn.rollback();
    };
}