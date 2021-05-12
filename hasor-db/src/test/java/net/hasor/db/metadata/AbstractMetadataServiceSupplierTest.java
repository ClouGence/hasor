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
package net.hasor.db.metadata;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.metadata.provider.AbstractMetadataProvider;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractMetadataServiceSupplierTest<T extends AbstractMetadataProvider> {
    private static final Logger     logger = LoggerFactory.getLogger(AbstractMetadataServiceSupplierTest.class);
    protected            Connection connection;
    protected            T          repository;

    protected abstract T initRepository(Connection con);

    protected abstract Connection initConnection() throws SQLException;

    protected abstract void beforeTest(JdbcTemplate jdbcTemplate, T repository) throws SQLException, IOException;

    @Before
    public final void beforeTest() throws SQLException, IOException {
        this.connection = this.initConnection();
        this.repository = this.initRepository(this.connection);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.connection);
        beforeTest(jdbcTemplate, this.repository);
    }

    @After
    public void afterTest() throws SQLException {
        this.connection.close();
    }

    protected void applySql(String sqlScript) {
        try {
            new JdbcTemplate(this.connection).execute(sqlScript);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}