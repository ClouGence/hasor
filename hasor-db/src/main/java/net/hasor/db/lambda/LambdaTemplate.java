/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.lambda;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.query.LambdaDeleteWrapper;
import net.hasor.db.lambda.query.LambdaInsertWrapper;
import net.hasor.db.lambda.query.LambdaQueryWrapper;
import net.hasor.db.lambda.query.LambdaUpdateWrapper;
import net.hasor.db.mapping.MappingRegistry;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaTemplate implements LambdaOperations {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Construct a new LambdaTemplate for bean usage.
     * <p>Note: The DataSource has to be set before using the instance.
     */
    public LambdaTemplate() {
        this.jdbcTemplate = new JdbcTemplate();
    }

    /**
     * Construct a new LambdaTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public LambdaTemplate(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Construct a new LambdaTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     * @param mappingHandler the Types
     */
    public LambdaTemplate(final DataSource dataSource, MappingRegistry mappingHandler) {
        this.jdbcTemplate = new JdbcTemplate(dataSource, mappingHandler);
    }

    /**
     * Construct a new LambdaTemplate, given a Connection to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param conn the JDBC Connection
     */
    public LambdaTemplate(final Connection conn) {
        this.jdbcTemplate = new JdbcTemplate(conn);
    }

    /**
     * Construct a new LambdaTemplate, given a Connection to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param conn the JDBC Connection
     * @param mappingHandler the Types
     */
    public LambdaTemplate(final Connection conn, MappingRegistry mappingHandler) {
        this.jdbcTemplate = new JdbcTemplate(conn, mappingHandler);
    }

    /**
     * Construct a new LambdaTemplate for bean usage.
     * <p>Note: The JdbcTemplate has to be set before using the instance.
     */
    public LambdaTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    @Override
    public <T> LambdaOperations.LambdaQuery<T> lambdaQuery(Class<T> exampleType) {
        return new LambdaQueryWrapper<>(exampleType, this.getJdbcTemplate());
    }

    @Override
    public <T> LambdaOperations.LambdaDelete<T> lambdaDelete(Class<T> exampleType) {
        return new LambdaDeleteWrapper<>(exampleType, this.getJdbcTemplate());
    }

    @Override
    public <T> LambdaOperations.LambdaUpdate<T> lambdaUpdate(Class<T> exampleType) {
        return new LambdaUpdateWrapper<>(exampleType, this.getJdbcTemplate());
    }

    @Override
    public <T> LambdaOperations.LambdaInsert<T> lambdaInsert(Class<T> exampleType) {
        return new LambdaInsertWrapper<>(exampleType, this.getJdbcTemplate());
    }
}
