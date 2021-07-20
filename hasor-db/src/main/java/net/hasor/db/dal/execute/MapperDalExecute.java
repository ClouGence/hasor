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
package net.hasor.db.dal.execute;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.rule.RuleRegistry;
import net.hasor.db.dal.repository.MapperRegistry;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 执行器入口
 * @version : 2021-07-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class MapperDalExecute extends DalExecute {
    private final String         namespace;
    private final MapperRegistry mapperRegistry;
    private final RuleRegistry   ruleRegistry;
    private final ClassLoader    classLoader;

    public MapperDalExecute(String namespace) {
        this(namespace, null, null, null);
    }

    public MapperDalExecute(String namespace, MapperRegistry mapperRegistry, RuleRegistry ruleRegistry) {
        this(namespace, mapperRegistry, ruleRegistry, null);
    }

    public MapperDalExecute(String namespace, MapperRegistry mapperRegistry, RuleRegistry ruleRegistry, ClassLoader classLoader) {
        this.namespace = StringUtils.isBlank(namespace) ? "" : namespace.trim();
        this.mapperRegistry = (mapperRegistry != null) ? mapperRegistry : MapperRegistry.DEFAULT;
        this.ruleRegistry = (ruleRegistry != null) ? ruleRegistry : RuleRegistry.DEFAULT;
        this.classLoader = (classLoader != null) ? classLoader : Thread.currentThread().getContextClassLoader();
    }

    public Object execute(Connection connection, String dynamicId, Map<String, Object> context) throws SQLException {
        BuilderContext builderContext = new BuilderContext(this.namespace, context, this.ruleRegistry, this.mapperRegistry, this.classLoader);
        DynamicSql dynamicSql = mapperRegistry.findDynamicSql(this.namespace, dynamicId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        //
        return execute(jdbcTemplate, dynamicSql, builderContext);
    }

    public Object execute(DataSource dataSource, String dynamicId, Map<String, Object> context) throws SQLException {
        BuilderContext builderContext = new BuilderContext(this.namespace, context, this.ruleRegistry, this.mapperRegistry, this.classLoader);
        DynamicSql dynamicSql = mapperRegistry.findDynamicSql(this.namespace, dynamicId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //
        return execute(jdbcTemplate, dynamicSql, builderContext);
    }

    public Object execute(JdbcTemplate jdbcTemplate, String dynamicId, Map<String, Object> context) throws SQLException {
        BuilderContext builderContext = new BuilderContext(this.namespace, context, this.ruleRegistry, this.mapperRegistry, this.classLoader);
        DynamicSql dynamicSql = mapperRegistry.findDynamicSql(this.namespace, dynamicId);
        //
        return execute(jdbcTemplate, dynamicSql, builderContext);
    }
}
