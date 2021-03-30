package net.hasor.db.lambda;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.query.LambdaDeleteWrapper;
import net.hasor.db.lambda.query.LambdaInsertWrapper;
import net.hasor.db.lambda.query.LambdaQueryWrapper;
import net.hasor.db.lambda.query.LambdaUpdateWrapper;
import net.hasor.db.mapping.MappingHandler;

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
    public LambdaTemplate(final DataSource dataSource, MappingHandler mappingHandler) {
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
    public LambdaTemplate(final Connection conn, MappingHandler mappingHandler) {
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
