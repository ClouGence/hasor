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
package net.hasor.plugins.jfinal;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.transaction.TranManager;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/**
 * Jfinal DB 提供和 Spring 相同的 数据库事务管理器
 *
 * @version : 2016-11-03
 * @author 赵永春 (zyc@byshell.org)
 */
public class HasorDataSourceProxy implements IDataSourceProvider, Module {
    private String     name;
    private DataSource dataSourceProxy;
    //
    public HasorDataSourceProxy(IDataSourceProvider iDataSourceProvider) {
        this(null, iDataSourceProvider);
    }
    public HasorDataSourceProxy(String name, final IDataSourceProvider iDataSourceProvider) {
        this.name = name;
        this.dataSourceProxy = newProxyDataSource(iDataSourceProvider);
    }
    @Override
    public DataSource getDataSource() {
        return this.dataSourceProxy;
    }
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.installModule(new JdbcModule(Level.Full, this.name, this.dataSourceProxy));
    }
    //
    /** 代理DataSource */
    private DataSource newProxyDataSource(IDataSourceProvider iDataSourceProvider) {
        DataSourceInvocationHandler handler = new DataSourceInvocationHandler(iDataSourceProvider);
        return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class[] { DataSource.class }, handler);
    }
    private class DataSourceInvocationHandler implements InvocationHandler {
        private IDataSourceProvider iDataSourceProvider;
        public DataSourceInvocationHandler(IDataSourceProvider iDataSourceProvider) {
            this.iDataSourceProvider = iDataSourceProvider;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getConnection")) {
                return TranManager.currentConnection(this.iDataSourceProvider.getDataSource());
            }
            try {
                return method.invoke(this.iDataSourceProvider.getDataSource(), args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}