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
package net.hasor.db.mybatis3;
import net.hasor.core.AppContext;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.ExceptionUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;
/**
 *
 */
class InnerSqlMapperProxy<T> implements Supplier<T>, InvocationHandler {
    private Class<?>             mapperType   = null;
    private Object               mapperObject = null;
    private Supplier<AppContext> appContext;
    public InnerSqlMapperProxy(Class<?> mapperType, Supplier<AppContext> provider) {
        this.mapperType = Objects.requireNonNull(mapperType, "mapperType is null.");
        this.appContext = provider;
    }
    //
    @Override
    public T get() {
        if (this.mapperObject != null) {
            return (T) this.mapperObject;
        }
        try {
            this.mapperObject = Proxy.newProxyInstance(this.appContext.get().getClassLoader(), new Class[] { this.mapperType }, this);
            return (T) this.mapperObject;
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return mapperType.toString();
        }
        //
        JdbcTemplate jdbcTemplate = appContext.get().getInstance(JdbcTemplate.class);
        SqlSessionFactory factory = appContext.get().getInstance(SqlSessionFactory.class);
        return jdbcTemplate.execute((ConnectionCallback<Object>) con -> {
            try (SqlSession sqlSession = factory.openSession(con)) {
                Object mapper = sqlSession.getMapper(mapperType);
                return method.invoke(mapper, args);
            } catch (InvocationTargetException e) {
                throw new SQLException(e.getTargetException());
            } catch (IllegalAccessException e) {
                throw new SQLException(e);
            }
        });
    }
}