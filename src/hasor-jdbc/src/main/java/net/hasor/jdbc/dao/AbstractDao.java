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
/**
 * Generic base class for DAOs, defining template methods for DAO initialization.
 *
 * <p>Extended by Spring's specific DAO support classes, such as:
 * JdbcDaoSupport, JdoDaoSupport, etc.
 *
 * @author Juergen Hoeller
 * @since 1.2.2
 */
public abstract class AbstractDao {
    /**
     * Concrete subclasses can override this for custom initialization behavior.
     * Gets called after population of this instance's bean properties.
     * @throws Exception if DAO initialization fails
     * (will be rethrown as a BeanInitializationException)
     * @see org.noe.platform.modules.db.jdbcorm.beans.factory.BeanInitializationException
     */
    protected void initDao() throws Exception {}
}